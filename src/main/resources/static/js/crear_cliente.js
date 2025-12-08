document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('cliente-form');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const cliente = {
            tipoIdentificacion: document.getElementById('tipoIdentificacion').value,
            identificacion: document.getElementById('identificacion').value,
            nombreRazonSocial: document.getElementById('nombreRazonSocial').value,
            direccion: document.getElementById('direccion').value,
            telefono: document.getElementById('telefono').value,
            correo: document.getElementById('correo').value
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

            const response = await fetch('/api/clientes', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(cliente)
            });

            if (response.ok) {
                alert('Cliente creado exitosamente');
                window.location.href = '/clientes';
            } else {
                const errorText = await response.text();
                // Mejorar manejo de errores del backend (ej: validaciones @Valid)
                try {
                    const errorJson = JSON.parse(errorText);
                    if (errorJson.errors) {
                        alert('Error de validación: ' + errorJson.errors.map(e => e.defaultMessage).join(', '));
                    } else {
                        alert('Error: ' + (errorJson.message || errorText));
                    }
                } catch (e) {
                    alert('Error al crear cliente: ' + errorText);
                }
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Error de conexión al crear cliente');
        }
    });
});
