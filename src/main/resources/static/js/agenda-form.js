/**
 * AgendaForm - Modal Bootstrap para crear/editar bloques
 * OKtask
 */

class AgendaForm {
    constructor() {
        this.modal = null;
        this.modalEl = null;
        this.form = null;
        this.modo = 'crear'; // 'crear' | 'editar'
        this.bloqueEditando = null;
        this.callbackGuardar = null;
        this.init();
    }

    init() {
        this.crearModalHTML();
        this.modalEl = document.getElementById('modal-bloque');
        this.modal = new bootstrap.Modal(this.modalEl, { backdrop: 'static', keyboard: true });
        this.form = document.getElementById('form-bloque');
        this.bindEvents();
        this.cargarMaterias();
    }

    crearModalHTML() {
        if (document.getElementById('modal-bloque')) return;

        const html = `
            <div class="modal fade" id="modal-bloque" tabindex="-1" aria-labelledby="modal-bloque-label" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header" id="modal-bloque-header">
                            <h5 class="modal-title" id="modal-bloque-label">
                                <i class="fas fa-calendar-plus me-2"></i> Nuevo Bloque
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                        </div>
                        <form id="form-bloque" novalidate>
                            <div class="modal-body">
                                <div class="mb-3">
                                    <label for="bloque-materia" class="form-label">Materia <span class="text-danger">*</span></label>
                                    <select class="form-select" id="bloque-materia" required>
                                        <option value="" disabled selected>Seleccionar materia...</option>
                                    </select>
                                    <div class="invalid-feedback">Debe seleccionar una materia</div>
                                </div>

                                <div class="mb-3">
                                    <label for="bloque-dia" class="form-label">Día <span class="text-danger">*</span></label>
                                    <select class="form-select" id="bloque-dia" required>
                                        <option value="" disabled selected>Seleccionar día...</option>
                                        <option value="LUNES">Lunes</option>
                                        <option value="MARTES">Martes</option>
                                        <option value="MIERCOLES">Miércoles</option>
                                        <option value="JUEVES">Jueves</option>
                                        <option value="VIERNES">Viernes</option>
                                        <option value="SABADO">Sábado</option>
                                        <option value="DOMINGO">Domingo</option>
                                    </select>
                                    <div class="invalid-feedback">Debe seleccionar un día</div>
                                </div>

                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <label for="bloque-hora-inicio" class="form-label">Hora Inicio <span class="text-danger">*</span></label>
                                        <input type="time" class="form-control" id="bloque-hora-inicio" required min="07:00" max="22:30" step="1800">
                                        <div class="invalid-feedback">Hora de inicio requerida</div>
                                    </div>
                                    <div class="col-md-6">
                                        <label for="bloque-hora-fin" class="form-label">Hora Fin <span class="text-danger">*</span></label>
                                        <input type="time" class="form-control" id="bloque-hora-fin" required min="07:30" max="23:00" step="1800">
                                        <div class="invalid-feedback">Hora de fin requerida</div>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label for="bloque-aula" class="form-label">Aula (opcional)</label>
                                    <input type="text" class="form-control" id="bloque-aula" placeholder="Ej: Aula 101, Lab 3" maxlength="100">
                                </div>

                                <div class="alert alert-warning d-none" id="bloque-conflicto-alert">
                                    <i class="fas fa-exclamation-triangle me-2"></i>
                                    <span id="bloque-conflicto-msg"></span>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                                <button type="submit" class="btn btn-primary" id="btn-guardar-bloque">
                                    <i class="fas fa-save me-1"></i> Guardar
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        `;
        document.body.insertAdjacentHTML('beforeend', html);
    }

    bindEvents() {
        this.form.addEventListener('submit', (e) => this.handleSubmit(e));
        this.form.addEventListener('input', () => this.validarFormulario());

        // Validación en tiempo real hora fin > hora inicio
        document.getElementById('bloque-hora-inicio').addEventListener('change', () => this.actualizarMinHoraFin());
        document.getElementById('bloque-hora-fin').addEventListener('change', () => this.validarFormulario());

        // Cambio de materia -> actualizar color header
        document.getElementById('bloque-materia').addEventListener('change', () => this.actualizarColorMateria());

        // Limpiar al cerrar modal
        this.modalEl.addEventListener('hidden.bs.modal', () => this.resetForm());
    }

    async cargarMaterias() {
        try {
            const materias = await API.listarMaterias();
            const select = document.getElementById('bloque-materia');
            select.innerHTML = '<option value="" disabled selected>Seleccionar materia...</option>';
            materias.forEach(m => {
                const opt = document.createElement('option');
                opt.value = m.id;
                opt.textContent = `${m.codigo} - ${m.nombre}`;
                opt.dataset.color = m.color;
                select.appendChild(opt);
            });
        } catch (err) {
            console.error('Error cargando materias:', err);
            Utils.mostrarToast('Error', 'No se pudieron cargar las materias', 'error');
        }
    }

    actualizarColorMateria() {
        const select = document.getElementById('bloque-materia');
        const opt = select.options[select.selectedIndex];
        const header = document.getElementById('modal-bloque-header');
        if (opt && opt.dataset.color) {
            header.style.backgroundColor = opt.dataset.color;
            header.style.color = '#fff';
        } else {
            header.style.backgroundColor = '';
            header.style.color = '';
        }
    }

