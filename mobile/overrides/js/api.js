/**
 * API - Comunicación con el Backend
 * OKtask
 * 
 * En mobile, delega a APILocal (localStorage).
 * En desktop, usa fetch contra el backend Java.
 */

const API_BASE = '/api';
const _isMobile = /android/i.test(navigator.userAgent);

class API {
    static async request(endpoint, options = {}) {
        if (_isMobile && window.APILocal) {
            // Delegar a la API local - cada método estático lo maneja
            throw new Error('request() no debería llamarse en mobile');
        }
        const url = `${API_BASE}${endpoint}`;
        const config = { headers: { 'Content-Type': 'application/json' }, ...options };
        try {
            const response = await fetch(url, config);
            if (!response.ok) {
                const error = await response.json().catch(() => ({}));
                throw new Error(error.message || `Error HTTP: ${response.status}`);
            }
            if (response.status === 204) return null;
            return await response.json();
        } catch (error) {
            console.error(`Error en petición a ${endpoint}:`, error);
            throw error;
        }
    }

    // MATERIAS
    static async listarMaterias() {
        if (_isMobile) return window.APILocal.listarMaterias();
        return this.request('/materias');
    }
    static async obtenerMateria(id) {
        if (_isMobile) return window.APILocal.obtenerMateria(id);
        return this.request(`/materias/${id}`);
    }
    static async crearMateria(m) {
        if (_isMobile) return window.APILocal.crearMateria(m);
        return this.request('/materias', { method: 'POST', body: JSON.stringify(m) });
    }
    static async editarMateria(id, m) {
        if (_isMobile) return window.APILocal.editarMateria(id, m);
        return this.request(`/materias/${id}`, { method: 'PUT', body: JSON.stringify(m) });
    }
    static async eliminarMateria(id) {
        if (_isMobile) return window.APILocal.eliminarMateria(id);
        return this.request(`/materias/${id}`, { method: 'DELETE' });
    }

    // TAREAS
    static async listarTareas() {
        if (_isMobile) return window.APILocal.listarTareas();
        return this.request('/tareas');
    }
    static async obtenerTarea(id) {
        if (_isMobile) return window.APILocal.obtenerTarea(id);
        return this.request(`/tareas/${id}`);
    }
    static async listarTareasPorMateria(mid) {
        if (_isMobile) return window.APILocal.listarTareasPorMateria(mid);
        return this.request(`/tareas/materia/${mid}`);
    }
    static async listarTareasPorEstado(e) {
        if (_isMobile) return window.APILocal.listarTareasPorEstado(e);
        return this.request(`/tareas/estado/${e}`);
    }
    static async crearTarea(t) {
        if (_isMobile) return window.APILocal.crearTarea(t);
        return this.request('/tareas', { method: 'POST', body: JSON.stringify(t) });
    }
    static async editarTarea(id, t) {
        if (_isMobile) return window.APILocal.editarTarea(id, t);
        return this.request(`/tareas/${id}`, { method: 'PUT', body: JSON.stringify(t) });
    }
    static async cambiarEstadoTarea(id, estado) {
        if (_isMobile) return window.APILocal.cambiarEstadoTarea(id, estado);
        return this.request(`/tareas/${id}`, { method: 'PUT', body: JSON.stringify({ estado }) });
    }
    static async eliminarTarea(id) {
        if (_isMobile) return window.APILocal.eliminarTarea(id);
        return this.request(`/tareas/${id}`, { method: 'DELETE' });
    }

    // POMODORO
    static async iniciarSesionPomodoro(s) {
        if (_isMobile) return window.APILocal.iniciarSesionPomodoro(s);
        return this.request('/pomodoro/iniciar', { method: 'POST', body: JSON.stringify(s) });
    }
    static async finalizarSesionPomodoro(id) {
        if (_isMobile) return window.APILocal.finalizarSesionPomodoro(id);
        return this.request(`/pomodoro/${id}/finalizar`, { method: 'POST' });
    }
    static async cancelarSesionPomodoro(id, mins) {
        if (_isMobile) return window.APILocal.cancelarSesionPomodoro(id, mins);
        return this.request(`/pomodoro/${id}/cancelar`, { method: 'POST', body: JSON.stringify({ minutosTranscurridos: mins }) });
    }
    static async obtenerSesionesTarea(tid) {
        if (_isMobile) return window.APILocal.obtenerSesionesTarea(tid);
        return this.request(`/pomodoro/tarea/${tid}`);
    }
    static async obtenerSesionesMateria(mid) {
        if (_isMobile) return window.APILocal.obtenerSesionesMateria(mid);
        return this.request(`/pomodoro/materia/${mid}`);
    }
    static async obtenerConfiguracionPomodoro() {
        if (_isMobile) return window.APILocal.obtenerConfiguracionPomodoro();
        return this.request('/pomodoro/configuracion');
    }
    static async actualizarConfiguracionPomodoro(c) {
        if (_isMobile) return window.APILocal.actualizarConfiguracionPomodoro(c);
        return this.request('/pomodoro/configuracion', { method: 'PUT', body: JSON.stringify(c) });
    }

    // ESTADISTICAS
    static async obtenerHorasPorMateria() {
        if (_isMobile) return window.APILocal.obtenerHorasPorMateria();
        return this.request('/estadisticas/horas');
    }
    static async obtenerHorasPorPeriodo(fi, ff) {
        if (_isMobile) return window.APILocal.obtenerHorasPorPeriodo(fi, ff);
        return this.request(`/estadisticas/horas/periodo?fechaInicio=${fi}&fechaFin=${ff}`);
    }
    static async obtenerProgresoTareas() {
        if (_isMobile) return window.APILocal.obtenerProgresoTareas();
        return this.request('/estadisticas/progreso');
    }
    static async obtenerResumen(fi, ff) {
        if (_isMobile) return window.APILocal.obtenerResumen(fi, ff);
        let url = '/estadisticas/resumen?';
        if (fi) url += `fechaInicio=${fi}&`;
        if (ff) url += `fechaFin=${ff}`;
        return this.request(url);
    }
}

window.API = API;
