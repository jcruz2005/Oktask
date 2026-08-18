/**
 * Estadísticas - Gráficos y análisis
 * OKtask
 */

class Estadisticas {
    constructor() {
        this.chartHoras = null;
        this.chartEvolucion = null;
        this.init();
    }

    init() {
        this.bindEvents();
    }

    bindEvents() {
        // Filtro de período
        document.getElementById('filter-periodo')?.addEventListener('change', (e) => {
            this.cambiarPeriodo(e.target.value);
        });

        // Filtros de rango de fechas
        document.getElementById('fecha-inicio')?.addEventListener('change', () => {
            this.cargarEstadisticas();
        });

        document.getElementById('fecha-fin')?.addEventListener('change', () => {
            this.cargarEstadisticas();
        });
    }

    /**
     * Carga las estadísticas iniciales
     */
    async cargarEstadisticas() {
        await Promise.all([
            this.cargarGraficoHoras(),
            this.cargarTareasProximas(),
            this.cargarUltimasSesiones(),
            this.cargarResumen()
        ]);
    }

    /**
     * Cambia el período de las estadísticas
     * @param {string} periodo - Período seleccionado
     */
    cambiarPeriodo(periodo) {
        const rangoFechas = document.getElementById('rango-fechas');
        
        if (periodo === 'rango') {
            rangoFechas?.classList.remove('d-none');
            document.getElementById('fecha-inicio').value = Utils.inicioMes();
            document.getElementById('fecha-fin').value = Utils.hoy();
        } else {
            rangoFechas?.classList.add('d-none');
            this.cargarEstadisticas();
        }
    }

    /**
     * Obtiene las fechas según el período seleccionado
     * @returns {Object} Fechas de inicio y fin
     */
    obtenerFechasPeriodo() {
        const periodo = document.getElementById('filter-periodo')?.value || 'mes';
        
        switch (periodo) {
            case 'semana':
                return {
                    inicio: Utils.inicioSemana(),
                    fin: Utils.hoy()
                };
            case 'mes':
                return {
                    inicio: Utils.inicioMes(),
                    fin: Utils.hoy()
                };
            case 'rango':
                return {
                    inicio: document.getElementById('fecha-inicio')?.value || Utils.inicioMes(),
                    fin: document.getElementById('fecha-fin')?.value || Utils.hoy()
                };
            default:
                return {
                    inicio: Utils.inicioMes(),
                    fin: Utils.hoy()
                };
        }
    }

    /**
     * Carga el gráfico de horas por materia
     */
    async cargarGraficoHoras() {
        try {
            const { inicio, fin } = this.obtenerFechasPeriodo();
            const estadisticas = await API.obtenerHorasPorPeriodo(inicio, fin);
            
            this.renderizarGraficoHoras(estadisticas);
        } catch (error) {
            console.error('Error al cargar gráfico de horas:', error);
        }
    }

    /**
     * Renderiza el gráfico de barras de minutos estudiados
     * @param {Array} estadisticas - Datos de estadísticas
     */
    renderizarGraficoHoras(estadisticas) {
        const ctx = document.getElementById('chart-horas');
        if (!ctx) return;

        // Destruir gráfico anterior si existe
        if (this.chartHoras) {
            this.chartHoras.destroy();
        }

        // Preparar datos - convertir horas a minutos para mejor visualización
        const labels = estadisticas.map(e => e.codigoMateria || e.nombreMateria);
        const minutos = estadisticas.map(e => Math.round((e.horasEstudiadas || 0) * 60));
        const colores = estadisticas.map((e, i) => {
            const materia = window.materias?.obtenerMateria(e.materiaId);
            return materia?.color || this.obtenerColorIndice(i);
        });

        // Crear gráfico
        this.chartHoras = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Minutos Estudiados',
                    data: minutos,
                    backgroundColor: colores.map(c => Utils.colorConOpacidad(c, 0.7)),
                    borderColor: colores,
                    borderWidth: 2,
                    borderRadius: 8,
                    barThickness: 40
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            label: (context) => {
                                const min = context.parsed.y;
                                return `${min} min`;
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: (value) => `${value}m`
                        },
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }

