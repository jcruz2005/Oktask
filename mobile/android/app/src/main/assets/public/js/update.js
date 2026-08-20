/**
 * UpdateChecker - Sistema de actualizaciones para OKtask
 * Verifica actualizaciones contra GitHub y muestra panel de descarga
 */

class UpdateChecker {
    constructor() {
        this.hasUpdate = false;
        this.updateInfo = null;
        this.modal = null;
        this.init();
    }

    init() {
        // Bind del botón de actualización
        document.getElementById('btn-check-update')?.addEventListener('click', () => {
            this.checkForUpdates(true);
        });

        // Verificar automáticamente al cargar (con delay para no afectar carga)
        setTimeout(() => this.checkForUpdates(false), 5000);
    }

    /**
     * Verifica si hay actualizaciones disponibles
     * @param {boolean} showNoUpdate - Mostrar mensaje si no hay actualización
     */
    async checkForUpdates(showNoUpdate = false) {
        const badge = document.getElementById('update-badge');
        const btn = document.getElementById('btn-check-update');
        
        try {
            // Animación de loading en el botón
            if (btn) btn.classList.add('spinning');

            let data;
            const isMobile = window.Capacitor != null;

            if (isMobile) {
                // En mobile, fetch directo desde GitHub
                const resp = await fetch('https://raw.githubusercontent.com/jcruz2005/Oktask/main/version.json');
                const versionData = await resp.json();
                const currentVersion = '1.2.0';
                data = {
                    hasUpdate: versionData.version !== currentVersion,
                    currentVersion: currentVersion,
                    version: versionData.version,
                    releaseDate: versionData.releaseDate,
                    changelog: versionData.changelog,
                    downloads: versionData.downloads,
                    currentPlatform: 'android'
                };
            } else {
                const response = await fetch(`/api/update/check?platform=${this.platform}`);
                data = await response.json();
            }

            if (btn) btn.classList.remove('spinning');

            if (data.hasUpdate) {
                this.hasUpdate = true;
                this.updateInfo = data;
                
                // Mostrar badge
                if (badge) {
                    badge.style.display = 'block';
                    badge.title = `Nueva versión v${data.version} disponible`;
                }
                
                // Mostrar toast notification
                this.showToast(data);
                
            } else {
                this.hasUpdate = false;
                this.updateInfo = null;
                
                // Ocultar badge
                if (badge) badge.style.display = 'none';
                
                if (showNoUpdate) {
                    this.showToastMessage('Ya tienes la última versión', 'success');
                }
            }

        } catch (error) {
            console.error('Error checking updates:', error);
            if (btn) btn.classList.remove('spinning');
            
            if (showNoUpdate) {
                this.showToastMessage('No se pudo verificar actualizaciones', 'error');
            }
        }
    }

