/**
 * Materias - Gestión de materias
 * OKtask
 */

class Materias {
    constructor() {
        this.materias = [];
        this.vistaActual = 'grid'; // 'grid' o 'table'
        this.init();
    }

    init() {
        this.bindEvents();
    }

    bindEvents() {
        // Botón nueva materia
        document.getElementById('btn-nueva-materia')?.addEventListener('click', () => {
            this.abrirModalCrear();
        });

        // Botón cambiar vista
        document.getElementById('btn-vista-materias')?.addEventListener('click', () => {
            this.toggleVista();
        });

        // Formulario de materia
        document.getElementById('form-materia')?.addEventListener('submit', (e) => {
            e.preventDefault();
            this.guardarMateria();
        });

        // Color picker
        document.getElementById('materia-color')?.addEventListener('input', (e) => {
            document.getElementById('materia-color-hex').textContent = e.target.value.toUpperCase();
        });
    }

    /**
     * Carga las materias desde la API
     */
    async cargarMaterias() {
        try {
            this.materias = await API.listarMaterias();
            this.renderizar();
            this.actualizarSelectMaterias();
            return this.materias;
        } catch (error) {
            Utils.mostrarToast('Error', 'No se pudieron cargar las materias', 'error');
            return [];
        }
    }

    /**
     * Renderiza las materias según la vista actual
     */
    renderizar() {
        if (this.vistaActual === 'grid') {
            this.renderizarGrid();
        } else {
            this.renderizarTabla();
        }
    }

    /**
     * Renderiza las materias en vista de cards
     */
    renderizarGrid() {
        const container = document.getElementById('materias-grid');
        if (!container) return;

        if (this.materias.length === 0) {
            container.innerHTML = `
                <div class="text-center p-4">
                    <i class="fas fa-book fa-3x text-muted mb-3"></i>
                    <p class="text-muted">No hay materias creadas</p>
                    <button class="btn btn-primary" onclick="materias.abrirModalCrear()">
                        <i class="fas fa-plus"></i> Crear primera materia
                    </button>
                </div>
            `;
            return;
        }

        container.innerHTML = this.materias.map(materia => `
            <div class="materia-card" data-id="${materia.id}">
                <div class="materia-card-color" style="background-color: ${materia.color}"></div>
                <div class="materia-card-body">
                    <div class="materia-card-header">
                        <span class="materia-card-codigo">${materia.codigo}</span>
                        <div class="materia-card-actions">
                            <button class="btn btn-sm btn-outline-secondary" onclick="materias.abrirModalEditar('${materia.id}')" title="Editar">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-danger" onclick="materias.eliminarMateria('${materia.id}')" title="Eliminar">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </div>
                    <h3 class="materia-card-nombre">${materia.nombre}</h3>
                    <div class="materia-card-stats">
                        <div class="materia-stat">
                            <span class="materia-stat-value">${materia.prioridad}</span>
                            <span class="materia-stat-label">Prioridad</span>
                        </div>
                    </div>
                </div>
            </div>
        `).join('');
    }

