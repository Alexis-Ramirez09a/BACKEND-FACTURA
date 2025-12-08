document.addEventListener('DOMContentLoaded', () => {
    cargarClientes();

    // Logout logic (copied from facturas.js for consistency)
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

async function cargarClientes() {
    const tbody = document.getElementById('clientes-tbody');
    const token = localStorage.getItem('token');

    if (!token) {
        window.location.href = '/login';
        return;
    }

    try {
        const response = await fetch('/api/clientes', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const clientes = await response.json();
            renderizarTabla(clientes);
        } else {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center">Error al cargar clientes</td></tr>';
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

function renderizarTabla(clientes) {
    const tbody = document.getElementById('clientes-tbody');
    tbody.innerHTML = '';

    if (clientes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">No hay clientes registrados</td></tr>';
        return;
    }

    clientes.forEach(cliente => {
        const rol = localStorage.getItem('rol');
        let botones = `<a href="/clientes/editar/${cliente.id}" class="btn btn-sm btn-info" style="margin-right: 5px;">Editar</a>`;

        if (rol === 'ADMIN') {
            botones += `<button class="btn btn-sm btn-danger" onclick="eliminarCliente(${cliente.id})">Eliminar</button>`;
        }

        tr.innerHTML = `
            <td>${cliente.id}</td>
            <td>${cliente.identificacion}</td>
            <td>${cliente.nombreRazonSocial}</td>
            <td>${cliente.correo || '-'}</td>
            <td>${cliente.telefono || '-'}</td>
            <td>
                ${botones}
            </td>
        `;
        tbody.appendChild(tr);
    });
}

async function eliminarCliente(id) {
    if (!confirm('¿Está seguro de eliminar este cliente?')) {
        return;
    }

    const token = localStorage.getItem('token');
    try {
        const response = await fetch(`/api/clientes/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            alert('Cliente eliminado');
            cargarClientes();
        } else {
            alert('Error al eliminar cliente');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error de conexión');
    }
}
