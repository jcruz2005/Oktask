/**
 * Tareas - Gestión de tareas con Drag & Drop
 * OKtask
 */

class Tareas {
    constructor() {
        this.tareas = [];
        this.tareasPorMateria = {};
        this.init();
    }

    init() {
        this.bindEvents();
    }

    bindEvents() {
        // Botón nueva tarea
        document.getElementById('btn-nueva-tarea')?.addEventListener('click', () => {
            this.abrirModalCrear();
        });

        // Formulario de tarea
        document.getElementById('form-tarea')?.addEventListener('submit', (e) => {
            e.preventDefault();
            this.guardarTarea();
        });

        // Filtros
        document.getElementById('filter-materia-tareas')?.addEventListener('change', () => {
            this.renderizar();
        });

        document.getElementById('filter-estado-tareas')?.addEventListener('change', () => {
            this.renderizar();
        });
    }

    /**
     * Carga las tareas desde la API
     */
    async cargarTareas() {
        try {
            this.tareas = await API.listarTareas();
            this.agruparPorMateria();
            this.renderizar();
            this.actualizarEstadisticas();
            return this.tareas;
        } catch (error) {
            Utils.mostrarToast('Error', 'No se pudieron cargar las tareas', 'error');
            return [];
        }
    }

    /**
     * Agrupa las tareas por materia
     */
    agruparPorMateria() {
        this.tareasPorMateria = {};
        
        this.tareas.forEach(tarea => {
            if (!this.tareasPorMateria[tarea.materiaId]) {
                this.tareasPorMateria[tarea.materiaId] = [];
            }
            this.tareasPorMateria[tarea.materiaId].push(tarea);
        });
    }

    /**
     * Renderiza las tareas agrupadas por materia
     */
    renderizar() {
        const container = document.getElementById('tareas-por-materia');
        if (!container) return;

        const filtroMateria = document.getElementById('filter-materia-tareas')?.value || '';
        const filtroEstado = document.getElementById('filter-estado-tareas')?.value || '';

        // Filtrar materias
        let materiasFiltradas = Object.keys(this.tareasPorMateria);
        
        if (filtroMateria) {
            materiasFiltradas = materiasFiltradas.filter(id => id === filtroMateria);
        }

        if (materiasFiltradas.length === 0) {
            container.innerHTML = `
                <div class="text-center p-4">
                    <i class="fas fa-tasks fa-3x text-muted mb-3"></i>
                    <p class="text-muted">No hay tareas para mostrar</p>
                    <button class="btn btn-primary" onclick="tareas.abrirModalCrear()">
                        <i class="fas fa-plus"></i> Crear primera tarea
                    </button>
                </div>
            `;
            return;
        }

        container.innerHTML = materiasFiltradas.map(materiaId => {
            const materia = window.materias?.obtenerMateria(materiaId);
            const tareas = this.filtrarTareas(this.tareasPorMateria[materiaId], filtroEstado);
            
            return this.renderizarSeccionMateria(materia, tareas);
        }).join('');

        // Inicializar Drag & Drop
        this.inicializarDragDrop();
    }

    /**
     * Filtra tareas por estado
     * @param {Array} tareas - Lista de tareas
     * @param {string} estado - Estado a filtrar
     * @returns {Array} Tareas filtradas
     */
    filtrarTareas(tareas, estado) {
        if (!estado) return tareas;
        return tareas.filter(t => t.estado === estado);
    }