    /**
     * Obtiene un color por índice
     * @param {number} indice - Índice
     * @returns {string} Color hexadecimal
     */
    obtenerColorIndice(indice) {
        const colores = [
            '#7C3AED', '#06B6D4', '#14B8A6', '#22C55E',
            '#F59E0B', '#EF4444', '#EC4899', '#8B5CF6'
        ];
        return colores[indice % colores.length];
    }

    /**
     * Carga las tareas próximas a vencer
     */
    async cargarTareasProximas() {
        const container = document.getElementById('proximas-tareas');
        if (!container) return;

        try {
            const tareas = window.tareas?.obtenerTareasProximas(7) || [];
            
            if (tareas.length === 0) {
                container.innerHTML = '<p class="text-muted">No hay tareas próximas</p>';
                return;
            }

            container.innerHTML = tareas.slice(0, 5).map(tarea => {
                const materia = window.materias?.obtenerMateria(tarea.materiaId);
                const diasRestantes = Utils.diasRestantes(tarea.fechaLimite);
                const claseUrgencia = Utils.obtenerClaseUrgencia(tarea.fechaLimite);
                const textoDias = Utils.textoDiasRestantes(tarea.fechaLimite);

                return `
                    <div class="task-item">
                        <div class="task-color" style="background-color: ${materia?.color || '#7C3AED'}"></div>
                        <div class="task-info">
                            <div class="task-title">${Utils.escapeHtml(tarea.titulo)}</div>
                            <div class="task-meta">${Utils.escapeHtml(materia?.codigo || '')}</div>
                        </div>
                        <span class="task-due ${claseUrgencia}">${textoDias}</span>
                    </div>
                `;
            }).join('');
        } catch (error) {
            container.innerHTML = '<p class="text-muted">Error al cargar tareas</p>';
        }
    }

    /**
     * Carga las últimas sesiones de Pomodoro
     */
    async cargarUltimasSesiones() {
        const container = document.getElementById('ultimas-sesiones');
        if (!container) return;

        try {
            // Obtener todas las sesiones recientes
            const materias = window.materias?.materias || [];
            let todasLasSesiones = [];

            for (const materia of materias.slice(0, 3)) {
                const sesiones = await API.obtenerSesionesMateria(materia.id);
                todasLasSesiones = [...todasLasSesiones, ...sesiones];
            }

            // Ordenar por fecha y tomar las últimas 5
            todasLasSesiones.sort((a, b) => 
                new Date(b.fechaInicio) - new Date(a.fechaInicio)
            );

            const ultimasSesiones = todasLasSesiones.slice(0, 5);

            if (ultimasSesiones.length === 0) {
                container.innerHTML = '<p class="text-muted">No hay sesiones recientes</p>';
                return;
            }

            container.innerHTML = ultimasSesiones.map(sesion => {
                const materia = window.materias?.obtenerMateria(sesion.materiaId);
                const fecha = Utils.formatearFechaHora(sesion.fechaInicio);

                return `
                    <div class="session-item">
                        <div class="task-color" style="background-color: ${materia?.color || '#7C3AED'}"></div>
                        <div class="session-info">
                            <div class="task-title">${Utils.escapeHtml(materia?.nombre || 'Sin materia')}</div>
                            <div class="task-meta">${fecha}</div>
                        </div>
                        <span class="session-time">${sesion.duracionMinutos} min</span>
                    </div>
                `;
            }).join('');
        } catch (error) {
            container.innerHTML = '<p class="text-muted">Error al cargar sesiones</p>';
        }
    }

    /**
     * Carga el resumen de estadísticas
     */
    async cargarResumen() {
        try {
            const { inicio, fin } = this.obtenerFechasPeriodo();
            const resumen = await API.obtenerResumen(inicio, fin);

            document.getElementById('stat-horas-hoy').textContent = 
                Utils.formatearHoras(resumen.totalHoras || 0);
            document.getElementById('stat-pomodoros-hoy').textContent = 
                resumen.totalPomodoros || 0;
        } catch (error) {
            console.error('Error al cargar resumen:', error);
        }
    }

    /**
     * Refresca todas las estadísticas
     */
    async refrescar() {
        await this.cargarEstadisticas();
    }
}

// Crear instancia global
window.estadisticas = new Estadisticas();
