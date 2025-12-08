// static/js/facturas.js

document.addEventListener("DOMContentLoaded", () => {
    const tbody = document.getElementById("facturas-tbody");

    // 1. Verificar que haya token; si no, al login
    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "/login";
        return;
    }

    // 2. Llamar a la API de facturas
    cargarFacturas(token, tbody);
});

async function cargarFacturas(token, tbody) {
    try {
        // 👉 AJUSTA ESTA URL SI TU BACKEND USA OTRA (por ejemplo /api/factura/listar)
        const response = await fetch("/api/facturas", {
            headers: {
                "Authorization": `Bearer ${token}`,
                "Accept": "application/json"
            }
        });

        if (response.status === 401 || response.status === 403) {
            // Token inválido / expirado → al login
            localStorage.removeItem("token");
            window.location.href = "/login";
            return;
        }

        if (!response.ok) {
            throw new Error("Error al obtener las facturas");
        }

        const facturas = await response.json();
        // facturas debería ser un array. Ejemplo:
        // [{numero: "...", clienteNombre: "...", fechaEmision: "...", total: 150.0, estado: "AUTORIZADA"}, ...]

        renderFacturas(facturas, tbody);
    } catch (err) {
        console.error(err);
        tbody.innerHTML = `
      <tr>
        <td colspan="6">Error al cargar las facturas</td>
      </tr>
    `;
    }
}

function renderFacturas(facturas, tbody) {
    if (!Array.isArray(facturas) || facturas.length === 0) {
        tbody.innerHTML = `
      <tr>
        <td colspan="6">No hay facturas para mostrar</td>
      </tr>
    `;
        return;
    }

    const filas = facturas.map(f => {
        // 👉 AJUSTA ESTOS NOMBRES A TU DTO REAL
        const numero = f.numero || f.numeroFactura || "";
        const cliente = f.clienteNombre || f.cliente || "";
        const fecha = f.fechaEmision || f.fecha || "";
        const total = f.total || f.totalFactura || 0;
        const estado = f.estado || "DESCONOCIDO";

        return `
      <tr>
        <td>${numero}</td>
        <td>${cliente}</td>
        <td>${fecha}</td>
        <td>$ ${total.toFixed ? total.toFixed(2) : total}</td>
        <td><span class="badge ${estadoClass(estado)}">${estado}</span></td>
        <td>
          <a href="#" class="btn-small">Ver</a>
        </td>
      </tr>
    `;
    }).join("");

    tbody.innerHTML = filas;
}

function estadoClass(estado) {
    const e = (estado || "").toUpperCase();
    if (e.includes("AUT")) return "success";
    if (e.includes("PEN") || e.includes("EMIT")) return "pending";
    if (e.includes("ANU")) return "danger";
    return "";
}
