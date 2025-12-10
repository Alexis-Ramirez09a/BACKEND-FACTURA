document.addEventListener('DOMContentLoaded', () => {
    cargarProductos();

    // Logout logic
    const btnLogout = document.getElementById('btn-logout');
    if (btnLogout) {
        btnLogout.addEventListener('click', () => {
            localStorage.removeItem('token');
            localStorage.removeItem('usuario');
            window.location.href = '/login';
        });
    }

    const userDisplay = document.getElementById('user-display');
    const usuario = localStorage.getItem('usuario');
    if (userDisplay && usuario) {
        userDisplay.textContent = usuario;
    }
});

async function cargarProductos() {
    const tbody = document.getElementById('productos-tbody');
    const token = localStorage.getItem('token');

    if (!token) {
        window.location.href = '/login';
        return;
    }

    try {
        const response = await fetch('/api/productos', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const productos = await response.json();
            renderizarTabla(productos);
        } else {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center">Error al cargar productos</td></tr>';
            if (response.status === 401 || response.status === 403) {
                localStorage.removeItem('token');
                window.location.href = '/login';
            }
        }
    } catch (error) {
        console.error('Error:', error);
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">Error de conexión</td></tr>';
    }
}

function renderizarTabla(productos) {
    const tbody = document.getElementById('productos-tbody');
    tbody.innerHTML = '';

    if (productos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">No hay productos registrados</td></tr>';
        return;
    }

    productos.forEach(producto => {
        const rol = localStorage.getItem('rol');
        let botones = '';

        if (rol === 'ADMINISTRADOR') {
            botones += `<button class="btn btn-sm btn-primary me-2" onclick="editarProducto(${producto.id})">Editar</button>`;
            botones += `<button class="btn btn-sm btn-danger" onclick="eliminarProducto(${producto.id})">Eliminar</button>`;
        }

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${producto.id}</td>
            <td>${producto.codigoPrincipal}</td>
            <td>${producto.descripcion}</td>
            <td>${producto.precioUnitario.toFixed(4)}</td>
            <td>${producto.cantidad}</td>
            <td>
                ${botones}
            </td>
        `;
        tbody.appendChild(tr);
    });
}

async function eliminarProducto(id) {
    if (!confirm('¿Está seguro de eliminar este producto?')) {
        return;
    }

    const token = localStorage.getItem('token');
    try {
        const response = await fetch(`/api/productos/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            alert('Producto eliminado');
            cargarProductos();
        } else {
            alert('Error al eliminar producto');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error de conexión');
    }

    function editarProducto(id) {
        window.location.href = `/crear_producto.html?id=${id}`;
    }
}

async function descargarReporte() {
    const token = localStorage.getItem('token');
    try {
        const response = await fetch('/api/reportes/productos', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'productos.pdf';
            document.body.appendChild(a);
            a.click();
            a.remove();
        } else {
            alert('Error al descargar el reporte');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error de conexión al descargar reporte');
    }
}
