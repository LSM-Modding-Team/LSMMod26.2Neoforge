"""Serialize generated resources and locate disposable review images."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def preview_path(filename):
    directory = ROOT / 'previews'
    directory.mkdir(exist_ok=True)
    return directory / filename


def resource_json(path, value, *, ensure_ascii=False):
    compact = bool({'models', 'blockstates'} & set(Path(path).parts))
    options = {'separators': (',', ':')} if compact else {'indent': 2}
    return json.dumps(value, ensure_ascii=ensure_ascii, **options)
