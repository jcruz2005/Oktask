/**
 * Utils - Utilidades generales
 * Gestor de Tareas Académicas
 */

class Utils {
    /**
     * Formatea una fecha a string legible
     * @param {string|Date} fecha - Fecha a formatear
     * @returns {string} Fecha formateada
     */
    static formatearFecha(fecha) {
        if (!fecha) return '';
        
        const date = new Date(fecha);
        const options = { 
            day: 'numeric', 
            month: 'short', 
            year: 'numeric' 
        };
        
        return date.toLocaleDateString('es-AR', options);
    }

    /**
     * Formatea una fecha a string corto
     * @param {string|Date} fecha - Fecha a formatear
     * @returns {string} Fecha formateada (ej: "14 ago")
     */
    static formatearFechaCorta(fecha) {
        if (!fecha) return '';
        
        const date = new Date(fecha);
        const options = { day: 'numeric', month: 'short' };
        
        return date.toLocaleDateString('es-AR', options);
    }

    /**
     * Formatea una fecha y hora
     * @param {string|Date} fecha - Fecha a formatear
     * @returns {string} Fecha y hora formateada
     */
    static formatearFechaHora(fecha) {
        if (!fecha) return '';
        
        const date = new Date(fecha);
        const options = { 
            day: 'numeric', 
            month: 'short', 
            hour: '2-digit', 
            minute: '2-digit' 
        };
        
        return date.toLocaleDateString('es-AR', options);
    }

    /**
     * Formatea horas a string legible
     * @param {number} horas - Cantidad de horas
     * @returns {string} Horas formateadas
     */
    static formatearHoras(horas) {
        if (!horas || horas === 0) return '0 min';
        
        const horasEntreas = Math.floor(horas);
        const minutos = Math.round((horas - horasEntreas) * 60);
        
        if (horasEntreas === 0) {
            return `${minutos} min`;
        }
        
        if (minutos === 0) {
            return `${horasEntreas}h`;
        }
        
        return `${horasEntreas}h ${minutos}min`;
    }

    /**
     * Formatea minutos a MM:SS
     * @param {number} minutos - Minutos totales
     * @returns {string} Tiempo formateado
     */
    static formatearTiempo(minutos) {
        const mins = Math.floor(minutos);
        const secs = Math.round((minutos - mins) * 60);
        return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }

    /**
     * Calcula los días restantes hasta una fecha
     * @param {string|Date} fecha - Fecha límite
     * @returns {number} Días restantes (negativo si ya pasó)
     */
    static diasRestantes(fecha) {
        if (!fecha) return Infinity;
        
        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);
        
        const fechaLimite = new Date(fecha);
        fechaLimite.setHours(0, 0, 0, 0);
        
