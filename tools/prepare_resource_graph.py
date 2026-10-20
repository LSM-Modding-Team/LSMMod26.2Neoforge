"""Incremental, independently owned resource stages; no global model graph on startup."""
import argparse
import hashlib
import json
import os
import time
from pathlib import Path

from resource_modules import MODULES, RESOURCES, ROOT, _resource_files, roots, source_path
from model_resource_rules import compact

RECIPE_PATH = ROOT / 'tools/resource_preparation.json'
RECIPE = json.loads(RECIPE_PATH.read_text(encoding='utf-8'))
WINDOW_IDS = tuple(RECIPE['window_ids'])
HIGH_STATES = tuple(RECIPE['high_state_blocks'])
MARKER = '.lsmmod-prepared-resources'


def encoded(value):
    return (json.dumps(value, ensure_ascii=False, separators=(',', ':')) + '\n').encode('utf-8')


def digest(raw):
    return hashlib.sha256(raw).hexdigest()


def shared(relative):
    return relative.name == 'sounds.json' or 'lang' in relative.parts or 'tags' in relative.parts


def window_model(relative):
    prefix = Path('assets/lsmmod/models/block')
    if not relative.is_relative_to(prefix):
        return False
    part = relative.relative_to(prefix).parts[0]
    return part in WINDOW_IDS or part in {name + '.json' for name in WINDOW_IDS}


def derived(relative):
    return (window_model(relative)
            or relative.as_posix() in {'assets/lsmmod/projecting_window_layouts.json',
                                      'assets/lsmmod/projecting_window_baked_layouts.json'}
            or (relative.parent == Path('assets/lsmmod/blockstates') and relative.stem in HIGH_STATES))


def sources(base):
    if base.exists():
        for path in _resource_files(base, set()):
            if path.suffix == '.json':
                yield path.relative_to(base), path


def transform(relative, value):
    if relative.is_relative_to('assets/lsmmod/models'):
        if (relative.is_relative_to('assets/lsmmod/models/block') and 'loader' not in value
                and value.get('render_type') == 'minecraft:solid' and value.get('elements')):
            value = dict(value, loader='fusion:model', type='base')
    elif relative.parent == Path('assets/lsmmod/blockstates') and 'variants' in value:
        value = dict(value, variants=compact(value['variants']))
    return value


class Output:
    """One task owns one directory; manifests are local state, never packaged assets."""
    def __init__(self, directory, state, identity, code):
        self.directory = directory.resolve()
        self.state = state.resolve()
        if (self.directory == ROOT or ROOT / 'src' == self.directory
                or ROOT / 'src' in self.directory.parents or self.directory in ROOT.parents):
            raise ValueError('Prepared output cannot overwrite sources')
        if self.state.is_relative_to(self.directory) or self.state.is_relative_to(ROOT / 'src'):
            raise ValueError('State must stay outside packaged resources and sources')
        if self.directory.exists() and any(self.directory.iterdir()) and not (self.directory / MARKER).is_file():
            raise ValueError(f'Refusing to manage an unowned directory: {self.directory}')
        self.directory.mkdir(parents=True, exist_ok=True)
        (self.directory / MARKER).touch()
        signature = hashlib.sha256(identity.encode('utf-8'))
        for name in sorted(set(code)):
            signature.update(name.encode('utf-8'))
            signature.update((ROOT / name).read_bytes())
        self.recipe = signature.hexdigest()
        self.previous = {}
        try:
            saved = json.loads(self.state.read_text(encoding='utf-8'))
            if isinstance(saved, dict) and saved.get('recipe') == self.recipe and isinstance(saved.get('files'), dict):
                self.previous = saved['files']
        except (OSError, ValueError, KeyError, TypeError):
            pass
        self.files = {}
        self.written = self.reused = self.removed = 0

    def path(self, name):
        name = Path(name)
        if name.is_absolute() or '..' in name.parts or name.as_posix() == MARKER:
            raise ValueError(f'Invalid prepared resource: {name}')
        return self.directory / name

    def reuse(self, name, source_digest):
        key = Path(name).as_posix()
        saved = self.previous.get(key)
        if not isinstance(saved, dict) or saved.get('source') != source_digest:
            return False
        try:
            if digest(self.path(name).read_bytes()) != saved['output']:
                return False
        except (OSError, KeyError):
            return False
        self.files[key] = saved
        self.reused += 1
        return True

    def put(self, name, payload, source_digest=None):
        key = Path(name).as_posix()
        destination = self.path(name)
        if key in self.files:
            raise ValueError(f'Duplicate prepared resource: {key}')
        identical = False
        try:
            identical = destination.read_bytes() == payload
        except FileNotFoundError:
            pass
        if identical:
            self.reused += 1
        else:
            destination.parent.mkdir(parents=True, exist_ok=True)
            temporary = destination.with_name(destination.name + '.preparing')
            temporary.write_bytes(payload)
            os.replace(temporary, destination)
            self.written += 1
        self.files[key] = {'source': source_digest or digest(payload), 'output': digest(payload)}

    def finish(self):
        for path in self.directory.rglob('*'):
            if path.is_file() and path.name != MARKER and path.relative_to(self.directory).as_posix() not in self.files:
                path.unlink()
                self.removed += 1
        self.state.parent.mkdir(parents=True, exist_ok=True)
        temporary = self.state.with_name(self.state.name + '.preparing')
        temporary.write_bytes(encoded({'recipe': self.recipe, 'files': self.files}))
        os.replace(temporary, self.state)