    actualizarMinHoraFin() {
        const inicio = document.getElementById('bloque-hora-inicio').value;
        const finInput = document.getElementById('bloque-hora-fin');
        if (inicio) {
            const [h, m] = inicio.split(':').map(Number);
            let minMinutes = h * 60 + m + 30;
            const minH = Math.floor(minMinutes / 60).toString().padStart(2, '0');
            const minM = (minMinutes % 60).toString().padStart(2, '0');
            finInput.min = `${minH}:${minM}`;
            if (finInput.value && AgendaUtils.timeToMinutes(finInput.value) <= AgendaUtils.timeToMinutes(inicio)) {
                finInput.value = finInput.min;
            }
        }
    }

    validarFormulario() {
        const inicio = document.getElementById('bloque-hora-inicio').value;
        const fin = document.getElementById('bloque-hora-fin').value;
        const valido = inicio && fin && AgendaUtils.validarRangoHorario(inicio, fin);
        document.getElementById('btn-guardar-bloque').disabled = !valido;
        return valido;
    }

    async handleSubmit(e) {
        e.preventDefault();
        if (!this.validarFormulario()) return;

        const btn = document.getElementById('btn-guardar-bloque');
        const originalText = btn.innerHTML;
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></i> Guardando...';

        const datos = {
            materiaId: document.getElementById('bloque-materia').value,
            diaSemana: document.getElementById('bloque-dia').value,
            horaInicio: document.getElementById('bloque-hora-inicio').value,
            horaFin: document.getElementById('bloque-hora-fin').value,
            aula: document.getElementById('bloque-aula').value.trim() || null,
        };

        try {
            let resultado;
            if (this.modo === 'crear') {
                resultado = await AgendaAPI.crearBloque(datos);
            } else {
                resultado = await AgendaAPI.actualizarBloque(this.bloqueEditando.id, datos);
            }

            Utils.mostrarToast('Éxito', `Bloque ${this.modo === 'crear' ? 'creado' : 'actualizado'}`, 'success');
            this.modal.hide();

            if (this.callbackGuardar) {
                this.callbackGuardar(resultado);
            }
        } catch (err) {
            if (err.message?.includes('409') || err.message?.includes('Conflict') || err.message?.includes('Solapamiento')) {
                this.mostrarConflicto(err);
            } else {
                console.error('Error guardando bloque:', err);
                Utils.mostrarToast('Error', err.message || 'No se pudo guardar el bloque', 'error');
            }
        } finally {
            btn.disabled = false;
            btn.innerHTML = originalText;
        }
    }

    mostrarConflicto(err) {
        const alert = document.getElementById('bloque-conflicto-alert');
        const msg = document.getElementById('bloque-conflicto-msg');
        // Parsear respuesta de conflicto si viene en err.response
        msg.textContent = 'Este horario se superpone con otro bloque. Intente otro horario.';
        alert.classList.remove('d-none');
        setTimeout(() => alert.classList.add('d-none'), 5000);
    }

    resetForm() {
        this.form.reset();
        this.form.classList.remove('was-validated');
        document.getElementById('modal-bloque-header').style.backgroundColor = '';
        document.getElementById('modal-bloque-header').style.color = '';
        document.getElementById('bloque-conflicto-alert').classList.add('d-none');
        document.getElementById('btn-guardar-bloque').disabled = true;
        this.modo = 'crear';
        this.bloqueEditando = null;
        this.callbackGuardar = null;
        document.getElementById('modal-bloque-label').innerHTML = '<i class="fas fa-calendar-plus me-2"></i> Nuevo Bloque';
    }

    /**
     * Abre modal para crear nuevo bloque
     * @param {Object} opciones - {dia, hora, callback}
     */
    abrirParaCrear(opciones = {}) {
        this.modo = 'crear';
        this.bloqueEditando = null;
        this.callbackGuardar = opciones.callback || null;

        if (opciones.dia) document.getElementById('bloque-dia').value = opciones.dia;
        if (opciones.hora) document.getElementById('bloque-hora-inicio').value = opciones.hora;
        this.actualizarMinHoraFin();

        document.getElementById('modal-bloque-label').innerHTML = '<i class="fas fa-calendar-plus me-2"></i> Nuevo Bloque';
        this.modal.show();
    }

    /**
     * Abre modal para editar bloque existente
     * @param {Object} bloque - BloqueHorarioResponse
     * @param {Function} callback
     */
    abrirParaEditar(bloque, callback) {
        this.modo = 'editar';
        this.bloqueEditando = bloque;
        this.callbackGuardar = callback || null;

        document.getElementById('bloque-materia').value = bloque.materiaId;
        document.getElementById('bloque-dia').value = bloque.diaSemana;
        document.getElementById('bloque-hora-inicio').value = bloque.horaInicio;
        document.getElementById('bloque-hora-fin').value = bloque.horaFin;
        document.getElementById('bloque-aula').value = bloque.aula || '';

        this.actualizarMinHoraFin();
        this.actualizarColorMateria();

        document.getElementById('modal-bloque-label').innerHTML = '<i class="fas fa-calendar-edit me-2"></i> Editar Bloque';
        this.modal.show();
    }
}

window.AgendaForm = AgendaForm;