    /**
     * Renderiza la sección de tareas de una materia
     * @param {Object} materia - Materia
     * @param {Array} tareas - Tareas de la materia
     * @returns {string} HTML generado
     */
    renderizarSeccionMateria(materia, tareas) {
        if (!materia) return '';

        const pendientes = tareas.filter(t => t.estado === 'PENDIENTE');
        const enProgreso = tareas.filter(t => t.estado === 'EN_PROGRESO');
        const completadas = tareas.filter(t => t.estado === 'COMPLETADA');

        return `
            <div class="materia-tasks-section" data-materia-id="${materia.id}">
                <div class="materia-tasks-header">
                    <div class="materia-tasks-color" style="background-color: ${materia.color}"></div>
                    <h3 class="materia-tasks-title">${materia.nombre}</h3>
                    <span class="materia-tasks-count">${tareas.length} tareas</span>
                </div>
                <div class="materia-tasks-board">
                    <div class="task-column" data-estado="PENDIENTE">
                        <div class="task-column-header pendiente">
                            <i class="fas fa-clock"></i>
                            Pendiente
                            <span class="task-column-count">${pendientes.length}</span>
                        </div>
                        <div class="task-list" data-estado="PENDIENTE" data-materia="${materia.id}">
                            ${pendientes.map(t => this.renderizarTarea(t, materia)).join('')}
                        </div>
                    </div>
                    <div class="task-column" data-estado="EN_PROGRESO">
                        <div class="task-column-header en-progreso">
                            <i class="fas fa-spinner"></i>
                            En Progreso
                            <span class="task-column-count">${enProgreso.length}</span>
                        </div>
                        <div class="task-list" data-estado="EN_PROGRESO" data-materia="${materia.id}">
                            ${enProgreso.map(t => this.renderizarTarea(t, materia)).join('')}
                        </div>
                    </div>
                    <div class="task-column" data-estado="COMPLETADA">
                        <div class="task-column-header completada">
                            <i class="fas fa-check"></i>
                            Completada
                            <span class="task-column-count">${completadas.length}</span>
                        </div>
                        <div class="task-list" data-estado="COMPLETADA" data-materia="${materia.id}">
                            ${completadas.map(t => this.renderizarTarea(t, materia)).join('')}
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    /**
     * Renderiza una tarea individual
     * @param {Object} tarea - Tarea a renderizar
     * @param {Object} materia - Materia de la tarea
     * @returns {string} HTML generado
     */
    renderizarTarea(tarea, materia) {
        const diasRestantes = Utils.diasRestantes(tarea.fechaLimite);
        const claseUrgencia = Utils.obtenerClaseUrgencia(tarea.fechaLimite);
        const textoDias = Utils.textoDiasRestantes(tarea.fechaLimite);
        const clasePrioridad = tarea.prioridad.toLowerCase();

        return `
            <div class="task-card" data-id="${tarea.id}" draggable="true">
                <div class="task-card-title">${tarea.titulo}</div>
                <div class="task-card-meta">
                    <span class="task-card-priority ${clasePrioridad}">${tarea.prioridad}</span>
                    <span class="task-due ${claseUrgencia}">${textoDias}</span>
                </div>
                ${tarea.descripcion ? `<div class="task-card-description text-muted">${this.truncarTexto(tarea.descripcion, 50)}</div>` : ''}
                <div class="task-card-actions">
                    <button class="btn btn-sm btn-outline-secondary" onclick="tareas.abrirModalEditar('${tarea.id}')" title="Editar">
                        <i class="fas fa-edit"></i>
                    </button>
                    ${tarea.estado !== 'COMPLETADA' ? `
                        <button class="btn btn-sm btn-outline-success" onclick="tareas.iniciarPomodoro('${tarea.id}', '${materia.id}')" title="Iniciar Pomodoro">
                            <i class="fas fa-clock"></i>
                        </button>
                    ` : ''}
                    <button class="btn btn-sm btn-outline-danger" onclick="tareas.eliminarTarea('${tarea.id}')" title="Eliminar">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
        `;
    }

    /**
     * Trunca un texto a cierta longitud
     * @param {string} texto - Texto a truncar
     * @param {number} longitud - Longitud máxima
     * @returns {string} Texto truncado
     */
    truncarTexto(texto, longitud) {
        if (!texto || texto.length <= longitud) return texto || '';
        return texto.substring(0, longitud) + '...';
    }

    /**
     * Inicializa el Drag & Drop
     */
    inicializarDragDrop() {
        const listas = document.querySelectorAll('.task-list');
        
        listas.forEach(lista => {
            new Sortable(lista, {
                group: 'tareas',
                animation: 200,
                ghostClass: 'dragging',
                onEnd: (evt) => {
                    const tareaId = evt.item.dataset.id;
                    const nuevoEstado = evt.to.dataset.estado;
                    this.cambiarEstadoTarea(tareaId, nuevoEstado);
                }
            });
        });
    }

    /**
     * Cambia el estado de una tarea
     * @param {string} tareaId - ID de la tarea
     * @param {string} nuevoEstado - Nuevo estado
     */
    async cambiarEstadoTarea(tareaId, nuevoEstado) {
        try {
            await API.cambiarEstadoTarea(tareaId, nuevoEstado);
            
            // Actualizar localmente
            const tarea = this.tareas.find(t => t.id === tareaId);
            if (tarea) {
                tarea.estado = nuevoEstado;
                if (nuevoEstado === 'COMPLETADA') {
                    tarea.fechaCompletado = new Date().toISOString();
                }
            }
            
            this.agruparPorMateria();
            this.renderizar();
            this.actualizarEstadisticas();
            
            Utils.mostrarToast('Éxito', `Tarea movida a ${this.formatearEstado(nuevoEstado)}`, 'success');
        } catch (error) {
            Utils.mostrarToast('Error', 'No se pudo cambiar el estado de la tarea', 'error');
            await this.cargarTareas(); // Recargar para restaurar estado
        }
    }

    /**
     * Formatea el estado para mostrar
     * @param {string} estado - Estado
     * @returns {string} Estado formateado
     */
    formatearEstado(estado) {
        const estados = {
            'PENDIENTE': 'Pendiente',
            'EN_PROGRESO': 'En Progreso',
            'COMPLETADA': 'Completada'
        };
        return estados[estado] || estado;
    }

    /**
     * Abre el modal para crear una tarea
     */
    abrirModalCrear() {
        document.getElementById('modal-tarea-title').textContent = 'Nueva Tarea';
        document.getElementById('form-tarea').reset();
        document.getElementById('tarea-id').value = '';
        
        // Establecer fecha mínima (hoy)
        document.getElementById('tarea-fecha-limite').min = Utils.hoy();
        document.getElementById('tarea-fecha-limite').value = Utils.hoy();
        
        // Actualizar select de materias
        window.materias?.actualizarSelectMaterias();
        
        const modal = new bootstrap.Modal(document.getElementById('modal-tarea'));
        modal.show();
    }

    /**
     * Abre el modal para editar una tarea
     * @param {string} id - ID de la tarea
     */
    async abrirModalEditar(id) {
        const tarea = this.tareas.find(t => t.id === id);
        if (!tarea) return;

        document.getElementById('modal-tarea-title').textContent = 'Editar Tarea';
        document.getElementById('tarea-id').value = tarea.id;
        document.getElementById('tarea-titulo').value = tarea.titulo;
        document.getElementById('tarea-descripcion').value = tarea.descripcion || '';
        document.getElementById('tarea-materia').value = tarea.materiaId;
        document.getElementById('tarea-fecha-limite').value = tarea.fechaLimite;
        document.getElementById('tarea-prioridad').value = tarea.prioridad;
        
        // Actualizar select de materias
        window.materias?.actualizarSelectMaterias();
        document.getElementById('tarea-materia').value = tarea.materiaId;
        
        const modal = new bootstrap.Modal(document.getElementById('modal-tarea'));
        modal.show();
    }

    /**
     * Guarda una tarea (crea o actualiza)
     */
    async guardarTarea() {
        const id = document.getElementById('tarea-id').value;
        const datos = {
            titulo: document.getElementById('tarea-titulo').value.trim(),
            descripcion: document.getElementById('tarea-descripcion').value.trim(),
            materiaId: document.getElementById('tarea-materia').value,
            fechaLimite: document.getElementById('tarea-fecha-limite').value,
            prioridad: document.getElementById('tarea-prioridad').value
        };

        try {
            if (id) {
                await API.editarTarea(id, datos);
                Utils.mostrarToast('Éxito', 'Tarea actualizada correctamente', 'success');
            } else {
                await API.crearTarea(datos);
                Utils.mostrarToast('Éxito', 'Tarea creada correctamente', 'success');
            }

            bootstrap.Modal.getInstance(document.getElementById('modal-tarea'))?.hide();
            await this.cargarTareas();
            
            // Actualizar lista de tareas en Pomodoro si está visible
            if (window.app?.tabActual === 'pomodoro') {
                window.pomodoro?.cargarTareasPendientes();
            }
        } catch (error) {
            Utils.mostrarToast('Error', error.message || 'No se pudo guardar la tarea', 'error');
        }
    }

    /**
     * Elimina una tarea
     * @param {string} id - ID de la tarea
     */
    async eliminarTarea(id) {
        const tarea = this.tareas.find(t => t.id === id);
        if (!tarea) return;

        const confirmado = await Utils.confirmar(
            `¿Estás seguro de que querés eliminar la tarea "${tarea.titulo}"?`
        );

        if (!confirmado) return;

        try {
            await API.eliminarTarea(id);
            Utils.mostrarToast('Éxito', 'Tarea eliminada correctamente', 'success');
            await this.cargarTareas();
            
            // Actualizar lista de tareas en Pomodoro si está visible
            if (window.app?.tabActual === 'pomodoro') {
                window.pomodoro?.cargarTareasPendientes();
            }
        } catch (error) {
            Utils.mostrarToast('Error', error.message || 'No se pudo eliminar la tarea', 'error');
        }
    }

    /**
     * Inicia un Pomodoro para una tarea
     * @param {string} tareaId - ID de la tarea
     * @param {string} materiaId - ID de la materia
     */
    iniciarPomodoro(tareaId, materiaId) {
        if (window.pomodoro) {
            window.pomodoro.iniciarParaTarea(tareaId, materiaId);
        }
    }

    /**
     * Actualiza las estadísticas del dashboard
     */
    actualizarEstadisticas() {
        const pendientes = this.tareas.filter(t => t.estado === 'PENDIENTE').length;
        const completadas = this.tareas.filter(t => t.estado === 'COMPLETADA').length;
        
        document.getElementById('stat-tareas-pendientes').textContent = pendientes;
        document.getElementById('stat-tareas-completadas').textContent = completadas;
    }

    /**
     * Obtiene las tareas próximas a vencer
     * @param {number} dias - Número de días a buscar
     * @returns {Array} Tareas próximas
     */
    obtenerTareasProximas(dias = 7) {
        return this.tareas
            .filter(t => {
                if (t.estado === 'COMPLETADA') return false;
                const diasRest = Utils.diasRestantes(t.fechaLimite);
                return diasRest >= 0 && diasRest <= dias;
            })
            .sort((a, b) => new Date(a.fechaLimite) - new Date(b.fechaLimite));
    }

    /**
     * Obtiene tareas por materia
     * @param {string} materiaId - ID de la materia
     * @returns {Array} Tareas de la materia
     */
    obtenerTareasPorMateria(materiaId) {
        return this.tareas.filter(t => t.materiaId === materiaId);
    }
}

// Crear instancia global
window.tareas = new Tareas();
