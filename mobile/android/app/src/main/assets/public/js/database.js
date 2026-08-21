/**
 * Database - Persistencia local con localStorage
 * OKtask Mobile
 */
class Database {
    constructor() { this.prefix = 'oktask_'; }

    generateId() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
            const r = Math.random() * 16 | 0;
            return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
        });
    }

    now() { return new Date().toISOString().replace('T', ' ').substring(0, 19); }
    today() { return new Date().toISOString().substring(0, 10); }

    getAll(c) { const d = localStorage.getItem(this.prefix + c); return d ? JSON.parse(d) : []; }
    saveAll(c, d) { localStorage.setItem(this.prefix + c, JSON.stringify(d)); }
    findById(c, id) { return this.getAll(c).find(i => i.id === id) || null; }

    insert(c, item) {
        const items = this.getAll(c);
        item.id = item.id || this.generateId();
        item.fechaCreacion = item.fechaCreacion || this.now();
        items.push(item);
        this.saveAll(c, items);
        return item;
    }

    update(c, id, updates) {
        const items = this.getAll(c);
        const i = items.findIndex(x => x.id === id);
        if (i === -1) return null;
        items[i] = { ...items[i], ...updates };
        this.saveAll(c, items);
        return items[i];
    }

    delete(c, id) {
        const items = this.getAll(c);
        const f = items.filter(x => x.id !== id);
        this.saveAll(c, f);
        return f.length < items.length;
    }

    // MATERIAS
    crearMateria(d) { return this.insert('materias', { nombre: d.nombre, codigo: d.codigo, color: d.color, prioridad: d.prioridad, activa: true }); }
    listarMaterias() { return this.getAll('materias').filter(m => m.activa !== false); }
    obtenerMateria(id) { return this.findById('materias', id); }
    editarMateria(id, d) { return this.update('materias', id, d); }
    eliminarMateria(id) { return this.update('materias', id, { activa: false }); }

    // TAREAS
    crearTarea(d) {
        const m = this.obtenerMateria(d.materiaId);
        return this.insert('tareas', {
            titulo: d.titulo, descripcion: d.descripcion || '', materiaId: d.materiaId,
            nombreMateria: m ? m.nombre : null, fechaLimite: d.fechaLimite,
            prioridad: d.prioridad, estado: 'PENDIENTE', fechaCompletado: null, minutosPomodoro: 0
        });
    }
    listarTareas() {
        return this.getAll('tareas').map(t => {
            if (!t.nombreMateria && t.materiaId) {
                const m = this.obtenerMateria(t.materiaId);
                t.nombreMateria = m ? m.nombre : 'Sin materia';
            }
            return t;
        });
    }
    obtenerTarea(id) { return this.findById('tareas', id); }
    listarTareasPorMateria(mid) { return this.listarTareas().filter(t => t.materiaId === mid); }
    listarTareasPorEstado(e) { return this.listarTareas().filter(t => t.estado === e); }
    editarTarea(id, d) {
        const t = this.obtenerTarea(id);
        if (!t) return null;
        if (d.estado === 'COMPLETADA' && t.estado !== 'COMPLETADA') d.fechaCompletado = this.now();
        return this.update('tareas', id, d);
    }
    eliminarTarea(id) { return this.delete('tareas', id); }

    // POMODORO
    crearSesionPomodoro(d) { return this.insert('sesiones', { tareaId: d.tareaId, materiaId: d.materiaId, duracionMinutos: d.duracionMinutos, tipoSesion: 'TRABAJO', fechaFin: null, completada: false }); }
    obtenerSesion(id) { return this.findById('sesiones', id); }
    finalizarSesion(id) { return this.update('sesiones', id, { fechaFin: this.now(), completada: true }); }
    cancelarSesion(id, mins) {
        const s = this.obtenerSesion(id);
        if (!s) return null;
        const i = new Date(s.fechaInicio || this.now());
        i.setMinutes(i.getMinutes() + Math.max(mins, 1));
        return this.update('sesiones', id, { fechaFin: i.toISOString().replace('T', ' ').substring(0, 19), completada: true });
    }
    listarSesionesPorTarea(tid) { return this.getAll('sesiones').filter(s => s.tareaId === tid); }
    listarSesionesPorMateria(mid) { return this.getAll('sesiones').filter(s => s.materiaId === mid); }

    // CONFIG
    obtenerConfiguracion() {
        let c = this.findById('configuracion', 'default');
        if (!c) c = this.insert('configuracion', { id: 'default', duracionTrabajo: 25, duracionDescanso: 5, duracionDescansoLargo: 15, pomodorosParaDescansoLargo: 4, activa: true });
        return c;
    }
    actualizarConfiguracion(d) { return this.update('configuracion', 'default', d); }

    // ESTADISTICAS
    calcularEstadisticas() {
        const ms = this.listarMaterias(), ts = this.listarTareas(), ss = this.getAll('sesiones').filter(s => s.completada);
        return ms.map(m => {
            const tm = ts.filter(t => t.materiaId === m.id);
            const sm = ss.filter(s => s.materiaId === m.id);
            const tc = tm.filter(t => t.estado === 'COMPLETADA');
            const mins = sm.reduce((s, x) => s + (x.duracionMinutos || 0), 0);
            return { materiaId: m.id, nombreMateria: m.nombre, codigoMateria: m.codigo, horasEstudiadas: Math.round((mins / 60) * 100) / 100, pomodorosCompletados: sm.length, tareasTotales: tm.length, tareasCompletadas: tc.length, porcentajeProgreso: tm.length > 0 ? Math.round((tc.length / tm.length) * 100) : 0 };
        });
    }
    calcularResumen(fi, ff) {
        let ss = this.getAll('sesiones').filter(s => s.completada);
        if (fi) ss = ss.filter(s => s.fechaInicio >= fi);
        if (ff) ss = ss.filter(s => s.fechaInicio <= ff + ' 23:59:59');
        const mins = ss.reduce((a, s) => a + (s.duracionMinutos || 0), 0);
        const dias = fi && ff ? Math.max(1, Math.ceil((new Date(ff) - new Date(fi)) / 86400000)) : 1;
        return { fechaInicio: fi || this.today(), fechaFin: ff || this.today(), totalHoras: Math.round((mins / 60) * 100) / 100, totalPomodoros: ss.length, promedioHorasDiarias: Math.round((mins / 60 / dias) * 100) / 100 };
    }
}

window.db = new Database();
