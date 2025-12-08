document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('producto-form');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const producto = {
            codigoPrincipal: document.getElementById('codigoPrincipal').value,
            codigoAuxiliar: document.getElementById('codigoAuxiliar').value,
            descripcion: document.getElementById('descripcion').value,
            precioUnitario: parseFloat(document.getElementById('precioUnitario').value),
            unidadMedida: document.getElementById('unidadMedida').value,
            activo: true
        };

        // Validaciones Frontend
        if (producto.precioUnitario < 0) {
            alert('El precio unitario no puede ser negativo.');
            return;
        }

        try {
            const token = localStorage.getItem('token');
            if (!token) {
                alert('No hay sesión activa. Por favor inicie sesión.');
                window.location.href = '/login';
                return;
            }

            const response = await fetch('/api/productos', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(producto)
            });

            if (response.ok) {
                alert('Producto creado exitosamente');
                window.location.href = '/productos';
            } else {
                const errorText = await response.text();
                if (errorText.includes("duplicate key") || errorText.includes("violates unique constraint")) {
                    alert('Error: El código principal ya existe. Por favor use otro código.');
                } else {
                    try {
                        const errorJson = JSON.parse(errorText);
                        if (errorJson.errors) {
                            alert('Error de validación: ' + errorJson.errors.map(e => e.defaultMessage).join(', '));
                        } else {
                            alert('Error: ' + (errorJson.message || errorText));
                        }
                    } catch (e) {
                        alert('Error al crear producto: ' + errorText);
                    }
                }
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Error de conexión al crear producto');
        }
    });
});
