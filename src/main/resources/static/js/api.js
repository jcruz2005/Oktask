/**
 * API - Comunicación con el Backend
 * Gestor de Tareas Académicas
 */

const API_BASE = '/api';

/**
 * Clase para interactuar con la API REST
 */
class API {
    /**
     * Realiza una petición HTTP
     * @param {string} endpoint - Endpoint de la API
     * @param {Object} options - Opciones de fetch
     * @returns {Promise} Respuesta de la API
     */
    static async request(endpoint, options = {}) {
        const url = `${API_BASE}${endpoint}`;
        
        const config = {
            headers: {
                'Content-Type': 'application/json',
            },
            ...options,
        };

        try {
            const response = await fetch(url, config);
            
            if (!response.ok) {
                const error = await response.json().catch(() => ({}));
                throw new Error(error.message || `Error HTTP: ${response.status}`);
            }

            if (response.status === 204) {
                return null;
            }

            return await response.json();
        } catch (error) {
            console.error(`Error en petición a ${endpoint}:`, error);
            throw error;
        }
    }

    // ============================================
    // MATERIAS
    // ============================================

    /**
     * Obtiene todas las materias
     * @returns {Promise<Array>} Lista de materias
     */
    static async listarMaterias() {
        return this.request('/materias');
    }

    /**
     * Obtiene una materia por ID
     * @param {string} id - ID de la materia
     * @returns {Promise<Object>} Materia
     */
    static async obtenerMateria(id) {
        return this.request(`/materias/${id}`);
    }

    /**
     * Crea una nueva materia
     * @param {Object} materia - Datos de la materia
     * @returns {Promise<Object>} Materia creada
     */
    static async crearMateria(materia) {
        return this.request('/materias', {
            method: 'POST',
            body: JSON.stringify(materia),
        });
    }

    /**
     * Actualiza una materia
     * @param {string} id - ID de la materia
     * @param {Object} materia - Datos actualizados
     * @returns {Promise<Object>} Materia actualizada
     */
    static async editarMateria(id, materia) {
        return this.request(`/materias/${id}`, {
            method: 'PUT',
            body: JSON.stringify(materia),
        });
    }

    /**
     * Elimina una materia
     * @param {string} id - ID de la materia
     * @returns {Promise<void>}
     */
    static async eliminarMateria(id) {
        return this.request(`/materias/${id}`, {
            method: 'DELETE',
        });
    }

    // ============================================
    // TAREAS
    // ============================================

    /**
     * Obtiene todas las tareas
     * @returns {Promise<Array>} Lista de tareas
     */
    static async listarTareas() {
        return this.request('/tareas');
    }

    /**
     * Obtiene una tarea por ID
     * @param {string} id - ID de la tarea
     * @returns {Promise<Object>} Tarea
     */
    static async obtenerTarea(id) {
        return this.request(`/tareas/${id}`);
    }

    /**
     * Obtiene tareas por materia
     * @param {string} materiaId - ID de la materia
     * @returns {Promise<Array>} Lista de tareas
     */
    static async listarTareasPorMateria(materiaId) {
        return this.request(`/tareas/materia/${materiaId}`);
    }

    /**
     * Obtiene tareas por estado
     * @param {string} estado - Estado de las tareas
     * @returns {Promise<Array>} Lista de tareas
     */
    static async listarTareasPorEstado(estado) {
        return this.request(`/tareas/estado/${estado}`);
    }

    /**
     * Crea una nueva tarea
     * @param {Object} tarea - Datos de la tarea
     * @returns {Promise<Object>} Tarea creada
     */
    static async crearTarea(tarea) {
        return this.request('/tareas', {
            method: 'POST',
            body: JSON.stringify(tarea),
        });
    }

    /**
     * Actualiza una tarea
     * @param {string} id - ID de la tarea
     * @param {Object} tarea - Datos actualizados
     * @returns {Promise<Object>} Tarea actualizada
     */
    static async editarTarea(id, tarea) {
        return this.request(`/tareas/${id}`, {
            method: 'PUT',
            body: JSON.stringify(tarea),
        });
    }

    /**
     * Cambia el estado de una tarea
     * @param {string} id - ID de la tarea
     * @param {string} estado - Nuevo estado
     * @returns {Promise<Object>} Tarea actualizada
     */
    static async cambiarEstadoTarea(id, estado) {
        return this.request(`/tareas/${id}`, {
            method: 'PUT',
            body: JSON.stringify({ estado }),
        });
    }

