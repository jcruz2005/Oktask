# OKtask Mobile (Android/iOS)

App móvil de OKtask construida con Capacitor.

## Requisitos

- Node.js 18+ y npm
- Android Studio (para Android)
- Xcode (para macOS/iOS)

## Instalación

```bash
cd mobile
npm install
npx cap add android
npx cap add ios
npx cap sync
```

## Ejecutar

### Android
```bash
npx cap open android
# Se abre Android Studio → Run
```

### iOS (necesitás Mac)
```bash
npx cap open ios
# Se abre Xcode → Run
```

## Actualizar frontend

Si modificás archivos en `src/main/resources/static/`, copialos al mobile:

```bash
npm run build
npx cap sync
```

## Estructura

```
mobile/
├── www/                    # Frontend (HTML/CSS/JS)
│   ├── index.html
│   ├── css/
│   └── js/
├── android/                # Proyecto Android (generado por Capacitor)
├── ios/                    # Proyecto iOS (generado por Capacitor)
├── capacitor.config.json   # Configuración de Capacitor
└── package.json
```

## Plugins nativos

- `@capacitor/local-notifications` - Notificaciones Pomodoro
- `@capacitor/background-task` - Timer en background
- `@capacitor-community/sqlite` - SQLite local
- `@capacitor/share` - Exportar datos

## Build para producción

### Android (APK)
```bash
cd android
./gradlew assembleRelease
# APK en: android/app/build/outputs/apk/release/
```

### iOS (IPA)
Xcode → Product → Archive → Distribute App
