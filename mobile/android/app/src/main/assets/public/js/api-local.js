/**
 * API Local - Implementación local de la API REST
 * OKtask Mobile
 * 
 * Replica las mismas funciones que API pero usando localStorage
 * en vez de hacer llamadas HTTP al backend.
 */

class APILocal {
    // MATERIAS
    static async listarMaterias() { return window.db.listarMaterias(); }
    static async obtenerMateria(id) { return window.db.obtenerMateria(id); }
    static async crearMateria(m) { return window.db.crearMateria(m); }
    static async editarMateria(id, m) { return window.db.editarMateria(id, m); }
    static async eliminarMateria(id) { return window.db.eliminarMateria(id); }

    // TAREAS
    static async listarTareas() { return window.db.listarTareas(); }
    static async obtenerTarea(id) { return window.db.obtenerTarea(id); }
    static async listarTareasPorMateria(mid) { return window.db.listarTareasPorMateria(mid); }
    static async listarTareasPorEstado(e) { return window.db.listarTareasPorEstado(e); }
    static async crearTarea(t) { return window.db.crearTarea(t); }
    static async editarTarea(id, t) { return window.db.editarTarea(id, t); }
    static async cambiarEstadoTarea(id, estado) { return window.db.editarTarea(id, { estado }); }
    static async eliminarTarea(id) { return window.db.eliminarTarea(id); }

    // POMODORO
    static async iniciarSesionPomodoro(s) { return window.db.crearSesionPomodoro(s); }
    static async finalizarSesionPomodoro(id) { return window.db.finalizarSesion(id); }
    static async cancelarSesionPomodoro(id, mins) { return window.db.cancelarSesion(id, mins); }
    static async obtenerSesionesTarea(tid) { return window.db.listarSesionesPorTarea(tid); }
    static async obtenerSesionesMateria(mid) { return window.db.listarSesionesPorMateria(mid); }
    static async obtenerConfiguracionPomodoro() { return window.db.obtenerConfiguracion(); }
    static async actualizarConfiguracionPomodoro(c) { return window.db.actualizarConfiguracion(c); }

    // ESTADISTICAS
    static async obtenerHorasPorMateria() { return window.db.calcularEstadisticas(); }
    static async obtenerHorasPorPeriodo(fi, ff) { return window.db.calcularEstadisticas(); }
    static async obtenerProgresoTareas() { return window.db.calcularEstadisticas(); }
    static async obtenerResumen(fi, ff) { return window.db.calcularResumen(fi, ff); }

    // UPDATE
    static async checkForUpdates(platform) {
        try {
            const resp = await fetch('https://raw.githubusercontent.com/jcruz2005/Oktask/main/version.json');
            const data = await resp.json();
            return data;
        } catch (e) {
            console.error('Error checking updates:', e);
            return null;
        }
    }
}

window.APILocal = APILocal;
