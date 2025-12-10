document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "/login";
        return;
    }

    cargarFacturas(token);

    document.getElementById("btn-logout").addEventListener("click", () => {
        localStorage.removeItem("token");
        window.location.href = "/login";
    });
});

async function cargarFacturas(token) {
    try {
        const response = await fetch("/api/facturas", {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (response.status === 401) {
            localStorage.removeItem("token");
            window.location.href = "/login";
            return;
        }

        const facturas = await response.json();
        renderizarTabla(facturas);

    } catch (error) {
        console.error("Error cargando facturas", error);
        document.getElementById("facturas-tbody").innerHTML = `<tr><td colspan="6" class="error">Error al cargar datos</td></tr>`;
    }
}

function renderizarTabla(facturas) {
    const tbody = document.getElementById("facturas-tbody");
    tbody.innerHTML = "";

    if (facturas.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" class="text-center">No hay facturas registradas</td></tr>`;
        return;
    }

    facturas.forEach(f => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${f.id}</td>
            <td>${f.fechaEmision}</td>
            <td>${f.cliente ? f.cliente.nombreRazonSocial : 'Consumidor Final'}</td>
            <td>$${f.importeTotal ? f.importeTotal.toFixed(2) : '0.00'}</td>
            <td><span class="badge ${f.estado}">${f.estado}</span></td>
            <td>
                <button onclick="descargarPdf(${f.id})" class="btn-link">PDF</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

async function descargarPdf(id) {
    const token = localStorage.getItem("token");
    try {
        const response = await fetch(`/api/facturas/${id}/pdf`, {
            headers: { "Authorization": "Bearer " + token }
        });

        if (response.ok) {
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = `factura-${id}.pdf`;
            document.body.appendChild(a);
            a.click();
            a.remove();
        } else {
            alert("Error al descargar PDF");
        }
    } catch (e) {
        console.error(e);
        alert("Error de conexión");
    }
}

async function descargarReporteGeneral() {
    const token = localStorage.getItem("token");
    try {
        const response = await fetch("/api/reportes/facturas", {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (response.ok) {
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = "reporte_general_facturas.pdf";
            document.body.appendChild(a);
            a.click();
            a.remove();
        } else {
            alert("Error al descargar reporte general");
        }
    } catch (e) {
        console.error(e);
        alert("Error de conexión");
    }
}

// function copiarRutaXml... (eliminado por petición)
