document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('cliente-form');
    // Obtener ID de la URL: /clientes/editar/123 -> 123
    const pathParts = window.location.pathname.split('/');
    const clienteId = pathParts[pathParts.length - 1];

    cargarCliente(clienteId);

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const cliente = {
            id: clienteId,
            tipoIdentificacion: document.getElementById('tipoIdentificacion').value,
            identificacion: document.getElementById('identificacion').value,
            nombreRazonSocial: document.getElementById('nombreRazonSocial').value,
            direccion: document.getElementById('direccion').value,
            telefono: document.getElementById('telefono').value,
            correo: document.getElementById('correo').value,
            activo: document.getElementById('activo').value === 'true'
        };

        // Validaciones Frontend
        if (!validarCedula(cliente.identificacion) && !validarRuc(cliente.identificacion)) {
            alert('La identificación (Cédula/RUC) no es válida.');
            return;
        }

        if (!validarEmail(cliente.correo) && cliente.correo) {
            alert('El correo electrónico no es válido.');
            return;
        }

        try {
            const token = localStorage.getItem('token');
            if (!token) {
                alert('No hay sesión activa. Por favor inicie sesión.');
                window.location.href = '/login';
                return;
            }

            const response = await fetch(`/api/clientes/${clienteId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(cliente)
            });

            if (response.ok) {
                alert('Cliente actualizado exitosamente');
                window.location.href = '/clientes';
            } else {
                const errorText = await response.text();
                try {
                    const errorJson = JSON.parse(errorText);
                    alert('Error: ' + (errorJson.message || errorText));
                } catch (e) {
                    alert('Error al actualizar cliente: ' + errorText);
                }
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Error de conexión al actualizar cliente');
        }
    });
});

async function cargarCliente(id) {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = '/login';
            return;
        }

        const response = await fetch(`/api/clientes/${id}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const cliente = await response.json();
            document.getElementById('tipoIdentificacion').value = cliente.tipoIdentificacion;
            document.getElementById('identificacion').value = cliente.identificacion;
            document.getElementById('nombreRazonSocial').value = cliente.nombreRazonSocial;
            document.getElementById('direccion').value = cliente.direccion || '';
            document.getElementById('telefono').value = cliente.telefono || '';
            document.getElementById('correo').value = cliente.correo || '';
            document.getElementById('activo').value = cliente.activo.toString();
        } else {
            alert('No se pudo cargar la información del cliente');
            window.location.href = '/clientes';
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error de conexión');
    }
}