    /**
     * Muestra un toast cuando hay una actualización disponible
     */
    showToast(data) {
        // Crear toast
        const toast = document.createElement('div');
        toast.className = 'update-toast';
        toast.innerHTML = `
            <div class="update-toast-content">
                <div class="update-toast-icon">🔄</div>
                <div class="update-toast-text">
                    <div class="update-toast-title">Nueva versión disponible</div>
                    <div class="update-toast-version">v${data.version}</div>
                </div>
                <button class="update-toast-close" onclick="this.parentElement.parentElement.remove()">✕</button>
            </div>
            <div class="update-toast-action" onclick="updateChecker.showModal(); this.parentElement.remove();">
                Ver detalles e instalar →
            </div>
        `;

        // Estilos del toast
        toast.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: linear-gradient(135deg, #7C3AED, #6D28D9);
            color: white;
            border-radius: 12px;
            box-shadow: 0 8px 32px rgba(124, 58, 237, 0.4);
            z-index: 10000;
            min-width: 320px;
            animation: slideInRight 0.3s ease;
            cursor: pointer;
        `;

        document.body.appendChild(toast);

        // Auto-remover después de 8 segundos
        setTimeout(() => {
            if (toast.parentElement) {
                toast.style.animation = 'slideOutRight 0.3s ease';
                setTimeout(() => toast.remove(), 300);
            }
        }, 8000);
    }

    /**
     * Muestra un toast con mensaje genérico
     */
    showToastMessage(message, type = 'info') {
        const toast = document.createElement('div');
        const bgColor = type === 'success' ? '#10B981' : type === 'error' ? '#EF4444' : '#3B82F6';
        
        toast.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: ${bgColor};
            color: white;
            padding: 12px 20px;
            border-radius: 8px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.2);
            z-index: 10000;
            animation: slideInRight 0.3s ease;
        `;
        toast.textContent = message;

        document.body.appendChild(toast);
        setTimeout(() => toast.remove(), 3000);
    }

    /**
     * Muestra el modal de actualización
     */
    showModal() {
        if (!this.updateInfo) return;

        const data = this.updateInfo;
        const changelog = data.changelog || [];
        const platform = data.currentPlatform || 'linux';
        const platformNames = {
            android: 'Android',
            windows: 'Windows',
            macos: 'macOS',
            linux: 'Linux'
        };
        const platformName = platformNames[platform] || 'Linux';
        const platformDl = data.downloads?.[platform];
        const downloadUrl = platformDl?.url || data.downloadUrl;
        const installCmd = platformDl?.installCommand || '';

        // Crear modal
        this.modal = document.createElement('div');
        this.modal.className = 'update-modal-overlay';
        this.modal.innerHTML = `
            <div class="update-modal">
                <div class="update-modal-header">
                    <div class="update-modal-header-bg"></div>
                    <div class="update-modal-header-content">
                        <div class="update-modal-icon">🔄</div>
                        <div>
                            <h2>Nueva Versión Disponible</h2>
                            <p>v${data.version} ${data.releaseDate ? '(' + data.releaseDate + ')' : ''}</p>
                            <p style="font-size: 12px; color: #aaa; margin-top: 4px;">Paquete para: ${platformName}</p>
                        </div>
                    </div>
                </div>
                
                <div class="update-modal-body">
                    <h3>Cambios en esta versión:</h3>
                    <ul class="update-changelog">
                        ${changelog.map(item => `<li>${item}</li>`).join('')}
                    </ul>
                    ${installCmd ? `
                    <div style="margin-top: 15px;">
                        <h3>Instalación:</h3>
                        <code style="display: block; background: #1a1a2e; color: #00d4aa; padding: 10px; border-radius: 6px; font-size: 12px; white-space: pre-wrap;">${installCmd}</code>
                    </div>
                    ` : ''}
                </div>
                
                <div class="update-modal-footer">
                    <button class="btn-update-close" onclick="updateChecker.closeModal()">
                        Cerrar
                    </button>
                    <button class="btn-update-download" onclick="updateChecker.openDownload()">
                        Descargar para ${platformName}
                    </button>
                </div>
            </div>
        `;

        // Estilos del modal
        const style = document.createElement('style');
        style.textContent = `
            @keyframes slideInRight {
                from { transform: translateX(100%); opacity: 0; }
                to { transform: translateX(0); opacity: 1; }
            }
            @keyframes slideOutRight {
                from { transform: translateX(0); opacity: 1; }
                to { transform: translateX(100%); opacity: 0; }
            }
            @keyframes fadeIn {
                from { opacity: 0; }
                to { opacity: 1; }
            }
            @keyframes scaleIn {
                from { transform: scale(0.9); opacity: 0; }
                to { transform: scale(1); opacity: 1; }
            }
            .update-modal-overlay {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0,0,0,0.5);
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 10001;
                animation: fadeIn 0.2s ease;
            }
            .update-modal {
                background: white;
                border-radius: 16px;
                width: 450px;
                max-height: 80vh;
                overflow: hidden;
                box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                animation: scaleIn 0.2s ease;
            }
            .update-modal-header {
                position: relative;
                padding: 24px;
                color: white;
                overflow: hidden;
            }
            .update-modal-header-bg {
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: linear-gradient(135deg, #7C3AED, #6D28D9);
            }
            .update-modal-header-content {
                position: relative;
                display: flex;
                align-items: center;
                gap: 16px;
            }
            .update-modal-icon {
                font-size: 36px;
            }
            .update-modal-header h2 {
                margin: 0;
                font-size: 20px;
                font-weight: bold;
            }
            .update-modal-header p {
                margin: 4px 0 0;
                opacity: 0.9;
                font-size: 14px;
            }
            .update-modal-body {
                padding: 20px 24px;
                max-height: 300px;
                overflow-y: auto;
            }
            .update-modal-body h3 {
                margin: 0 0 12px;
                font-size: 14px;
                color: #374151;
            }
            .update-changelog {
                list-style: none;
                padding: 0;
                margin: 0;
            }
            .update-changelog li {
                padding: 8px 0;
                padding-left: 20px;
                position: relative;
                color: #4B5563;
                font-size: 14px;
                border-bottom: 1px solid #F3F4F6;
            }
            .update-changelog li:last-child {
                border-bottom: none;
            }
            .update-changelog li::before {
                content: '•';
                position: absolute;
                left: 0;
                color: #7C3AED;
                font-weight: bold;
            }
            .update-modal-footer {
                padding: 16px 24px;
                display: flex;
                justify-content: flex-end;
                gap: 12px;
                background: #F9FAFB;
                border-top: 1px solid #E5E7EB;
            }
            .btn-update-close {
                padding: 10px 20px;
                border: none;
                background: transparent;
                color: #6B7280;
                cursor: pointer;
                border-radius: 8px;
                font-size: 14px;
            }
            .btn-update-close:hover {
                background: #E5E7EB;
            }
            .btn-update-download {
                padding: 10px 20px;
                border: none;
                background: #7C3AED;
                color: white;
                cursor: pointer;
                border-radius: 8px;
                font-size: 14px;
                font-weight: 600;
            }
            .btn-update-download:hover {
                background: #6D28D9;
            }
            .btn-check-update.spinning i {
                animation: spin 1s linear infinite;
            }
            @keyframes spin {
                from { transform: rotate(0deg); }
                to { transform: rotate(360deg); }
            }
        `;
        document.head.appendChild(style);

        document.body.appendChild(this.modal);

        // Cerrar al hacer click fuera
        this.modal.addEventListener('click', (e) => {
            if (e.target === this.modal) this.closeModal();
        });
    }

    /**
     * Cierra el modal
     */
    closeModal() {
        if (this.modal) {
            this.modal.style.animation = 'fadeIn 0.2s ease reverse';
            setTimeout(() => {
                this.modal?.remove();
                this.modal = null;
            }, 200);
        }
    }

    /**
     * Muestra un indicador de progreso de descarga
     */
    showDownloadProgress(message) {
        this._progressEl = document.createElement('div');
        this._progressEl.style.cssText = `
            position: fixed; top: 0; left: 0; width: 100%; height: 100%;
            background: rgba(0,0,0,0.7); display: flex; align-items: center;
            justify-content: center; z-index: 20000;
        `;
        this._progressEl.innerHTML = `
            <div style="background: white; border-radius: 16px; padding: 32px; text-align: center; min-width: 280px;">
                <div style="font-size: 36px; margin-bottom: 16px;">📥</div>
                <div style="font-size: 16px; font-weight: 600; color: #1a1a2e; margin-bottom: 8px;">${message}</div>
                <div style="width: 100%; height: 6px; background: #e5e7eb; border-radius: 3px; overflow: hidden; margin-top: 12px;">
                    <div class="download-progress-bar" style="width: 0%; height: 100%; background: linear-gradient(90deg, #7C3AED, #6D28D9); border-radius: 3px; transition: width 0.3s;"></div>
                </div>
                <div class="download-progress-text" style="font-size: 12px; color: #9ca3af; margin-top: 8px;">Preparando...</div>
            </div>
        `;
        document.body.appendChild(this._progressEl);
    }

    updateDownloadProgress(percent, text) {
        const bar = this._progressEl?.querySelector('.download-progress-bar');
        const txt = this._progressEl?.querySelector('.download-progress-text');
        if (bar) bar.style.width = percent + '%';
        if (txt) txt.textContent = text;
    }

    hideDownloadProgress() {
        this._progressEl?.remove();
        this._progressEl = null;
    }

    /**
     * Descarga e instala el APK directamente desde la app (solo Android)
     */
    async openDownload() {
        if (!this.updateInfo) return;

        const platform = this.updateInfo.currentPlatform || 'linux';
        const platformDl = this.updateInfo.downloads?.[platform];
        const url = platformDl?.url || this.updateInfo.downloadUrl;

        if (!url) return;

        // En mobile Android, descarga directa e instalación
        if (window.Capacitor && platform === 'android') {
            this.closeModal();
            this.showDownloadProgress('Descargando e instalando...');

            try {
                const ApkInstaller = Capacitor.Plugins.ApkInstaller;
                const fileName = `OKtask-${this.updateInfo.version}.apk`;
                await ApkInstaller.downloadAndInstall({ url, fileName });

                this.hideDownloadProgress();

            } catch (error) {
                this.hideDownloadProgress();
                console.error('Error installing APK:', error);
                this.showToastMessage('Error al instalar: ' + error.message, 'error');
            }

        } else {
            // Desktop: abrir en navegador
            window.open(url, '_blank');
            this.closeModal();
        }
    }
}

// Inicializar cuando el DOM esté listo
let updateChecker;
document.addEventListener('DOMContentLoaded', () => {
    updateChecker = new UpdateChecker();
});