    /**
     * Elimina una tarea
     * @param {string} id - ID de la tarea
     * @returns {Promise<void>}
     */
    static async eliminarTarea(id) {
        return this.request(`/tareas/${id}`, {
            method: 'DELETE',
        });
    }

    // ============================================
    // POMODORO
    // ============================================

    /**
     * Inicia una sesión de Pomodoro
     * @param {Object} sesion - Datos de la sesión
     * @returns {Promise<Object>} Sesión iniciada
     */
    static async iniciarSesionPomodoro(sesion) {
        return this.request('/pomodoro/iniciar', {
            method: 'POST',
            body: JSON.stringify(sesion),
        });
    }

    /**
     * Finaliza una sesión de Pomodoro
     * @param {string} sesionId - ID de la sesión
     * @returns {Promise<Object>} Sesión finalizada
     */
    static async finalizarSesionPomodoro(sesionId) {
        return this.request(`/pomodoro/${sesionId}/finalizar`, {
            method: 'POST',
        });
    }

    /**
     * Cancela una sesión de Pomodoro
     * @param {string} sesionId - ID de la sesión
     * @param {number} minutosTranscurridos - Minutos realmente transcurridos
     * @returns {Promise<Object>} Sesión cancelada
     */
    static async cancelarSesionPomodoro(sesionId, minutosTranscurridos) {
        return this.request(`/pomodoro/${sesionId}/cancelar`, {
            method: 'POST',
            body: JSON.stringify({ minutosTranscurridos }),
        });
    }

    /**
     * Obtiene las sesiones de una tarea
     * @param {string} tareaId - ID de la tarea
     * @returns {Promise<Array>} Lista de sesiones
     */
    static async obtenerSesionesTarea(tareaId) {
        return this.request(`/pomodoro/tarea/${tareaId}`);
    }

    /**
     * Obtiene las sesiones de una materia
     * @param {string} materiaId - ID de la materia
     * @returns {Promise<Array>} Lista de sesiones
     */
    static async obtenerSesionesMateria(materiaId) {
        return this.request(`/pomodoro/materia/${materiaId}`);
    }

    /**
     * Obtiene la configuración del Pomodoro
     * @returns {Promise<Object>} Configuración
     */
    static async obtenerConfiguracionPomodoro() {
        return this.request('/pomodoro/configuracion');
    }

    /**
     * Actualiza la configuración del Pomodoro
     * @param {Object} configuracion - Configuración a actualizar
     * @returns {Promise<Object>} Configuración actualizada
     */
    static async actualizarConfiguracionPomodoro(configuracion) {
        return this.request('/pomodoro/configuracion', {
            method: 'PUT',
            body: JSON.stringify(configuracion),
        });
    }

    // ============================================
    // ESTADÍSTICAS
    // ============================================

    /**
     * Obtiene las horas por materia
     * @returns {Promise<Array>} Estadísticas por materia
     */
    static async obtenerHorasPorMateria() {
        return this.request('/estadisticas/horas');
    }

    /**
     * Obtiene las horas por período
     * @param {string} fechaInicio - Fecha de inicio
     * @param {string} fechaFin - Fecha de fin
     * @returns {Promise<Array>} Estadísticas por período
     */
    static async obtenerHorasPorPeriodo(fechaInicio, fechaFin) {
        return this.request(`/estadisticas/horas/periodo?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`);
    }

    /**
     * Obtiene el progreso de tareas
     * @returns {Promise<Array>} Estadísticas de progreso
     */
    static async obtenerProgresoTareas() {
        return this.request('/estadisticas/progreso');
    }

    /**
     * Obtiene un resumen de estadísticas
     * @param {string} fechaInicio - Fecha de inicio (opcional)
     * @param {string} fechaFin - Fecha de fin (opcional)
     * @returns {Promise<Object>} Resumen
     */
    static async obtenerResumen(fechaInicio = null, fechaFin = null) {
        let url = '/estadisticas/resumen?';
        if (fechaInicio) url += `fechaInicio=${fechaInicio}&`;
        if (fechaFin) url += `fechaFin=${fechaFin}`;
        return this.request(url);
    }
}

// Exportar para uso global
window.API = API;