    /**
     * Renderiza las materias en vista de tabla
     */
    renderizarTabla() {
        const tbody = document.getElementById('materias-tbody');
        if (!tbody) return;

        if (this.materias.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center p-4">
                        <p class="text-muted">No hay materias creadas</p>
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = this.materias.map(materia => `
            <tr>
                <td><strong>${materia.codigo}</strong></td>
                <td>${materia.nombre}</td>
                <td><span class="materia-color-badge" style="background-color: ${materia.color}"></span></td>
                <td>${materia.prioridad}</td>
                <td>-</td>
                <td>-</td>
                <td>
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-secondary" onclick="materias.abrirModalEditar('${materia.id}')" title="Editar">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-outline-danger" onclick="materias.eliminarMateria('${materia.id}')" title="Eliminar">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    /**
     * Alterna entre vista grid y tabla
     */
    toggleVista() {
        const grid = document.getElementById('materias-grid');
        const table = document.getElementById('materias-table');
        const btn = document.getElementById('btn-vista-materias');

        if (this.vistaActual === 'grid') {
            this.vistaActual = 'table';
            grid?.classList.add('d-none');
            table?.classList.remove('d-none');
            btn.innerHTML = '<i class="fas fa-th"></i>';
        } else {
            this.vistaActual = 'grid';
            table?.classList.add('d-none');
            grid?.classList.remove('d-none');
            btn.innerHTML = '<i class="fas fa-list"></i>';
        }

        this.renderizar();
    }

    /**
     * Abre el modal para crear una materia
     */
    abrirModalCrear() {
        document.getElementById('modal-materia-title').textContent = 'Nueva Materia';
        document.getElementById('form-materia').reset();
        document.getElementById('materia-id').value = '';
        document.getElementById('materia-color').value = '#7C3AED';
        document.getElementById('materia-color-hex').textContent = '#7C3AED';
        document.getElementById('materia-codigo').removeAttribute('readonly');
        
        const modal = new bootstrap.Modal(document.getElementById('modal-materia'));
        modal.show();
    }

    /**
     * Abre el modal para editar una materia
     * @param {string} id - ID de la materia
     */
    async abrirModalEditar(id) {
        const materia = this.materias.find(m => m.id === id);
        if (!materia) return;

        document.getElementById('modal-materia-title').textContent = 'Editar Materia';
        document.getElementById('materia-id').value = materia.id;
        document.getElementById('materia-nombre').value = materia.nombre;
        document.getElementById('materia-codigo').value = materia.codigo;
        document.getElementById('materia-codigo').setAttribute('readonly', true);
        document.getElementById('materia-color').value = materia.color;
        document.getElementById('materia-color-hex').textContent = materia.color;
        document.getElementById('materia-prioridad').value = materia.prioridad;
        
        const modal = new bootstrap.Modal(document.getElementById('modal-materia'));
        modal.show();
    }

    /**
     * Guarda una materia (crea o actualiza)
     */
    async guardarMateria() {
        const id = document.getElementById('materia-id').value;
        const datos = {
            nombre: document.getElementById('materia-nombre').value.trim(),
            codigo: document.getElementById('materia-codigo').value.trim().toUpperCase(),
            color: document.getElementById('materia-color').value,
            prioridad: document.getElementById('materia-prioridad').value
        };

        try {
            if (id) {
                // Editar
                await API.editarMateria(id, {
                    nombre: datos.nombre,
                    color: datos.color,
                    prioridad: datos.prioridad
                });
                Utils.mostrarToast('Éxito', 'Materia actualizada correctamente', 'success');
            } else {
                // Crear
                await API.crearMateria(datos);
                Utils.mostrarToast('Éxito', 'Materia creada correctamente', 'success');
            }

            bootstrap.Modal.getInstance(document.getElementById('modal-materia'))?.hide();
            await this.cargarMaterias();
            
            // Recargar tareas si están visibles
            if (window.tareas) {
                await window.tareas.cargarTareas();
            }
        } catch (error) {
            Utils.mostrarToast('Error', error.message || 'No se pudo guardar la materia', 'error');
        }
    }

    /**
     * Elimina una materia
     * @param {string} id - ID de la materia
     */
    async eliminarMateria(id) {
        const materia = this.materias.find(m => m.id === id);
        if (!materia) return;

        const confirmado = await Utils.confirmar(
            `¿Estás seguro de que querés eliminar la materia "${materia.nombre}"?`
        );

        if (!confirmado) return;

        try {
            await API.eliminarMateria(id);
            Utils.mostrarToast('Éxito', 'Materia eliminada correctamente', 'success');
            await this.cargarMaterias();
            
            // Recargar tareas si están visibles
            if (window.tareas) {
                await window.tareas.cargarTareas();
            }
        } catch (error) {
            Utils.mostrarToast('Error', error.message || 'No se pudo eliminar la materia', 'error');
        }
    }

    /**
     * Actualiza el select de materias en formularios
     */
    actualizarSelectMaterias() {
        const selects = document.querySelectorAll('#tarea-materia, #pomodoro-select-tarea, #filter-materia-tareas');
        
        selects.forEach(select => {
            const valorActual = select.value;
            const primeraOpcion = select.querySelector('option:first-child');
            
            // Mantener primera opción y limpiar el resto
            select.innerHTML = '';
            if (primeraOpcion) {
                select.appendChild(primeraOpcion);
            }
            
            // Agregar materias
            this.materias.forEach(materia => {
                const option = document.createElement('option');
                option.value = materia.id;
                option.textContent = `${materia.codigo} - ${materia.nombre}`;
                select.appendChild(option);
            });
            
            // Restaurar valor seleccionado
            if (valorActual) {
                select.value = valorActual;
            }
        });
    }

    /**
     * Obtiene una materia por ID
     * @param {string} id - ID de la materia
     * @returns {Object|null} Materia encontrada
     */
    obtenerMateria(id) {
        return this.materias.find(m => m.id === id) || null;
    }
}

// Crear instancia global
window.materias = new Materias();
