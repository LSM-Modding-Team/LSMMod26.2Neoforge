#!/usr/bin/env bash
# Source this file to select Java 25, reusable caches, proxy and CA trust.
set -euo pipefail
lsm_project_root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd)
lsm_tools_root=${LSM_BUILD_TOOLS_DIR:-"$lsm_project_root/.build-tools"}
lsm_java25() {
    [ -x "$1/bin/javac" ] && "$1/bin/javac" -version 2>&1 | grep -Eq '^javac 25([. ]|$)'
}
# This ignored directory is an index of warm build inputs, not project source.
# Link shared installations in place: moving them invalidates caches and affects other workspaces.
mkdir -p "$lsm_tools_root"
lsm_cache_link() {
    if [ -d "$1" ] && [ ! -e "$2" ] && [ ! -L "$2" ]; then
        ln -s -- "$1" "$2"
    fi
}
if [ -n "${JAVA_HOME:-}" ] && lsm_java25 "$JAVA_HOME"; then
    lsm_cache_link "$JAVA_HOME" "$lsm_tools_root/jdk25"
else
    lsm_cache_link /workspace/.cloud-setup/jdk25 "$lsm_tools_root/jdk25"
fi
lsm_cache_link "${GRADLE_USER_HOME:-/workspace/.gradle-cloud}" "$lsm_tools_root/gradle"
lsm_cache_link "$lsm_project_root/.gradle" "$lsm_tools_root/project-cache"
lsm_cache_link "$lsm_project_root/build" "$lsm_tools_root/output"
lsm_cache_link "$lsm_project_root/libs" "$lsm_tools_root/libs"
if [ ! -e "$lsm_tools_root/AGENTS.md" ]; then
    cat > "$lsm_tools_root/AGENTS.md" <<'EOF'
# Solo compilación: no explorar durante tareas normales

Esta carpeta contiene o enlaza JDK, dependencias, cachés y salidas de compilación.
No enumerar, leer, indexar ni buscar recursivamente aquí salvo que la tarea requiera
compilar o diagnosticar un fallo de compilación. No es código fuente editable.
Usar tools/build/compile.sh desde la raíz del proyecto. No versionar su contenido,
no limpiar las cachés para una compilación normal y no seguir enlaces en búsquedas generales.
EOF
fi
if [ ! -e "$lsm_tools_root/README.md" ]; then
    cat > "$lsm_tools_root/README.md" <<'EOF'
# SOLO PARA COMPILAR — caché local, fuera de Git

Compilación rápida: bash tools/build/compile.sh jar --offline
Compilación con comprobaciones completas: bash tools/build/compile.sh build --offline

- jdk25: Java 25 reutilizable.
- gradle: distribución, dependencias y Minecraft/NeoForge ya preparados.
- project-cache: estado incremental del proyecto (.gradle).
- libs: dependencias locales de compilación, como Jade.
- output: clases, recursos preparados, comprobaciones y JAR (build).

Los enlaces conservan las ubicaciones reales de las cachés compartidas. No es una
carpeta portable ni una copia del código. No explorar en tareas normales de Codex.
Offline necesita las dependencias de la versión solicitada ya descargadas.
EOF
fi
if [ -n "${JAVA_HOME:-}" ] && lsm_java25 "$JAVA_HOME"; then
    :
elif lsm_java25 "$lsm_tools_root/jdk25"; then
    export JAVA_HOME="$lsm_tools_root/jdk25"
elif lsm_java25 /workspace/.cloud-setup/jdk25; then
    export JAVA_HOME=/workspace/.cloud-setup/jdk25
elif command -v javac >/dev/null 2>&1 && lsm_java25 "$(dirname "$(dirname "$(readlink -f "$(command -v javac)")")")"; then
    export JAVA_HOME=$(dirname "$(dirname "$(readlink -f "$(command -v javac)")")")
else
    if [ "$(uname -s)" != Linux ] || [ "$(uname -m)" != x86_64 ]; then
        echo 'Set JAVA_HOME to a JDK 25 installation; automatic setup supports Linux x86_64.' >&2
        return 1
    fi
    command -v curl >/dev/null
    command -v python3 >/dev/null
    mkdir -p "$lsm_tools_root"
    echo 'Downloading JDK 25 (first build only)...' >&2
    curl -fsSL --retry 2 https://download.oracle.com/java/25/latest/jdk-25_linux-x64_bin.tar.gz -o "$lsm_tools_root/jdk25.tar.gz"
    curl -fsSL --retry 2 https://download.oracle.com/java/25/latest/jdk-25_linux-x64_bin.tar.gz.sha256 -o "$lsm_tools_root/jdk25.sha256"
    python3 - "$lsm_tools_root" <<'PY'
import hashlib, pathlib, sys
root = pathlib.Path(sys.argv[1])
with (root / 'jdk25.tar.gz').open('rb') as stream:
    digest = hashlib.file_digest(stream, 'sha256').hexdigest()
if digest != (root / 'jdk25.sha256').read_text().split()[0]:
    raise SystemExit('JDK checksum mismatch; download not extracted.')
PY
    mkdir -p "$lsm_tools_root/jdk25"
    tar -xzf "$lsm_tools_root/jdk25.tar.gz" --strip-components=1 -C "$lsm_tools_root/jdk25"
    export JAVA_HOME="$lsm_tools_root/jdk25"
    lsm_java25 "$JAVA_HOME"
    rm -f -- "$lsm_tools_root/jdk25.tar.gz" "$lsm_tools_root/jdk25.sha256"
fi
export PATH="$JAVA_HOME/bin:$PATH"
if [ -z "${GRADLE_USER_HOME:-}" ]; then
    mkdir -p "$lsm_tools_root/gradle"
    # Resolve the link so Gradle/NeoForm retain the cache keys from the warm build.
    export GRADLE_USER_HOME=$(cd -- "$lsm_tools_root/gradle" && pwd -P)
fi
# Keep any existing explicit truststore and the inherited proxy configuration.
if [[ "${JAVA_TOOL_OPTIONS:-}" != *javax.net.ssl.trustStore=* ]] && [ -f /etc/ssl/certs/java/cacerts ]; then
    export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:+$JAVA_TOOL_OPTIONS }-Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts"
fi
if [ -n "${HTTPS_PROXY:-${https_proxy:-}}" ] && [[ "${JAVA_TOOL_OPTIONS:-}" != *https.proxyHost=* ]]; then
    lsm_proxy_options=$(python3 - <<'PY'
import os, urllib.parse
proxy = urllib.parse.urlsplit(os.environ.get('HTTPS_PROXY') or os.environ['https_proxy'])
if not proxy.hostname or not proxy.port or proxy.username or proxy.password:
    raise SystemExit('Build proxy must specify a host and port without embedded credentials.')
print(f'-Dhttps.proxyHost={proxy.hostname} -Dhttps.proxyPort={proxy.port} -Dhttp.proxyHost={proxy.hostname} -Dhttp.proxyPort={proxy.port}')
PY
    )
    export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:+$JAVA_TOOL_OPTIONS }$lsm_proxy_options"
    unset lsm_proxy_options
fi
