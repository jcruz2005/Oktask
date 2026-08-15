/**
 * App - Lógica principal de la aplicación
 * Gestor de Tareas Académicas
 */

class App {
    constructor() {
        this.tabActual = 'dashboard';
        this.init();
    }

    async init() {
        this.bindEvents();
        this.cargarTema();
        this.navegarA('dashboard');
        
        // Cargar datos iniciales
        await this.cargarDatosIniciales();
        
        // Iniciar verificación de notificaciones
        this.iniciarNotificaciones();
        
        // Cargar atajos de teclado
        this.cargarAtajosTeclado();
    }

    bindEvents() {
        // Navegación por sidebar
        document.querySelectorAll('.sidebar-item').forEach(item => {
            item.addEventListener('click', () => {
                const tabId = item.dataset.tab;
                this.navegarA(tabId);
            });
        });

        // Toggle de sidebar
        document.getElementById('btn-toggle-sidebar')?.addEventListener('click', () => {
            this.toggleSidebar();
        });

        document.getElementById('btn-collapse-sidebar')?.addEventListener('click', () => {
            this.toggleSidebar();
        });

        // Toggle de tema
        document.getElementById('btn-theme-toggle')?.addEventListener('click', () => {
            this.toggleTema();
        });

        // Botón exportar
        document.getElementById('btn-exportar')?.addEventListener('click', () => {
            this.exportarDatos();
        });
    }

    /**
     * Navega a un tab específico
     * @param {string} tabId - ID del tab
     */
    navegarA(tabId) {
        // Actualizar items del sidebar activos
        document.querySelectorAll('.sidebar-item').forEach(item => {
            item.classList.toggle('active', item.dataset.tab === tabId);
        });

        // Actualizar contenido visible
        document.querySelectorAll('.tab-content').forEach(content => {
            content.classList.toggle('active', content.id === `tab-${tabId}`);
        });

        this.tabActual = tabId;

        // Cargar datos específicos del tab
        this.cargarDatosTab(tabId);

        // En móvil, cerrar el sidebar después de navegar
        if (window.innerWidth <= 768) {
            document.getElementById('sidebar')?.classList.remove('mobile-open');
        }
    }

    /**
     * Carga datos según el tab seleccionado
     * @param {string} tabId - ID del tab
     */
    async cargarDatosTab(tabId) {
        switch (tabId) {
            case 'dashboard':
                await window.estadisticas?.refrescar();
                break;
            case 'materias':
                await window.materias?.cargarMaterias();
                break;
            case 'tareas':
                await window.tareas?.cargarTareas();
                break;
            case 'pomodoro':
                await window.pomodoro?.cargarTareasPendientes();
                break;
        }
    }

    /**
     * Carga los datos iniciales de la aplicación
     */
    async cargarDatosIniciales() {
        try {
            // Cargar materias primero (necesarias para tareas)
            await window.materias?.cargarMaterias();
            
            // Luego cargar tareas
            await window.tareas?.cargarTareas();
            
            // Finalmente estadísticas
            await window.estadisticas?.cargarEstadisticas();
        } catch (error) {
            console.error('Error al cargar datos iniciales:', error);
            Utils.mostrarToast('Error', 'No se pudieron cargar todos los datos', 'error');
        }
    }

    /**
     * Carga el tema guardado
     */
    cargarTema() {
        const temaGuardado = localStorage.getItem('tema') || 'light';
        this.aplicarTema(temaGuardado);
    }

    /**
     * Aplica el tema indicado
     * @param {string} tema - 'light' o 'dark'
     */
    aplicarTema(tema) {
        document.documentElement.setAttribute('data-theme', tema);
        
        const btnIcon = document.querySelector('#btn-theme-toggle i');
        if (btnIcon) {
            btnIcon.className = tema === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
        }
    }

    /**
     * Alterna el sidebar entre expandido y colapsado
     */
    toggleSidebar() {
        const sidebar = document.getElementById('sidebar');
        if (!sidebar) return;

        if (window.innerWidth <= 768) {
            // En móvil, toggle entre open y closed
            sidebar.classList.toggle('mobile-open');
        } else {
            // En desktop, toggle entre collapsed y expanded
            sidebar.classList.toggle('collapsed');
        }
    }

    /**
     * Alterna entre tema claro y oscuro
     */
    toggleTema() {
        const temaActual = document.documentElement.getAttribute('data-theme');
        const nuevoTema = temaActual === 'dark' ? 'light' : 'dark';
        
        this.aplicarTema(nuevoTema);
        localStorage.setItem('tema', nuevoTema);
    }

    /**
     * Exporta los datos a CSV
     */
    async exportarDatos() {
        try {
            const materias = window.materias?.materias || [];
            const tareas = window.tareas?.tareas || [];

            // Crear contenido CSV de materias
            let csv = 'MATERIAS\n';
            csv += 'Codigo,Nombre,Color,Prioridad,Fecha Creacion\n';
            materias.forEach(m => {
                csv += `${m.codigo},${m.nombre},${m.color},${m.prioridad},${m.fechaCreacion}\n`;
            });

            csv += '\nTAREAS\n';
            csv += 'Titulo,Descripcion,Materia,Fecha Limite,Prioridad,Estado,Fecha Creacion\n';
            tareas.forEach(t => {
                const materia = window.materias?.obtenerMateria(t.materiaId);
                csv += `"${t.titulo}","${t.descripcion || ''}","${materia?.nombre || ''}",${t.fechaLimite},${t.prioridad},${t.estado},${t.fechaCreacion}\n`;
            });

            // Descargar archivo
            const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = `gestor-tareas-${Utils.hoy()}.csv`;
            link.click();

            Utils.mostrarToast('Éxito', 'Datos exportados correctamente', 'success');
        } catch (error) {
            Utils.mostrarToast('Error', 'No se pudieron exportar los datos', 'error');
        }
    }

    /**
     * Inicia las notificaciones
     */
    async iniciarNotificaciones() {
        const permiso = await Notificaciones.solicitarPermiso();
        
        if (permiso) {
            // Verificar tareas cada hora
            Notificaciones.iniciarVerificacionPeriodica(
                () => window.tareas?.cargarTareas(),
                60
            );
        }
    }

    /**
     * Carga los atajos de teclado
     */
    cargarAtajosTeclado() {
        document.addEventListener('keydown', (e) => {
            // Ctrl/Cmd + N: Nueva tarea
            if ((e.ctrlKey || e.metaKey) && e.key === 'n') {
                e.preventDefault();
                if (this.tabActual === 'tareas') {
                    window.tareas?.abrirModalCrear();
                } else if (this.tabActual === 'materias') {
                    window.materias?.abrirModalCrear();
                }
            }

            // Escape: Cerrar modales
            if (e.key === 'Escape') {
                const modales = document.querySelectorAll('.modal.show');
                modales.forEach(modal => {
                    bootstrap.Modal.getInstance(modal)?.hide();
                });
            }

            // 1-4: Cambiar de tab (sin Ctrl)
            if (!e.ctrlKey && !e.metaKey && !e.altKey) {
                if (e.key === '1') this.navegarA('dashboard');
                if (e.key === '2') this.navegarA('materias');
                if (e.key === '3') this.navegarA('tareas');
                if (e.key === '4') this.navegarA('pomodoro');
            }
        });
    }
}

// Iniciar aplicación cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    window.app = new App();
});
