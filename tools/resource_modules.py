"""Compose internal module sources into the unchanged Minecraft resource namespace."""
import json
import os
import shutil
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RESOURCES = ROOT / 'src/main/resources'
MODULES = ('awnings', 'backpacks', 'balls', 'books_notebooks_and_folders', 'building_blocks', 'chairs', 'facilities', 'lighting', 'lockers_and_storage', 'music_discs', 'musical_instruments', 'npcs', 'painting_and_editable_blocks', 'school_supplies', 'shelving', 'slopes', 'special_blocks', 'tables_and_desks', 'technology', 'uniforms', 'wall_blocks', 'windows_and_enclosures')
_snapshot = None


def roots():
    return (RESOURCES, *(RESOURCES / name for name in MODULES), ROOT / 'src/generated/resources')


def source_path(relative):
    """Find one logical resource; dictionaries must be read from a composed snapshot."""
    matches = [base / relative for base in roots() if (base / relative).is_file()]
    if len(matches) != 1:
        raise ValueError(f'Expected one source for {relative}, found {len(matches)}')
    return matches[0]


def compose(output, *, skip_directories=()):
    output = Path(output)
    skip_directories = set(skip_directories)
    directories = set()
    for base in roots():
        if not base.exists():
            continue
        for path in _resource_files(base, skip_directories):
            relative = path.relative_to(base)
            destination = output / relative
            if destination.parent not in directories:
                destination.parent.mkdir(parents=True, exist_ok=True)
                directories.add(destination.parent)
            if destination.exists():
                # Shared JSON dictionaries and tags are merged, never silently overwritten.
                if relative.suffix != '.json':
                    raise ValueError(f'Duplicate resource: {relative}')
                left = json.loads(destination.read_text(encoding='utf-8-sig'))
                right = json.loads(path.read_text(encoding='utf-8-sig'))
                if 'tags' in relative.parts:
                    if left.get('replace', False) or right.get('replace', False):
                        raise ValueError(f'Ambiguous replacing tag fragments: {relative}')
                    left['values'] = list(dict.fromkeys(json.dumps(v, sort_keys=True) for v in left['values'] + right['values']))
                    left['values'] = [json.loads(v) for v in left['values']]
                elif relative.name == 'sounds.json' or 'lang' in relative.parts:
                    overlap = left.keys() & right.keys()
                    if overlap:
                        raise ValueError(f'Duplicate definitions in {relative}: {overlap}')
                    left.update(right)
                else:
                    raise ValueError(f'Duplicate resource: {relative}')
                destination.write_text(json.dumps(left, ensure_ascii=False, indent=2) + '\n')
            else:
                shutil.copyfile(path, destination)


def _resource_files(base, skip_directories):
    """Prune module roots before descent instead of visiting every source twice."""
    for directory, names, files in os.walk(base):
        parent = Path(directory)
        names[:] = sorted(name for name in names if name != '.cache'
                          and not (parent == RESOURCES and name in MODULES)
                          and (parent / name).relative_to(base) not in skip_directories)
        for name in sorted(files):
            if name not in ('.gitkeep', '.cache') and not name.endswith('.bbmodel'):
                path = parent / name
                if path.is_file():
                    yield path


def source_assets():
    """Read-only tooling view, including merged sounds, translations and tags."""
    global _snapshot
    if _snapshot is None:
        _snapshot = tempfile.TemporaryDirectory(prefix='lsmmod-source-')
        compose(Path(_snapshot.name))
    return Path(_snapshot.name) / 'assets/lsmmod'


_LAYOUT = json.loads((ROOT / 'tools/module_layout.json').read_text())
_JAVA = ROOT / 'src/main/java/net/nicomar2009/lsmmod'
_index = None
_registry_ref_owners = {}


def _normalize(path):
    path = Path(path)
    return path if path.is_absolute() else ROOT / path


def _resource_relative(path):
    try:
        relative = _normalize(path).relative_to(RESOURCES)
    except (ValueError, TypeError):
        return None
    if relative.parts and relative.parts[0] in MODULES:
        relative = Path(*relative.parts[1:])
    return relative


def _sources(relative):
    global _index
    if _index is None:
        _index = {}
        for base in roots():
            if not base.exists():
                continue
            for path in base.rglob('*'):
                if not path.is_file():
                    continue
                rel = path.relative_to(base)
                if base == RESOURCES and rel.parts[0] in MODULES:
                    continue
                _index.setdefault(rel.as_posix(), []).append(path)
    return _index.get(Path(relative).as_posix(), [])


