document.addEventListener('DOMContentLoaded', async () => {
    const form = document.getElementById('producto-form');
    const urlParams = new URLSearchParams(window.location.search);
    const productoId = urlParams.get('id');

    // MODO EDICIÓN: Cargar datos si hay ID
    if (productoId) {
        document.querySelector('h2').textContent = 'Editar Producto';
        document.querySelector('button[type="submit"]').textContent = 'Actualizar';
        await cargarDatosProducto(productoId);
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const producto = {
            codigoPrincipal: document.getElementById('codigoPrincipal').value,
            descripcion: document.getElementById('descripcion').value,
            precioUnitario: parseFloat(document.getElementById('precioUnitario').value),
            cantidad: parseInt(document.getElementById('cantidad').value) || 0,
            activo: true
        };

        // ... (validaciones) ...
        if (producto.precioUnitario < 0) {
            alert('El precio unitario no puede ser negativo.');
            return;
        }

        try {
            const token = localStorage.getItem('token');
            if (!token) {
                window.location.href = '/login';
                return;
            }

            // Definir URL y Método según si es Crear o Editar
            let url = '/api/productos';
            let method = 'POST';

            if (productoId) {
                url = `/api/productos/${productoId}`;
                method = 'PUT';
            }

            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(producto)
            });

            if (response.ok) {
                alert(productoId ? 'Producto actualizado' : 'Producto creado exitosamente');
                window.location.href = '/productos.html';
            } else {
                const errorText = await response.text();
                // ... manejo errores ...
                alert('Error: ' + errorText);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Error de conexión');
        }
    });

    async function cargarDatosProducto(id) {
        try {
            const token = localStorage.getItem('token');
            const res = await fetch(`/api/productos/${id}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (res.ok) {
                const p = await res.json();
                document.getElementById('codigoPrincipal').value = p.codigoPrincipal;
                document.getElementById('descripcion').value = p.descripcion;
                document.getElementById('precioUnitario').value = p.precioUnitario;
                document.getElementById('cantidad').value = p.cantidad;
            } else {
                alert("No se pudo cargar el producto");
                window.location.href = '/productos.html';
            }
        } catch (e) {
            console.error(e);
        }
    }
});