        const diffTime = fechaLimite.getTime() - hoy.getTime();
        return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    }

    /**
     * Obtiene la clase CSS para la urgencia de una fecha
     * @param {string|Date} fecha - Fecha límite
     * @returns {string} Clase CSS
     */
    static obtenerClaseUrgencia(fecha) {
        const dias = this.diasRestantes(fecha);
        
        if (dias < 0) return 'urgent';
        if (dias <= 2) return 'urgent';
        if (dias <= 7) return 'soon';
        return 'later';
    }

    /**
     * Obtiene el texto de días restantes
     * @param {string|Date} fecha - Fecha límite
     * @returns {string} Texto descriptivo
     */
    static textoDiasRestantes(fecha) {
        const dias = this.diasRestantes(fecha);
        
        if (dias < 0) return `Venció hace ${Math.abs(dias)} días`;
        if (dias === 0) return 'Vence hoy';
        if (dias === 1) return 'Vence mañana';
        return `Vence en ${dias} días`;
    }

    /**
     * Genera un color aleatorio
     * @returns {string} Color hexadecimal
     */
    static colorAleatorio() {
        const colores = [
            '#7C3AED', '#06B6D4', '#14B8A6', '#22C55E',
            '#F59E0B', '#EF4444', '#EC4899', '#8B5CF6',
            '#3B82F6', '#10B981', '#F97316', '#6366F1'
        ];
        return colores[Math.floor(Math.random() * colores.length)];
    }

    /**
     * Convierte hex a RGB
     * @param {string} hex - Color hexadecimal
     * @returns {Object} Objeto RGB
     */
    static hexARGB(hex) {
        const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
        return result ? {
            r: parseInt(result[1], 16),
            g: parseInt(result[2], 16),
            b: parseInt(result[3], 16)
        } : null;
    }

    /**
     * Obtiene un color con opacidad
     * @param {string} hex - Color hexadecimal
     * @param {number} opacidad - Opacidad (0-1)
     * @returns {string} Color con opacidad
     */
    static colorConOpacidad(hex, opacidad) {
        const rgb = this.hexARGB(hex);
        if (!rgb) return hex;
        return `rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, ${opacidad})`;
    }

    /**
     * Debounce para funciones
     * @param {Function} func - Función a ejecutar
     * @param {number} wait - Tiempo de espera en ms
     * @returns {Function} Función con debounce
     */
    static debounce(func, wait = 300) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    /**
     * Muestra un toast de notificación
     * @param {string} titulo - Título del toast
     * @param {string} mensaje - Mensaje del toast
     * @param {string} tipo - Tipo (success, error, warning, info)
     */
    static mostrarToast(titulo, mensaje, tipo = 'info') {
        const toast = document.getElementById('toast-notificacion');
        const toastTitulo = document.getElementById('toast-titulo');
        const toastMensaje = document.getElementById('toast-mensaje');
        
        toastTitulo.textContent = titulo;
        toastMensaje.textContent = mensaje;
        
        // Limpiar clases anteriores
        toast.className = 'toast';
        toast.classList.add(`bg-${tipo === 'error' ? 'danger' : tipo}`);
        toast.classList.add('text-white');
        
        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();
    }

    /**
     * Muestra un diálogo de confirmación
     * @param {string} mensaje - Mensaje a mostrar
     * @returns {Promise<boolean>} True si confirmó
     */
    static confirmar(mensaje) {
        return new Promise((resolve) => {
            const modal = document.getElementById('modal-confirmar');
            const mensajeEl = document.getElementById('modal-confirmar-mensaje');
            const btnConfirmar = document.getElementById('btn-confirmar-eliminar');
            
            mensajeEl.textContent = mensaje;
            
            const bsModal = new bootstrap.Modal(modal);
            bsModal.show();
            
            const onConfirm = () => {
                bsModal.hide();
                btnConfirmar.removeEventListener('click', onConfirm);
                resolve(true);
            };
            
            const onHide = () => {
                btnConfirmar.removeEventListener('click', onConfirm);
                resolve(false);
            };
            
            btnConfirmar.addEventListener('click', onConfirm);
            modal.addEventListener('hidden.bs.modal', onHide, { once: true });
        });
    }

    /**
     * Obtiene la fecha de inicio de la semana actual
     * @returns {string} Fecha en formato YYYY-MM-DD
     */
    static inicioSemana() {
        const hoy = new Date();
        const diaSemana = hoy.getDay();
        const diff = hoy.getDate() - diaSemana + (diaSemana === 0 ? -6 : 1);
        
        const inicio = new Date(hoy);
        inicio.setDate(diff);
        
        return inicio.toISOString().split('T')[0];
    }

    /**
     * Obtiene la fecha de inicio del mes actual
     * @returns {string} Fecha en formato YYYY-MM-DD
     */
    static inicioMes() {
        const hoy = new Date();
        return new Date(hoy.getFullYear(), hoy.getMonth(), 1).toISOString().split('T')[0];
    }

    /**
     * Obtiene la fecha actual en formato YYYY-MM-DD
     * @returns {string} Fecha formateada
     */
    static hoy() {
        return new Date().toISOString().split('T')[0];
    }

    /**
     * Obtiene la fecha de fin del mes actual
     * @returns {string} Fecha en formato YYYY-MM-DD
     */
    static finMes() {
        const hoy = new Date();
        return new Date(hoy.getFullYear(), hoy.getMonth() + 1, 0).toISOString().split('T')[0];
    }
}

// Exportar para uso global
window.Utils = Utils;