def _owner(relative):
    key = Path(relative).as_posix()
    if key in _LAYOUT['resource_overrides']:
        return _LAYOUT['resource_overrides'][key]
    if key.startswith('assets/lsmmod/sounds/'):
        return 'technology'
    parts = Path(relative).parts
    if key.startswith(('assets/lsmmod/models/', 'assets/lsmmod/textures/')):
        name = '/'.join(parts[4:]).rsplit('.', 1)[0]
    else:
        name = Path(relative).stem
    for prefix, owner in sorted({**_LAYOUT['blocks'], **_LAYOUT['prefixes']}.items(), key=lambda item: -len(item[0])):
        if name == prefix or name.startswith((prefix + '_', prefix + '/', prefix + '.')):
            return owner
    return None


def _java_path(path):
    path = _normalize(path)
    try:
        rel = path.relative_to(_JAVA).as_posix()
    except ValueError:
        return path
    return _JAVA / _LAYOUT['classes'].get(rel, rel)


def module_path(path):
    """Resolve a source file; external files and prepared output are passed through."""
    if not isinstance(path, (str, Path)):
        return path
    path = _normalize(path)
    relative = _resource_relative(path)
    if relative is None:
        return _java_path(path)
    matches = _sources(relative)
    if path in matches:
        return path
    if len(matches) == 1:
        return matches[0]
    return path


def output_path(path):
    """Resolve physical output ownership, including resources first created by a generator."""
    if not isinstance(path, (str, Path)):
        return path
    path = _normalize(path)
    relative = _resource_relative(path)
    if relative is None:
        destination = _java_path(path)
    else:
        matches = _sources(relative)
        if len(matches) == 1:
            destination = matches[0]
        elif len(matches) > 1:
            raise ValueError(f'Use write_resource for shared dictionaries: {relative}')
        else:
            owner = _owner(relative)
            destination = RESOURCES / owner / relative if owner else path
            _sources(relative)
            _index[relative.as_posix()] = [destination]
    destination.parent.mkdir(parents=True, exist_ok=True)
    return destination


def _shared(relative):
    return relative is not None and (relative.name == 'sounds.json' or 'lang' in relative.parts or 'tags' in relative.parts)


def module_read_text(path, *args, **kwargs):
    path = _normalize(path)
    if _is_registry_catalog(path):
        return _registry_catalog(path)
    relative = _resource_relative(path)
    matches = _sources(relative) if relative is not None else []
    if _shared(relative) and len(matches) > 1:
        merged = {}
        for source in matches:
            value = json.loads(source.read_text(*args, **kwargs))
            if 'tags' in relative.parts:
                values = merged.get('values', []) + value.get('values', [])
                merged.update(value)
                merged['values'] = values
            else:
                overlap = merged.keys() & value.keys()
                if overlap:
                    raise ValueError(f'Duplicate dictionary keys: {relative}: {overlap}')
                merged.update(value)
        return json.dumps(merged, ensure_ascii=False)
    return module_path(path).read_text(*args, **kwargs)


def _field_end(text, start):
    depth = 0
    quoted = escaped = False
    for index in range(start, len(text)):
        char = text[index]
        if quoted:
            if escaped:
                escaped = False
            elif char == '\\':
                escaped = True
            elif char == '"':
                quoted = False
        elif char == '"':
            quoted = True
        elif char in '([{':
            depth += 1
        elif char in ')]}':
            depth -= 1
        elif char == ';' and depth == 0:
            return index + 1
    raise ValueError('Unterminated Java field')


