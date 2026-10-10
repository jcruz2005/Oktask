/**
 * AgendaAPI - Comunicación con Backend para Agenda Semanal
 * OKtask
 */

class AgendaAPI {
    static async listarBloques(filtros = {}) {
        const params = new URLSearchParams();
        if (filtros.materiaId) params.append('materiaId', filtros.materiaId);
        if (filtros.diaSemana) params.append('diaSemana', filtros.diaSemana);
        const query = params.toString() ? `?${params.toString()}` : '';
        return API.request(`/agenda/bloques${query}`);
    }

    static async obtenerSemana() {
        return API.request('/agenda/bloques/semana');
    }

    static async obtenerBloque(id) {
        return API.request(`/agenda/bloques/${id}`);
    }

    static async crearBloque(datos) {
        return API.request('/agenda/bloques', {
            method: 'POST',
            body: JSON.stringify(datos),
        });
    }

    static async actualizarBloque(id, datos) {
        return API.request(`/agenda/bloques/${id}`, {
            method: 'PUT',
            body: JSON.stringify(datos),
        });
    }

    static async moverBloque(id, datos) {
        return API.request(`/agenda/bloques/${id}/mover`, {
            method: 'PATCH',
            body: JSON.stringify(datos),
        });
    }

    static async eliminarBloque(id) {
        return API.request(`/agenda/bloques/${id}`, {
            method: 'DELETE',
        });
    }

    static async validarDisponibilidad(materiaId, diaSemana, horaInicio, horaFin) {
        const params = new URLSearchParams({
            materiaId,
            diaSemana,
            horaInicio,
            horaFin,
        });
        return API.request(`/agenda/bloques/validar?${params.toString()}`);
    }
}

window.AgendaAPI = AgendaAPI;