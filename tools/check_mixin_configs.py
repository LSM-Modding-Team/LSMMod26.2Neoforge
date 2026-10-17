"""Reject Mixin package reservations that can block ordinary mod classes at bootstrap."""
import argparse
import json
import re
import tomllib
import zipfile
from pathlib import Path
from resource_modules import ROOT, source_path


def verify(read, names, metadata, suffix, expected=None):
    entries = tomllib.loads(metadata.replace('${mod_id}', 'lsmmod')).get('mixins', [])
    assert entries, 'No Mixin configurations declared in mod metadata'
    reserved = []
    registered = set()
    for entry in entries:
        filename = entry['config'].replace('${mod_id}', 'lsmmod')
        config = json.loads(read(filename))
        package = config['package']
        assert package.endswith('.mixin'), (filename, 'Mixin packages must be dedicated leaves')
        prefix = package.replace('.', '/') + '/'
        assert not 'net/nicomar2009/lsmmod/LSMMod.class'.startswith(prefix), filename
        assert not any(prefix.startswith(other) or other.startswith(prefix) for other in reserved), filename
        reserved.append(prefix)
        mixins = config.get('mixins', []) + config.get('client', []) + config.get('server', [])
        assert mixins, filename
        assert len(mixins) == len(set(mixins)), filename
        declared = {prefix + name.replace('.', '/') + suffix for name in mixins}
        assert not declared & registered, ('Duplicate mixins', filename)
        assert declared <= names, ('Missing mixin classes', declared - names)
        # Mixin owns every class below the package, not just the listed names.
        ordinary = {name for name in names if name.startswith(prefix) and '$' not in name} - declared
        assert not ordinary, ('Ordinary classes inside a reserved Mixin package', ordinary)
        registered.update(declared)
    if expected is not None:
        assert registered == expected, ('Unregistered mixins', expected - registered)
    return len(entries), len(registered)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jar', type=Path)
    args = parser.parse_args()
    java = ROOT / 'src/main/java'
    sources = {p.relative_to(java).as_posix(): p for p in java.rglob('*.java')}
    expected = {name for name, path in sources.items() if re.search(r'@Mixin\s*\(', path.read_text())}
    metadata = (ROOT / 'src/main/templates/META-INF/neoforge.mods.toml').read_text()
    configs, classes = verify(lambda name: source_path(name).read_text(), set(sources), metadata, '.java', expected)
    if args.jar:
        with zipfile.ZipFile(args.jar) as jar:
            packaged = {name.removesuffix('.java') + '.class' for name in expected}
            verify(lambda name: jar.read(name).decode(), set(jar.namelist()),
                   jar.read('META-INF/neoforge.mods.toml').decode(), '.class', packaged)
    print(f'OK: {classes} mixins in {configs} dedicated packages; ordinary mod classes remain loadable by package policy.'
          + (' Packaged JAR verified.' if args.jar else ''))


if __name__ == '__main__':
    main()