def _write_java(path, text, *args, **kwargs):
    import re
    path = _java_path(path)
    if _is_registry_catalog(path):
        fields = _registry_fields(path.name)
        updates = {}
        for match in re.finditer(r'    public static final ([^=;\n]+?)\s+(\w+)\s*=\s*', text):
            name = match[2]
            if name not in fields:
                raise ValueError(f'Add new registrations in their owning module: {path.name}: {name}')
            owner, original = fields[name]
            end = _field_end(text, match.end())
            declaration = text[match.start():end]
            expression = text[match.end():end - 1].strip()
            alias = 'net.nicomar2009.lsmmod.' + owner.relative_to(_JAVA).with_suffix('').as_posix().replace('/', '.') + '.' + name
            if declaration == original or expression == alias:
                continue
            updates[name] = (owner, declaration)
        for name, (owner, declaration) in updates.items():
            source = owner.read_text()
            field = re.search(r'    public static final [^=;\n]+?\s+' + re.escape(name) + r'\s*=\s*', source)
            end = _field_end(source, field.end())
            declaration = _qualify_registry_refs(declaration)
            updated = source[:field.start()] + declaration + source[end:]
            if updated != source:
                owner.write_text(updated)
        # Catalogs are transient tooling views; central bootstrap files never gain content fields.
        return len(text)
    text = _qualify_registry_refs(text)
    if _JAVA in path.parents and 'modules' in path.relative_to(_JAVA).parts:
        package = 'net.nicomar2009.lsmmod.' + path.parent.relative_to(_JAVA).as_posix().replace('/', '.')
        text = re.sub(r'^package [^;]+;', 'package ' + package + ';', text, count=1)
        # Generated classes historically used implicit peers in the old block package.
        if 'import net.nicomar2009.lsmmod.block.*;' not in text:
            text = text.replace(';', ';\nimport net.nicomar2009.lsmmod.block.*;', 1)
        for old, new in _LAYOUT['classes'].items():
            fqn = 'net.nicomar2009.lsmmod.' + new[:-5].replace('/', '.')
            name = Path(old).stem
            if re.search(r'\b' + name + r'\b', text) and name != path.stem and fqn.rsplit('.', 1)[0] != package and f'import {fqn};' not in text:
                text = text.replace(';', ';\nimport ' + fqn + ';', 1)
    path.parent.mkdir(parents=True, exist_ok=True)
    return path.write_text(text, *args, **kwargs)


def write_resource(path, data):
    from resource_json import resource_json
    relative = _resource_relative(path)
    if relative is None or not _shared(relative):
        destination = output_path(path)
        destination.write_text(resource_json(destination, data) + '\n', encoding='utf-8')
        return
    sources = _sources(relative)
    owners = {}
    for source in sources:
        value = json.loads(source.read_text())
        for key in value.get('values', []) if 'tags' in relative.parts else value:
            owners[json.dumps(key, sort_keys=True) if 'tags' in relative.parts else key] = source
    fragments = {}
    if 'tags' in relative.parts:
        for value in data.get('values', []):
            key = json.dumps(value, sort_keys=True)
            ident = value if isinstance(value, str) else value.get('id', '')
            owner = _LAYOUT['blocks'].get(ident.removeprefix('lsmmod:')) if ident.startswith('lsmmod:') else None
            destination = owners.get(key) or (RESOURCES / owner / relative if owner else RESOURCES / relative)
            fragments.setdefault(destination, {**data, 'values': []})['values'].append(value)
    else:
        for key, value in data.items():
            ident = key.split('.lsmmod.', 1)[-1].split('.')[0]
            owner = _LAYOUT['blocks'].get(ident)
            destination = owners.get(key) or (RESOURCES / owner / relative if owner else RESOURCES / relative)
            fragments.setdefault(destination, {})[key] = value
    for destination, fragment in fragments.items():
        destination.parent.mkdir(parents=True, exist_ok=True)
        if destination.exists():
            current = json.loads(destination.read_text())
            if 'tags' not in relative.parts:
                fragment = {**current, **fragment}
            else:
                fragment['values'] = [json.loads(v) for v in dict.fromkeys(json.dumps(v, sort_keys=True) for v in current.get('values', []) + fragment['values'])]
        destination.write_text(resource_json(destination, fragment) + '\n', encoding='utf-8')
    # A partial generator write updates its entries without erasing unrelated fragments.
    for source in sources:
        if source not in fragments:
            fragments[source] = json.loads(source.read_text())
    _index[relative.as_posix()] = list(fragments)


def module_write_text(path, text, *args, **kwargs):
    path = Path(path)
    if path.suffix == '.java':
        return _write_java(path, text, *args, **kwargs)
    relative = _resource_relative(path)
    if _shared(relative):
        write_resource(path, json.loads(text))
        return len(text)
    return output_path(path).write_text(text, *args, **kwargs)


def module_read_bytes(path):
    relative = _resource_relative(path)
    if _shared(relative):
        return module_read_text(path, encoding='utf-8').encode('utf-8')
    return module_path(path).read_bytes()


