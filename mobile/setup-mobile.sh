#!/bin/bash
# setup-mobile.sh - Configurar proyecto mobile con Capacitor
set -e

echo "=========================================="
echo "  SETUP OKtask Mobile (Android/iOS)"
echo "=========================================="

# Verificar Node.js
if ! command -v node &>/dev/null; then
    echo "[ERROR] Se requiere Node.js 18+"
    echo "Instalar: https://nodejs.org/"
    exit 1
fi

NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
if [ "$NODE_VERSION" -lt 18 ]; then
    echo "[ERROR] Se requiere Node.js 18 o superior"
    exit 1
fi
echo "[OK] Node.js $(node -v)"

# Verificar npm
if ! command -v npm &>/dev/null; then
    echo "[ERROR] Se requiere npm"
    exit 1
fi
echo "[OK] npm $(npm -v)"

# Ir al directorio mobile
cd "$(dirname "${BASH_SOURCE[0]}")"

# Copiar frontend
echo "[BUILD] Copiando frontend..."
mkdir -p www
cp -r ../src/main/resources/static/* www/
echo "[OK] Frontend copiado"

# Instalar dependencias
echo "[BUILD] Instalando dependencias..."
npm install
echo "[OK] Dependencias instaladas"

# Instalar Capacitor
echo "[BUILD] Instalando Capacitor..."
npm install @capacitor/core @capacitor/cli
npm install @capacitor/local-notifications
npm install @capacitor/background-task
npm install @capacitor/share
npm install @capacitor-community/sqlite
echo "[OK] Capacitor instalado"

# Agregar plataformas
echo "[BUILD] Agregando plataformas..."
npx cap add android
echo "[OK] Android agregado"

if [[ "$*" == *"--ios"* ]]; then
    if [[ "$OSTYPE" == "darwin"* ]]; then
        npx cap add ios
        echo "[OK] iOS agregado"
    else
        echo "[WARN] iOS solo se puede agregar en macOS"
    fi
fi

# Sync
npx cap sync

echo ""
echo "=========================================="
echo "  SETUP COMPLETADO"
echo "=========================================="
echo ""
echo "Para Android:"
echo "  npx cap open android"
echo ""
echo "Para iOS (solo macOS):"
echo "  npx cap open ios"
echo ""
