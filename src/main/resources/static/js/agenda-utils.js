/**
 * AgendaUtils - Helpers para Agenda Semanal
 * OKtask
 */

const AgendaUtils = {
    DIAS_SEMANA: ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO'],
    DIAS_CORTOS: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'],
    HORA_INICIO: 7,
    HORA_FIN: 23,
    INTERVALO_MINUTOS: 30,
    ROW_HEIGHT: 40,

    // ========== TIME PARSING ==========

    /**
     * Convierte "HH:MM" a minutos desde medianoche
     * @param {string} timeStr - "12:30"
     * @returns {number} minutos
     */
    timeToMinutes(timeStr) {
        const [h, m] = timeStr.split(':').map(Number);
        return h * 60 + m;
    },

    /**
     * Convierte minutos a "HH:MM"
     * @param {number} minutes
     * @returns {string} "12:30"
     */
    minutesToTime(minutes) {
        const h = Math.floor(minutes / 60).toString().padStart(2, '0');
        const m = (minutes % 60).toString().padStart(2, '0');
        return `${h}:${m}`;
    },

    /**
     * Genera array de slots de tiempo ["07:00", "07:30", ... "22:30"]
     * @returns {string[]}
     */
    generarSlotsTiempo() {
        const slots = [];
        for (let h = this.HORA_INICIO; h < this.HORA_FIN; h++) {
            slots.push(`${h.toString().padStart(2, '0')}:00`);
            slots.push(`${h.toString().padStart(2, '0')}:30`);
        }
        return slots;
    },

    /**
     * Calcula posición top en px para una hora
     * @param {string} timeStr - "12:30"
     * @returns {number} px
     */
    calcularTop(timeStr) {
        const minutes = this.timeToMinutes(timeStr);
        const startMinutes = this.HORA_INICIO * 60;
        return ((minutes - startMinutes) / this.INTERVALO_MINUTOS) * this.ROW_HEIGHT;
    },

    /**
     * Calcula height en px para una duración
     * @param {string} inicio - "12:00"
     * @param {string} fin - "14:00"
     * @returns {number} px
     */
    calcularHeight(inicio, fin) {
        const duracion = this.timeToMinutes(fin) - this.timeToMinutes(inicio);
        return (duracion / this.INTERVALO_MINUTOS) * this.ROW_HEIGHT;
    },

    // ========== GRID COORDINATES ==========

    /**
     * Obtiene día y hora desde evento mouse en grid
     * @param {MouseEvent} e
     * @param {HTMLElement} gridContainer
     * @returns {{dia: string, hora: string, rowIndex: number, colIndex: number}}
     */
    obtenerCoordenadasGrid(e, gridContainer) {
        const rect = gridContainer.getBoundingClientRect();
        const scrollTop = gridContainer.scrollTop;
        const scrollLeft = gridContainer.scrollLeft;

        const y = e.clientY - rect.top + scrollTop;
        const x = e.clientX - rect.left + scrollLeft;

        const headerHeight = 40; // altura header días
        const timeColWidth = 60; // ancho columna horas

        const rowIndex = Math.floor((y - headerHeight) / this.ROW_HEIGHT);
        const colIndex = Math.floor((x - timeColWidth) / ((rect.width - timeColWidth) / 7));

        const hora = this.minutesToTime(this.HORA_INICIO * 60 + rowIndex * this.INTERVALO_MINUTOS);
        const dia = this.DIAS_SEMANA[colIndex];

        return {
            dia: dia || this.DIAS_SEMANA[0],
            hora: hora || '07:00',
            rowIndex: Math.max(0, rowIndex),
            colIndex: Math.max(0, Math.min(6, colIndex)),
        };
    },

    // ========== LOCALSTORAGE ==========

    STORAGE_KEY: 'oktask-agenda-state',

    /**
     * Guarda estado UI en localStorage
     * @param {Object} estado
     */
    guardarEstado(estado) {
        try {
            const actual = this.obtenerEstado();
            const nuevo = { ...actual, ...estado, timestamp: Date.now() };
            localStorage.setItem(this.STORAGE_KEY, JSON.stringify(nuevo));
        } catch (err) {
            console.warn('No se pudo guardar estado agenda:', err);
        }
    },

    /**
     * Obtiene estado UI de localStorage
     * @returns {Object}
     */
    obtenerEstado() {
        try {
            const data = localStorage.getItem(this.STORAGE_KEY);
            return data ? JSON.parse(data) : {};
        } catch (err) {
            return {};
        }
    },

    /**
     * Limpia estado guardado
     */
    limpiarEstado() {
        localStorage.removeItem(this.STORAGE_KEY);
    },

    // ========== FORMATTERS ==========

    /**
     * Formatea bloque para tooltip
     * @param {Object} bloque
     * @returns {string}
     */
    formatearTooltip(bloque) {
        const partes = [`${bloque.materiaNombre}`, `${bloque.horaInicio} - ${bloque.horaFin}`];
        if (bloque.aula) partes.push(`📍 ${bloque.aula}`);
        return partes.join('\n');
    },

    /**
     * Obtiene nombre corto de día
     * @param {string} dia - "LUNES"
     * @returns {string} "Lun"
     */
    diaCorto(dia) {
        const idx = this.DIAS_SEMANA.indexOf(dia);
        return idx >= 0 ? this.DIAS_CORTOS[idx] : dia;
    },

    // ========== VALIDATION ==========

    /**
     * Valida que horaInicio < horaFin
     * @param {string} inicio
     * @param {string} fin
     * @returns {boolean}
     */
    validarRangoHorario(inicio, fin) {
        return this.timeToMinutes(inicio) < this.timeToMinutes(fin);
    },

    /**
     * Genera opciones de hora para select (07:00 - 22:30)
     * @returns {string[]}
     */
    generarOpcionesHora() {
        const opciones = [];
        for (let h = this.HORA_INICIO; h < this.HORA_FIN; h++) {
            opciones.push(`${h.toString().padStart(2, '0')}:00`);
            opciones.push(`${h.toString().padStart(2, '0')}:30`);
        }
        return opciones;
    },
};

window.AgendaUtils = AgendaUtils;