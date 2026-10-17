"""Compose internal module sources into the unchanged Minecraft resource namespace."""
import json
import shutil
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RESOURCES = ROOT / 'src/main/resources'
# Only migrated modules participate; scaffolds remain deliberately unopened.
MODULES = ('npcs', 'balls', 'school_supplies', 'uniforms', 'music_discs',
           'musical_instruments', 'books_notebooks_and_folders')
_snapshot = None


def roots():
    return (RESOURCES, *(RESOURCES / name for name in MODULES), ROOT / 'src/generated/resources')


def source_path(relative):
    """Find one logical resource; dictionaries must be read from a composed snapshot."""
    matches = [base / relative for base in roots() if (base / relative).is_file()]
    if len(matches) != 1:
        raise ValueError(f'Expected one source for {relative}, found {len(matches)}')
    return matches[0]


def compose(output):
    output = Path(output)
    for base in roots():
        if not base.exists():
            continue
        for path in sorted(base.rglob('*')):
            if not path.is_file() or path.suffix == '.bbmodel' or path.name == '.gitkeep' or '.cache' in path.parts:
                continue
            relative = path.relative_to(base)
            if base == RESOURCES and relative.parts[0] in MODULES:
                continue
            destination = output / relative
            destination.parent.mkdir(parents=True, exist_ok=True)
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


def source_assets():
    """Read-only tooling view, including merged sounds, translations and tags."""
    global _snapshot
    if _snapshot is None:
        _snapshot = tempfile.TemporaryDirectory(prefix='lsmmod-source-')
        compose(Path(_snapshot.name))
    return Path(_snapshot.name) / 'assets/lsmmod'


def output_path(path):
    """Keep mixed generators writing existing resources in their owning module."""
    path = Path(path)
    try:
        relative = path.relative_to(RESOURCES)
    except ValueError:
        return path
    if relative.parts[0] in MODULES:
        return path
    matches = [RESOURCES / name / relative for name in MODULES if (RESOURCES / name / relative).is_file()]
    if path.exists() or not matches:
        return path
    if len(matches) != 1:
        raise ValueError(f'Use write_resource for shared dictionaries: {relative}')
    return matches[0]


def write_resource(path, data):
    """Route generated dictionaries to their existing owners without duplicate keys."""
    from resource_json import resource_json
    path = Path(path)
    relative = path.relative_to(RESOURCES)
    fragments = {}
    if relative.name == 'sounds.json' or 'lang' in relative.parts:
        for name in MODULES:
            candidate = RESOURCES / name / relative
            if candidate.exists():
                current = json.loads(candidate.read_text())
                selected = {key: data.pop(key) for key in list(data) if key in current}
                if selected:
                    fragments[candidate] = {**current, **selected}
    fragments[output_path(path)] = data
    for destination, value in fragments.items():
        destination.parent.mkdir(parents=True, exist_ok=True)
        destination.write_text(resource_json(destination, value) + '\n', encoding='utf-8')
