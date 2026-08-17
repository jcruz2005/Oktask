/**
 * Pomodoro - Temporizador Pomodoro (versión Tab)
 * OKtask
 */

class Pomodoro {
    constructor() {
        this.configuracion = null;
        this.sesionActual = null;
        this.tareaActual = null;
        this.materiaActual = null;
        
        // Estado del temporizador
        this.estado = 'inactivo'; // inactivo, trabajo, descanso, pausado
        this.tiempoRestante = 25 * 60; // en segundos
        this.tiempoTotal = 25 * 60;
        this.intervalo = null;
        
        // Contadores
        this.pomodorosHoy = 0;
        this.tiempoTotalHoy = 0; // en minutos
        
        this.init();
    }

    init() {
        this.cargarConfiguracion();
        this.bindEvents();
        this.cargarEstadisticasHoy();
        this.cargarConfiguracionUI(); // Cargar valores en el formulario
    }

    bindEvents() {
        // Botones de control del timer
        document.getElementById('btn-pomodoro-iniciar')?.addEventListener('click', () => {
            this.iniciar();
        });

        document.getElementById('btn-pomodoro-pausar')?.addEventListener('click', () => {
            this.pausar();
        });

        document.getElementById('btn-pomodoro-reanudar')?.addEventListener('click', () => {
            this.reanudar();
        });

        document.getElementById('btn-pomodoro-finalizar')?.addEventListener('click', () => {
            this.finalizar();
        });

        // Botón cambiar tarea
        document.getElementById('btn-cambiar-tarea')?.addEventListener('click', () => {
            this.cambiarTarea();
        });

        // Formulario de configuración
        const configForm = document.getElementById('pomodoro-config-form');
        if (configForm) {
            configForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.guardarConfiguracion();
            });
        }
    }

    /**
     * Carga los valores de configuración en el formulario UI
     */
    async cargarConfiguracionUI() {
        try {
            const config = await API.obtenerConfiguracionPomodoro();
            document.getElementById('config-trabajo').value = config.duracionTrabajo?.minutos ?? 25;
            document.getElementById('config-descanso').value = config.duracionDescanso?.minutos ?? 5;
            document.getElementById('config-descanso-largo').value = config.duracionDescansoLargo?.minutos ?? 15;
            document.getElementById('config-pomodoros-largo').value = config.pomodorosParaDescansoLargo ?? 4;
        } catch (error) {
            console.error('Error al cargar configuración para UI:', error);
            // Valores por defecto
            document.getElementById('config-trabajo').value = 25;
            document.getElementById('config-descanso').value = 5;
            document.getElementById('config-descanso-largo').value = 15;
            document.getElementById('config-pomodoros-largo').value = 4;
        }
    }

    /**
     * Guarda la configuración desde el formulario
     */
    async guardarConfiguracion() {
        const trabajo = parseInt(document.getElementById('config-trabajo').value);
        const descanso = parseInt(document.getElementById('config-descanso').value);
        const descansoLargo = parseInt(document.getElementById('config-descanso-largo').value);
        const pomodorosLargo = parseInt(document.getElementById('config-pomodoros-largo').value);

        // Validación básica
        if (isNaN(trabajo) || trabajo < 1 || trabajo > 120) {
            Utils.mostrarToast('Error', 'El tiempo de trabajo debe estar entre 1 y 120 minutos', 'error');
            return;
        }
        if (isNaN(descanso) || descanso < 1 || descanso > 60) {
            Utils.mostrarToast('Error', 'El tiempo de descanso debe estar entre 1 y 60 minutos', 'error');
            return;
        }
        if (isNaN(descansoLargo) || descansoLargo < 1 || descansoLargo > 60) {
            Utils.mostrarToast('Error', 'El tiempo de descanso largo debe estar entre 1 y 60 minutos', 'error');
            return;
        }
        if (isNaN(pomodorosLargo) || pomodorosLargo < 1 || pomodorosLargo > 12) {
            Utils.mostrarToast('Error', 'Los pomodoros para descanso largo deben estar entre 1 y 12', 'error');
            return;
        }

        try {
            await API.actualizarConfiguracionPomodoro({
                duracionTrabajo: trabajo,
                duracionDescanso: descanso,
                duracionDescansoLargo: descansoLargo,
                pomodorosParaDescansoLargo: pomodorosLargo
            });

            // Actualizar la configuración local
            this.configuracion = {
                duracionTrabajo: trabajo,
                duracionDescanso: descanso,
                duracionDescansoLargo: descansoLargo,
                pomodorosParaDescansoLargo: pomodorosLargo
            };

            // Actualizar el tiempo total si estamos en estado inactivo
            if (this.estado === 'inactivo') {
                this.tiempoTotal = this.configuracion.duracionTrabajo * 60;
                this.tiempoRestante = this.tiempoTotal;
                this.actualizarDisplay();
            }

            Utils.mostrarToast('Éxito', 'Configuración guardada correctamente', 'success');
        } catch (error) {
            console.error('Error al guardar configuración:', error);
            Utils.mostrarToast('Error', 'No se pudo guardar la configuración', 'error');
        }
    }

    /**
     * Carga la configuración del Pomodoro
     */
    async cargarConfiguracion() {
        try {
            const config = await API.obtenerConfiguracionPomodoro();
            // DuracionMinutos viene como {"minutos":25}, extraer el número
            const durTrabajo = config.duracionTrabajo?.minutos ?? config.duracionTrabajo ?? 25;
            const durDescanso = config.duracionDescanso?.minutos ?? config.duracionDescanso ?? 5;
            const durDescansoLargo = config.duracionDescansoLargo?.minutos ?? config.duracionDescansoLargo ?? 15;
            
            this.configuracion = {
                duracionTrabajo: durTrabajo,
                duracionDescanso: durDescanso,
                duracionDescansoLargo: durDescansoLargo,
                pomodorosParaDescansoLargo: config.pomodorosParaDescansoLargo ?? 4
            };
            this.tiempoTotal = this.configuracion.duracionTrabajo * 60;
            this.tiempoRestante = this.tiempoTotal;
            this.actualizarDisplay();
        } catch (error) {
            console.warn('Usando configuración por defecto del Pomodoro');
            this.configuracion = {
                duracionTrabajo: 25,
                duracionDescanso: 5,
                duracionDescansoLargo: 15,
                pomodorosParaDescansoLargo: 4
            };
        }
    }

    /**
     * Carga las estadísticas de hoy
     */
    async cargarEstadisticasHoy() {
        try {
            const hoy = Utils.hoy();
            const sesiones = await API.obtenerHorasPorPeriodo(hoy, hoy);
            
            this.pomodorosHoy = 0;
            this.tiempoTotalHoy = 0;
            
            sesiones.forEach(s => {
                this.pomodorosHoy += s.pomodorosCompletados || 0;
                this.tiempoTotalHoy += s.horasEstudiadas * 60 || 0;
            });
            
            this.actualizarEstadisticas();
        } catch (error) {
            console.error('Error al cargar estadísticas:', error);
        }
    }

    /**
     * Carga las tareas para mostrar en el tab
     * Muestra: PENDIENTE, EN_PROGRESO, y COMPLETADA (para reabrir)
     */
    async cargarTareasPendientes() {
        const container = document.getElementById('pomodoro-task-list');
        if (!container) return;

        // Asegurarse de que las tareas estén cargadas
        if (!window.tareas?.tareas || window.tareas.tareas.length === 0) {
            await window.tareas?.cargarTareas();
        }

        const tareas = window.tareas?.tareas || [];
        
        // Mostrar todas las tareas excepto las que tienen 0 pomodoros y están completadas
        // (las completadas con pomodoros se muestran para poder reabrirlas)
        const tareasParaMostrar = tareas.filter(t => {
            if (t.estado !== 'COMPLETADA') return true;
            // Mostrar completadas que tienen tiempo pomodoro
            return (t.minutosPomodoro || 0) > 0;
        });

        if (tareasParaMostrar.length === 0) {
            container.innerHTML = `
                <div class="text-center text-muted py-4">
                    <i class="fas fa-clipboard-list fa-3x mb-3"></i>
                    <p>No hay tareas disponibles</p>
                    <p class="small">Creá una tarea para empezar a estudiar</p>
                </div>
            `;
            return;
        }

        // Agrupar por estado
        const pendientes = tareasParaMostrar.filter(t => t.estado === 'PENDIENTE');
        const enProgreso = tareasParaMostrar.filter(t => t.estado === 'EN_PROGRESO');
        const completadas = tareasParaMostrar.filter(t => t.estado === 'COMPLETADA');

        let html = '';

        // Tareas en progreso (prioridad)
        if (enProgreso.length > 0) {
            html += `<div class="pomodoro-section">
                <div class="pomodoro-section-header en-progreso">
                    <i class="fas fa-spinner"></i> En Progreso
                </div>
                ${enProgreso.map(t => this.renderizarTareaPomodoro(t)).join('')}
            </div>`;
        }

        // Tareas pendientes
        if (pendientes.length > 0) {
            html += `<div class="pomodoro-section">
                <div class="pomodoro-section-header pendiente">
                    <i class="fas fa-clock"></i> Pendientes
                </div>
                ${pendientes.map(t => this.renderizarTareaPomodoro(t)).join('')}
            </div>`;
        }

        // Tareas completadas (para reabrir)
        if (completadas.length > 0) {
            html += `<div class="pomodoro-section">
                <div class="pomodoro-section-header completada">
                    <i class="fas fa-check"></i> Completadas
                </div>
                ${completadas.map(t => this.renderizarTareaPomodoro(t, true)).join('')}
            </div>`;
        }

        container.innerHTML = html;

        // Bind click events
        container.querySelectorAll('.btn-seleccionar-tarea').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const item = e.target.closest('.pomodoro-task-item');
                const tareaId = item.dataset.tareaId;
                const materiaId = item.dataset.materiaId;
                this.seleccionarTarea(tareaId, materiaId);
            });
        });

        // Bind reabrir events
        container.querySelectorAll('.btn-reabrir-tarea').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const item = e.target.closest('.pomodoro-task-item');
                const tareaId = item.dataset.tareaId;
                await this.reabrirTarea(tareaId);
            });
        });
    }

    /**
     * Renderiza una tarea para la lista del pomodoro
     * @param {Object} tarea - Tarea a renderizar
     * @param {boolean} esCompletada - Si es una tarea completada
     * @returns {string} HTML generado
     */
    renderizarTareaPomodoro(tarea, esCompletada = false) {
        const materia = window.materias?.obtenerMateria(tarea.materiaId);
        const fechaLimite = new Date(tarea.fechaLimite);
        const hoy = new Date();
        const diasRestantes = Math.ceil((fechaLimite - hoy) / (1000 * 60 * 60 * 24));
        
        let urgencyClass = 'later';
        if (!esCompletada) {
            if (diasRestantes <= 1) urgencyClass = 'urgent';
            else if (diasRestantes <= 3) urgencyClass = 'soon';
        }

        const minutos = tarea.minutosPomodoro || 0;
        const horas = Math.floor(minutos / 60);
        const mins = minutos % 60;
        const tiempoFormateado = horas > 0 ? `${horas}h ${mins}m` : `${mins}m`;

        return `
            <div class="pomodoro-task-item ${esCompletada ? 'completada' : ''}" 
                 data-tarea-id="${tarea.id}" 
                 data-materia-id="${tarea.materiaId}">
                <div class="task-color" style="background-color: ${materia?.color || '#6B7280'}"></div>
                <div class="task-info">
                    <div class="task-title">${tarea.titulo}</div>
                    <div class="task-meta">
                        <span class="task-materia">${materia?.codigo || 'Sin materia'}</span>
                        ${!esCompletada ? `<span class="task-due ${urgencyClass}">${Utils.formatearFechaCorta(tarea.fechaLimite)}</span>` : ''}
                    </div>
                </div>
                <div class="task-pomodoro-time">
                    <i class="fas fa-clock"></i> ${tiempoFormateado}
                </div>
                ${esCompletada ? 
                    `<button class="btn btn-outline-warning btn-sm btn-reabrir-tarea" title="Volver a trabajar">
                        <i class="fas fa-undo"></i> Reabrir
                    </button>` :
                    `<button class="btn btn-primary btn-sm btn-seleccionar-tarea" title="Iniciar pomodoro">
                        <i class="fas fa-play"></i> Estudiar
                    </button>`
                }
            </div>
        `;
    }

    /**
     * Reabre una tarea completada
     * @param {string} tareaId - ID de la tarea
     */
    async reabrirTarea(tareaId) {
        try {
            await API.editarTarea(tareaId, { estado: 'EN_PROGRESO' });
            await window.tareas?.cargarTareas();
            await this.cargarTareasPendientes();
            Utils.mostrarToast('Tarea reabierta', 'La tarea volver a estar en progreso', 'success');
        } catch (error) {
            Utils.mostrarToast('Error', 'No se pudo reabrir la tarea', 'error');
        }
    }

    /**
     * Selecciona una tarea para el Pomodoro
     * @param {string} tareaId - ID de la tarea
     * @param {string} materiaId - ID de la materia
     */
    async seleccionarTarea(tareaId, materiaId) {
        this.tareaActual = window.tareas?.tareas.find(t => t.id === tareaId);
        this.materiaActual = window.materias?.obtenerMateria(materiaId);

        // Si la tarea está pendiente, cambiarla a en progreso
        if (this.tareaActual?.estado === 'PENDIENTE') {
            try {
                await API.editarTarea(tareaId, { estado: 'EN_PROGRESO' });
                this.tareaActual.estado = 'EN_PROGRESO';
                await window.tareas?.cargarTareas();
            } catch (error) {
                console.error('Error al cambiar estado:', error);
            }
        }

        // Mostrar el timer y ocultar la selección
        document.getElementById('pomodoro-seleccion-container')?.classList.add('d-none');
        document.getElementById('pomodoro-timer-container')?.classList.remove('d-none');

        this.actualizarInfoTarea();
        this.actualizarDisplay();
    }

    /**
     * Cambia la tarea seleccionada
     */
    cambiarTarea() {
        if (this.estado !== 'inactivo') {
            if (!confirm('¿Estás seguro de que querés cambiar de tarea? Se perderá el progreso actual.')) {
                return;
            }
            this.detener();
        }

        // Mostrar selección y ocultar timer
        document.getElementById('pomodoro-seleccion-container')?.classList.remove('d-none');
        document.getElementById('pomodoro-timer-container')?.classList.add('d-none');

        this.tareaActual = null;
        this.materiaActual = null;
        this.cargarTareasPendientes();
    }

    /**
     * Actualiza la información de la tarea en el timer
     */
    actualizarInfoTarea() {
        const materiaEl = document.getElementById('pomodoro-materia');
        const tareaEl = document.getElementById('pomodoro-tarea');
        const pomodoroTimeEl = document.getElementById('pomodoro-task-time');
        
        if (materiaEl) {
            materiaEl.textContent = this.materiaActual?.nombre || 'Sin materia';
        }
        if (tareaEl) {
            tareaEl.textContent = this.tareaActual?.titulo || 'Sin tarea seleccionada';
        }
        if (pomodoroTimeEl && this.tareaActual) {
            const minutos = this.tareaActual.minutosPomodoro || 0;
            const horas = Math.floor(minutos / 60);
            const mins = minutos % 60;
            pomodoroTimeEl.textContent = horas > 0 ? `${horas}h ${mins}m acumuladas` : `${mins}m acumuladas`;
        }
    }

    /**
     * Inicia el temporizador
     */
    async iniciar() {
        if (this.estado !== 'inactivo') return;
        
        // Crear sesión en el backend
        if (this.tareaActual && this.materiaActual) {
            try {
                this.sesionActual = await API.iniciarSesionPomodoro({
                    tareaId: this.tareaActual.id,
                    materiaId: this.materiaActual.id,
                    duracionMinutos: this.configuracion.duracionTrabajo
                });
            } catch (error) {
                Utils.mostrarToast('Error', 'No se pudo iniciar la sesión', 'error');
                return;
            }
        }
        
        this.estado = 'trabajo';
        this.tiempoRestante = this.tiempoTotal;
        
        this.mostrarBotones('trabajo');
        this.iniciarTemporizador();
    }

    /**
     * Pausa el temporizador
     */
    pausar() {
        if (this.estado !== 'trabajo' && this.estado !== 'descanso') return;
        
        this.estado = 'pausado';
        this.detenerTemporizador();
        this.mostrarBotones('pausado');
    }

    /**
     * Reanuda el temporizador
     */
    reanudar() {
        if (this.estado !== 'pausado') return;
        
        this.estado = this.tiempoRestante > 0 ? 'trabajo' : 'descanso';
        this.iniciarTemporizador();
        this.mostrarBotones(this.estado);
    }

    /**
     * Finaliza la sesión actual
     */
    async finalizar() {
        if (this.sesionActual) {
            try {
                await API.finalizarSesionPomodoro(this.sesionActual.id);
                
                // Calcular tiempo real transcurrido (no el configurado)
                const minutosTranscurridos = this.calcularMinutosTranscurridos();
                this.pomodorosHoy++;
                this.tiempoTotalHoy += minutosTranscurridos;
                this.actualizarEstadisticas();
                
                // Recargar tareas para mostrar el tiempo actualizado
                await window.tareas?.cargarTareas();
                
                Utils.mostrarToast('¡Completado!', 
                    `${minutosTranscurridos} minutos guardados`, 'success');
            } catch (error) {
                console.error('Error al finalizar sesión:', error);
            }
        }
        
        // Limpiar estado sin intentar cancelar la sesión ya completada
        this.detenerTemporizador();
        this.estado = 'inactivo';
        this.sesionActual = null;
        
        // Resetear tiempo
        this.tiempoTotal = (this.configuracion?.duracionTrabajo || 25) * 60;
        this.tiempoRestante = this.tiempoTotal;
        
        this.mostrarBotones('inactivo');
        this.actualizarDisplay();
        this.quitarAnimaciones();
    }

    /**
     * Detiene el temporizador y guarda el tiempo si hay sesión activa
     */
    async detener() {
        this.detenerTemporizador();
        
        // Si hay sesión activa, cancelarla guardando el tiempo transcurrido
        if (this.sesionActual) {
            const minutosTranscurridos = this.calcularMinutosTranscurridos();
            try {
                await API.cancelarSesionPomodoro(this.sesionActual.id, minutosTranscurridos);
                
                if (minutosTranscurridos > 0) {
                    this.tiempoTotalHoy += minutosTranscurridos;
                    this.actualizarEstadisticas();
                    await window.tareas?.cargarTareas();
                }
                
                Utils.mostrarToast('Sesión guardada', 
                    `${minutosTranscurridos} minutos guardados`, 'info');
            } catch (error) {
                console.error('Error al cancelar sesión:', error);
            }
        }
        
        this.estado = 'inactivo';
        this.sesionActual = null;
        
        // Resetear tiempo
        this.tiempoTotal = (this.configuracion?.duracionTrabajo || 25) * 60;
        this.tiempoRestante = this.tiempoTotal;
        
        this.mostrarBotones('inactivo');
        this.actualizarDisplay();
        this.quitarAnimaciones();
    }

    /**
     * Inicia el intervalo del temporizador
     */
    iniciarTemporizador() {
        this.detenerTemporizador();
        
        this.intervalo = setInterval(() => {
            this.tiempoRestante--;
            this.actualizarDisplay();
            this.actualizarProgreso();
            
            if (this.tiempoRestante <= 0) {
                this.temporizadorTerminado();
            }
        }, 1000);
    }

    /**
     * Detiene el intervalo del temporizador
     */
    detenerTemporizador() {
        if (this.intervalo) {
            clearInterval(this.intervalo);
            this.intervalo = null;
        }
    }

    /**
     * Se ejecuta cuando el temporizador termina
     */
    async temporizadorTerminado() {
        this.detenerTemporizador();
        this.reproducirSonido();
        
        if (this.estado === 'trabajo') {
            // Completar la sesión de trabajo cuando el timer termina
            if (this.sesionActual) {
                try {
                    await API.finalizarSesionPomodoro(this.sesionActual.id);
                    const minutosTranscurridos = this.calcularMinutosTranscurridos();
                    this.pomodorosHoy++;
                    this.tiempoTotalHoy += minutosTranscurridos;
                    this.actualizarEstadisticas();
                    await window.tareas?.cargarTareas();
                } catch (error) {
                    console.error('Error al finalizar sesión automáticamente:', error);
                }
                this.sesionActual = null;
            }
            
            this.mostrarAnimacionCompletado();
            Notificaciones.notificarPomodoroCompletado(this.configuracion.duracionTrabajo);
            
            setTimeout(() => {
                this.iniciarDescanso();
            }, 2000);
        } else if (this.estado === 'descanso') {
            Notificaciones.notificarDescansoTerminado();
            Utils.mostrarToast('Descanso terminado', 'Es hora de volver a estudiar', 'info');
            
            setTimeout(() => {
                this.detener();
            }, 2000);
        }
    }

    /**
     * Inicia el período de descanso
     */
    iniciarDescanso() {
        this.estado = 'descanso';
        
        const esDescansoLargo = this.pomodorosHoy % this.configuracion.pomodorosParaDescansoLargo === 0;
        const duracionDescanso = esDescansoLargo 
            ? this.configuracion.duracionDescansoLargo 
            : this.configuracion.duracionDescanso;
        
        this.tiempoTotal = duracionDescanso * 60;
        this.tiempoRestante = this.tiempoTotal;
        
        document.querySelector('.card-pomodoro-timer')?.classList.add('descanso');
        
        this.mostrarBotones('descanso');
        this.actualizarDisplay();
        this.iniciarTemporizador();
    }

    /**
     * Muestra los botones según el estado
     * @param {string} estado - Estado del Pomodoro
     */
    mostrarBotones(estado) {
        const btnIniciar = document.getElementById('btn-pomodoro-iniciar');
        const btnPausar = document.getElementById('btn-pomodoro-pausar');
        const btnReanudar = document.getElementById('btn-pomodoro-reanudar');
        const btnFinalizar = document.getElementById('btn-pomodoro-finalizar');
        
        btnIniciar?.classList.add('d-none');
        btnPausar?.classList.add('d-none');
        btnReanudar?.classList.add('d-none');
        btnFinalizar?.classList.remove('d-none');
        
        switch (estado) {
            case 'inactivo':
                btnIniciar?.classList.remove('d-none');
                btnFinalizar?.classList.add('d-none');
                break;
            case 'trabajo':
            case 'descanso':
                btnPausar?.classList.remove('d-none');
                break;
            case 'pausado':
                btnReanudar?.classList.remove('d-none');
                break;
        }
    }

    /**
     * Actualiza el display del temporizador
     */
    actualizarDisplay() {
        const minutos = Math.floor(this.tiempoRestante / 60);
        const segundos = this.tiempoRestante % 60;
        
        const minutesEl = document.getElementById('timer-minutes');
        const secondsEl = document.getElementById('timer-seconds');
        const statusEl = document.getElementById('timer-status');
        
        if (minutesEl) minutesEl.textContent = minutos.toString().padStart(2, '0');
        if (secondsEl) secondsEl.textContent = segundos.toString().padStart(2, '0');
        if (statusEl) {
            statusEl.textContent = this.estado === 'descanso' ? 'DESCANSO' : 'TRABAJO';
            statusEl.className = 'timer-status ' + (this.estado === 'descanso' ? 'descanso' : '');
        }
    }

    /**
     * Actualiza la barra de progreso
     */
    actualizarProgreso() {
        const progress = document.getElementById('timer-progress');
        if (!progress) return;
        
        const circumference = 2 * Math.PI * 90;
        const percent = this.tiempoRestante / this.tiempoTotal;
        const offset = circumference * percent;
        
        progress.style.strokeDashoffset = offset;
    }

    /**
     * Actualiza las estadísticas en el tab
     */
    actualizarEstadisticas() {
        const countEl = document.getElementById('pomodoro-count-hoy');
        const timeEl = document.getElementById('pomodoro-tiempo-hoy');
        
        if (countEl) countEl.textContent = this.pomodorosHoy;
        if (timeEl) timeEl.textContent = `${Math.round(this.tiempoTotalHoy)} min`;
    }

    /**
     * Calcula los minutos transcurridos desde el inicio de la sesión
     * @returns {number} Minutos transcurridos
     */
    calcularMinutosTranscurridos() {
        if (!this.sesionActual) return 0;
        
        const segundosTranscurridos = this.tiempoTotal - this.tiempoRestante;
        return Math.max(Math.floor(segundosTranscurridos / 60), 1);
    }

    /**
     * Muestra animación de completado
     */
    mostrarAnimacionCompletado() {
        const display = document.querySelector('.timer-display');
        display?.classList.add('flash');
        
        setTimeout(() => {
            display?.classList.remove('flash');
        }, 2000);
    }

    /**
     * Quita todas las animaciones
     */
    quitarAnimaciones() {
        document.querySelector('.timer-display')?.classList.remove('flash', 'pulsing');
        document.querySelector('.card-pomodoro-timer')?.classList.remove('descanso');
    }

    /**
     * Reproduce sonido de notificación
     */
    reproducirSonido() {
        try {
            const audioContext = new (window.AudioContext || window.webkitAudioContext)();
            const oscillator = audioContext.createOscillator();
            const gainNode = audioContext.createGain();
            
            oscillator.connect(gainNode);
            gainNode.connect(audioContext.destination);
            
            oscillator.frequency.value = 800;
            oscillator.type = 'sine';
            
            gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
            gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.5);
            
            oscillator.start(audioContext.currentTime);
            oscillator.stop(audioContext.currentTime + 0.5);
        } catch (error) {
            console.warn('No se pudo reproducir sonido:', error);
        }
    }

    /**
     * Inicia el Pomodoro para una tarea específica (desde la lista de tareas)
     * @param {string} tareaId - ID de la tarea
     * @param {string} materiaId - ID de la materia
     */
    iniciarParaTarea(tareaId, materiaId) {
        window.app?.navegarA('pomodoro');
        
        setTimeout(() => {
            this.seleccionarTarea(tareaId, materiaId);
        }, 300);
    }
}

// Crear instancia global
window.pomodoro = new Pomodoro();
