/**
 * Notificaciones - Gestión de notificaciones del navegador
 * Gestor de Tareas Académicas
 */

class Notificaciones {
    /**
     * Solicita permiso para notificaciones
     * @returns {Promise<boolean>} True si se otorgó permiso
     */
    static async solicitarPermiso() {
        if (!('Notification' in window)) {
            console.warn('Este navegador no soporta notificaciones');
            return false;
        }

        if (Notification.permission === 'granted') {
            return true;
        }

        if (Notification.permission !== 'denied') {
            const permiso = await Notification.requestPermission();
            return permiso === 'granted';
        }

        return false;
    }

    /**
     * Envía una notificación
     * @param {string} titulo - Título de la notificación
     * @param {Object} opciones - Opciones de la notificación
     */
    static enviar(titulo, opciones = {}) {
        if (Notification.permission === 'granted') {
            const defaultOptions = {
                icon: '/img/favicon.ico',
                badge: '/img/favicon.ico',
                vibrate: [200, 100, 200],
                ...opciones
            };

            new Notification(titulo, defaultOptions);
        }
    }

    /**
     * Notifica cuando una tarea está por vencer
     * @param {Object} tarea - Tarea a notificar
     */
    static notificarTareaProxima(tarea) {
        const diasRestantes = Utils.diasRestantes(tarea.fechaLimite);
        
        let titulo = '';
        let body = '';

        if (diasRestantes === 0) {
            titulo = '⚠️ Tarea vence hoy';
            body = `"${tarea.titulo}" vence hoy`;
        } else if (diasRestantes === 1) {
            titulo = '⏰ Tarea vence mañana';
            body = `"${tarea.titulo}" vence mañana`;
        } else if (diasRestantes <= 3) {
            titulo = '📅 Tarea próxima a vencer';
            body = `"${tarea.titulo}" vence en ${diasRestantes} días`;
        }

        if (titulo) {
            this.enviar(titulo, { body, tag: `tarea-${tarea.id}` });
        }
    }

    /**
     * Notifica cuando un Pomodoro se completa
     * @param {number} duracion - Duración del pomodoro en minutos
     */
    static notificarPomodoroCompletado(duracion) {
        this.enviar('🍅 ¡Pomodoro completado!', {
            body: `Completaste ${duracion} minutos de estudio`,
            tag: 'pomodoro-completado',
            requireInteraction: true
        });
    }

    /**
     * Notifica cuando un descanso termina
     */
    static notificarDescansoTerminado() {
        this.enviar('⏰ Descanso terminado', {
            body: 'Es hora de volver a estudiar',
            tag: 'descanso-terminado',
            requireInteraction: true
        });
    }

    /**
     * Verifica tareas próximas a vencer y envía notificaciones
     * @param {Array} tareas - Lista de tareas
     */
    static verificarTareasProximas(tareas) {
        const tareasProximas = tareas.filter(tarea => {
            if (tarea.estado === 'COMPLETADA') return false;
            const dias = Utils.diasRestantes(tarea.fechaLimite);
            return dias >= 0 && dias <= 3;
        });

        tareasProximas.forEach(tarea => {
            this.notificarTareaProxima(tarea);
        });
    }

    /**
     * Inicia la verificación periódica de tareas
     * @param {Function} funcionObtenerTareas - Función para obtener tareas
     * @param {number} intervaloMinutos - Intervalo en minutos
     */
    static iniciarVerificacionPeriodica(funcionObtenerTareas, intervaloMinutos = 60) {
        // Verificar inmediatamente
        this.verificarTareas(funcionObtenerTareas);

        // Programar verificación periódica
        setInterval(() => {
            this.verificarTareas(funcionObtenerTareas);
        }, intervaloMinutos * 60 * 1000);
    }

    /**
     * Verifica y notifica tareas
     * @param {Function} funcionObtenerTareas - Función para obtener tareas
     */
    static async verificarTareas(funcionObtenerTareas) {
        try {
            const permiso = await this.solicitarPermiso();
            if (!permiso) return;

            const tareas = await funcionObtenerTareas();
            this.verificarTareasProximas(tareas);
        } catch (error) {
            console.error('Error al verificar tareas:', error);
        }
    }
}

// Exportar para uso global
window.Notificaciones = Notificaciones;