def module_stage(output, module):
    base = {'common': RESOURCES, 'generated': ROOT / 'src/generated/resources'}.get(module, RESOURCES / module)
    if module not in (*MODULES, 'common', 'generated'):
        raise ValueError(f'Unknown resource module: {module}')
    for relative, path in sources(base):
        if shared(relative) or derived(relative):
            continue
        raw = path.read_bytes()
        fingerprint = digest(raw)
        if output.reuse(relative, fingerprint):
            continue
        value = transform(relative, json.loads(raw))
        output.put(relative, encoded(value), fingerprint)


def shared_stage(output):
    fragments = {}
    owners = {}
    # Check ordinary resource ownership without parsing other modules' models.
    for base in roots():
        for relative, path in sources(base):
            if shared(relative):
                fragments.setdefault(relative, []).append(path)
            elif relative in owners:
                raise ValueError(f'Duplicate resource: {relative}: {owners[relative]} and {path}')
            else:
                owners[relative] = path
    for relative, paths in fragments.items():
        raws = [path.read_bytes() for path in paths]
        fingerprint = digest(encoded([digest(raw) for raw in raws]))
        if output.reuse(relative, fingerprint):
            continue
        values = [json.loads(raw) for raw in raws]
        value = values[0]
        for fragment in values[1:]:
            if 'tags' in relative.parts:
                if value.get('replace', False) or fragment.get('replace', False):
                    raise ValueError(f'Ambiguous replacing tag fragments: {relative}')
                entries = dict.fromkeys(json.dumps(item, sort_keys=True) for item in value['values'] + fragment['values'])
                value['values'] = [json.loads(item) for item in entries]
            else:
                overlap = value.keys() & fragment.keys()
                if overlap:
                    raise ValueError(f'Duplicate definitions in {relative}: {overlap}')
                value.update(fragment)
        output.put(relative, encoded(value), fingerprint)


def window_stage(output):
    from windows_and_enclosures.create_school_projecting_window_assets import prepare_layouts
    documents = {}
    seen = set()
    assets = output.directory / 'assets/lsmmod'
    for base in roots():
        for relative, path in sources(base):
            if not window_model(relative) or 'layouts' in relative.parts:
                continue
            if relative in seen:
                raise ValueError(f'Duplicate window input: {relative}')
            seen.add(relative)
            documents[output.path(relative)] = json.loads(path.read_bytes())
    table = Path('assets/lsmmod/projecting_window_layouts.json')
    output.put(table, source_path(table).read_bytes())
    # The generator's only non-model output is the baked layout table. Keep it
    # in this task's ownership manifest even when the generator rewrites it.
    prepare_layouts(assets, documents=documents, share_geometry=True)
    baked = Path('assets/lsmmod/projecting_window_baked_layouts.json')
    output.put(baked, output.path(baked).read_bytes())
    for path, value in documents.items():
        relative = path.relative_to(output.directory)
        output.put(relative, encoded(transform(relative, value)))


def high_state_stage(output, block):
    from prepare_high_state_models import prepare
    if block not in HIGH_STATES:
        raise ValueError(f'Unknown high-state block: {block}')
    relative = Path(f'assets/lsmmod/blockstates/{block}.json')
    source = json.loads(source_path(relative).read_bytes())
    destination = output.path(relative)
    destination.parent.mkdir(parents=True, exist_ok=True)
    destination.write_bytes(encoded(dict(source, variants=compact(source['variants'])) if 'variants' in source else source))
    generated = {}
    def write_model(path, value):
        generated[path.relative_to(output.directory)] = encoded(value)
    prepare(output.directory / 'assets/lsmmod', targets=(block,), model_writer=write_model)
    output.put(relative, destination.read_bytes())
    for name, payload in generated.items():
        output.put(name, payload)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--stage', choices=('module', 'shared', 'windows', 'high-state'), required=True)
    parser.add_argument('--module', default='common')
    parser.add_argument('--block')
    parser.add_argument('--output', type=Path, required=True)
    parser.add_argument('--state', type=Path, required=True)
    args = parser.parse_args()
    code = ['tools/prepare_resource_graph.py', 'tools/resource_modules.py', 'tools/model_resource_rules.py',
            'tools/resource_preparation.json']
    if args.stage == 'windows':
        code.extend(RECIPE['window_tools'])
    elif args.stage == 'high-state':
        code.append('tools/prepare_high_state_models.py')
    start = time.perf_counter()
    identity = f'{args.stage}:{args.module}:{args.block}'
    output = Output(args.output, args.state, identity, code)
    if args.stage == 'module':
        module_stage(output, args.module)
    elif args.stage == 'shared':
        shared_stage(output)
    elif args.stage == 'windows':
        window_stage(output)
    else:
        high_state_stage(output, args.block)
    output.finish()
    print(f'Resources [{identity}]: {output.written} written, {output.reused} reused, '
          f'{output.removed} removed, {time.perf_counter()-start:.3f}s', flush=True)


if __name__ == '__main__':
    main()