def module_write_bytes(path, data):
    return output_path(path).write_bytes(data)


def module_exists(path):
    relative = _resource_relative(path)
    return bool(_sources(relative)) if relative is not None and _sources(relative) else module_path(path).exists()


def module_is_file(path):
    return module_exists(path) and (bool(_sources(_resource_relative(path))) if _resource_relative(path) is not None else module_path(path).is_file())


def module_stat(path, *args, **kwargs):
    return module_path(path).stat(*args, **kwargs)


def module_unlink(path, *args, **kwargs):
    actual = module_path(path)
    relative = _resource_relative(path)
    result = actual.unlink(*args, **kwargs)
    if relative is not None and _index is not None:
        _index[relative.as_posix()] = [p for p in _sources(relative) if p != actual]
    return result


def module_glob(path, pattern, *, recursive=False):
    path = _normalize(path)
    relative = _resource_relative(path)
    if relative is None:
        yield from (path.rglob(pattern) if recursive else path.glob(pattern))
        return
    found = set()
    for base in roots():
        directory = base / relative
        for candidate in directory.rglob(pattern) if recursive else directory.glob(pattern):
            suffix = candidate.relative_to(directory)
            if suffix not in found:
                found.add(suffix)
                yield path / suffix


def module_rglob(path, pattern):
    return module_glob(path, pattern, recursive=True)


def module_registry_text(path, *args, **kwargs):
    """Static checks inspect concrete registrations in their owning modules."""
    return module_read_text(path, *args, **kwargs)


def _is_registry_catalog(path):
    return path.parent == _JAVA / 'registry' and path.name in _LAYOUT['registry_sections']


def _registry_fields(filename):
    import re
    fields = {}
    for owner in sorted((_JAVA / 'modules').glob('*/registry/Module*' + filename[3:])):
        allowed = {'Module' + filename[3:]}
        if filename == 'ModItems.java':
            allowed.add('ModuleCurtainItems.java')
        if owner.name not in allowed:
            continue
        source = owner.read_text()
        for match in re.finditer(r'    public static final [^=;\n]+?\s+(\w+)\s*=\s*', source):
            name = match[1]
            if name in fields:
                raise ValueError(f'Duplicate registry constant: {filename}: {name}')
            fields[name] = (owner, source[match.start():_field_end(source, match.end())])
    return fields


def _registry_catalog(path):
    """Adapt legacy generator section APIs without storing a second registry catalog."""
    sections = '\n'.join('    ' + marker for marker in _LAYOUT['registry_sections'][path.name])
    fields = '\n\n'.join(declaration for owner, declaration in _registry_fields(path.name).values())
    return ('package net.nicomar2009.lsmmod.registry;\npublic final class ' + path.stem + ' {\n'
            + sections + '\n' + fields + '\n    private ' + path.stem + '() {}\n}\n')


def _qualify_registry_refs(text):
    import re
    kinds = {'ModBlocks': 'BLOCKS', 'ModItems': 'ITEMS', 'ModBlockEntities': 'BLOCK_ENTITIES',
             'ModEntities': 'ENTITIES', 'ModSounds': 'SOUNDS'}
    def target(match):
        kind, name = match.group(1), match.group(2)
        if name == kinds[kind]:
            return 'net.nicomar2009.lsmmod.registry.ModRegistries.' + name
        if kind == 'ModSounds' and name == 'SpeakerTrack':
            return 'net.nicomar2009.lsmmod.modules.technology.registry.ModuleSounds.SpeakerTrack'
        if name in ('awningVariant', 'awningEntityBlocks'):
            return 'net.nicomar2009.lsmmod.modules.awnings.registry.Module' + kind[3:] + '.' + name
        if kind not in _registry_ref_owners:
            _registry_ref_owners[kind] = {key: value[0] for key, value in _registry_fields(kind + '.java').items()}
        if name not in _registry_ref_owners[kind]:
            return match[0]
        owner = _registry_ref_owners[kind][name]
        return 'net.nicomar2009.lsmmod.' + owner.relative_to(_JAVA).with_suffix('').as_posix().replace('/', '.') + '.' + name
    return re.sub(r'(?<![\w.])(?:net\.nicomar2009\.lsmmod\.registry\.)?(ModBlocks|ModItems|ModBlockEntities|ModEntities|ModSounds)\.(\w+)', target, text)
