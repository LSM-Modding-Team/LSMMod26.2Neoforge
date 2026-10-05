#!/usr/bin/env bash
# Reusable Linux build harness. Run from any directory; extra arguments go to Gradle.
set -euo pipefail
lsm_project_root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd)
source "$lsm_project_root/tools/build/environment.sh"
cd "$lsm_project_root"
if [ "$#" -eq 0 ]; then set -- build; fi
exec bash "$lsm_project_root/gradlew" --max-workers=4 "$@"
