
//  UTILIDADES
function obtenerToken() {
    return localStorage.getItem("token");
}

let PRODUCTOS_CACHE = [];

async function cargarProductos() {
    const token = obtenerToken();
    try {
        const res = await fetch("/api/productos", {
            headers: { "Authorization": "Bearer " + token }
        });
        if (res.ok) {
            PRODUCTOS_CACHE = await res.json();
            llenarSelectProductos(document.querySelector(".prod"));
        }
    } catch (e) {
        console.error("Error cargando productos", e);
    }
}

async function cargarEmpresas() {
    const token = obtenerToken();
    try {
        const res = await fetch("/api/empresas", {
            headers: { "Authorization": "Bearer " + token }
        });
        if (res.ok) {
            const empresas = await res.json();
            const select = document.getElementById("empresa");
            select.innerHTML = '<option value="">Seleccione empresa...</option>';
            empresas.forEach(e => {
                const opt = document.createElement("option");
                opt.value = e.id;
                opt.textContent = e.razonSocial || e.nombreComercial;
                select.appendChild(opt);
            });

            // Si hay empresas, cargar establecimientos de la primera (o esperar selección)
            if (empresas.length > 0) {
                // select.value = empresas[0].id; // Opcional: seleccionar la primera por defecto
                // cargarEstablecimientos(empresas[0].id);
            } else {
                alert("No hay empresas registradas. Debes crear una empresa primero.");
            }
        }
    } catch (e) {
        console.error("Error cargando empresas", e);
    }
}

async function cargarEstablecimientos(empresaId) {
    if (!empresaId) return;
    const token = obtenerToken();
    try {
        const res = await fetch(`/api/establecimientos/empresa/${empresaId}`, {
            headers: { "Authorization": "Bearer " + token }
        });
        if (res.ok) {
            const establecimientos = await res.json();
            const select = document.getElementById("establecimiento");
            select.innerHTML = '<option value="">Seleccione...</option>';
            establecimientos.forEach(e => {
                const opt = document.createElement("option");
                opt.value = e.id; // Usamos el ID del establecimiento, no el código
                opt.textContent = e.codigo + " - " + (e.nombreComercial || "");
                select.appendChild(opt);
            });
        }
    } catch (e) { console.error(e); }
}

async function cargarPuntosEmision(establecimientoId) {
    if (!establecimientoId) return;
    const token = obtenerToken();
    try {
        const res = await fetch(`/api/puntos-emision/establecimiento/${establecimientoId}`, {
            headers: { "Authorization": "Bearer " + token }
        });
        if (res.ok) {
            const puntos = await res.json();
            const select = document.getElementById("punto-emision");
            select.innerHTML = '<option value="">Seleccione...</option>';
            puntos.forEach(p => {
                const opt = document.createElement("option");
                opt.value = p.id; // Usamos el ID del punto, no el código
                opt.textContent = p.codigo;
                select.appendChild(opt);
            });
        }
    } catch (e) { console.error(e); }
}

function llenarSelectProductos(select) {
    if (!select) return;
    select.innerHTML = '<option value="">Seleccione producto...</option>';
    PRODUCTOS_CACHE.forEach(p => {
        const opt = document.createElement("option");
        opt.value = p.id;
        opt.textContent = `${p.nombre} - $${p.precioUnitario}`;
        opt.dataset.precio = p.precioUnitario;
        select.appendChild(opt);
    });
}

function formatear(num) {
    return Number(num).toFixed(2);
}

//  RECALCULAR UNA LÍNEA DE DETALLE
function recalcularLinea(row) {
    const cant = parseFloat(row.querySelector(".cant").value || 0);
    const precio = parseFloat(row.querySelector(".precio").value || 0);
    const desc = parseFloat(row.querySelector(".desc").value || 0);
    const iva = parseFloat(row.querySelector(".iva").value || 0);

    const subtotal = cant * precio;
    const valorDesc = subtotal * (desc / 100);
    const base = subtotal - valorDesc;
    const valorIva = base * (iva / 100);
    const total = base + valorIva;

    row.querySelector(".total").value = formatear(total);

    return {
        subtotal,
        descuento: valorDesc,
        iva: valorIva,
        total
    };
}

//  RECALCULAR RESUMEN (subtotal, descuento, IVA, total)

function recalcularResumen() {
    let subtotal = 0;
    let descuento = 0;
    let iva = 0;
    let total = 0;

    document.querySelectorAll("#lineas-tbody tr.linea").forEach(row => {
        const calc = recalcularLinea(row);
        subtotal += calc.subtotal;
        descuento += calc.descuento;
        iva += calc.iva;
        total += calc.total;
    });

    document.getElementById("subtotal-text").textContent = "$" + formatear(subtotal);
    document.getElementById("descuento-text").textContent = "$" + formatear(descuento);
    document.getElementById("iva-text").textContent = "$" + formatear(iva);
    document.getElementById("total-text").textContent = "$" + formatear(total);
}

//  AGREGAR NUEVA LÍNEA

function agregarLinea() {
    const tbody = document.getElementById("lineas-tbody");

    const nueva = document.createElement("tr");
    nueva.className = "linea";

    nueva.innerHTML = `
        <td>
            <select class="prod">
                <option value="">Seleccione producto...</option>
                <!-- Se llenará dinámicamente -->
            </select>
        </td>
        <td><input class="cant" type="number" value="1" min="1"></td>
        <td><input class="precio" type="number" value="0" step="0.01"></td>
        <td><input class="desc" type="number" value="0" min="0" max="100" step="0.01"></td>
        <td>
            <select class="iva">
                <option value="12">12%</option>
                <option value="0">0%</option>
            </select>
        </td>
        <td><input class="total" type="text" value="0.00" disabled></td>
        <td>
            <button type="button" class="btn-small danger btn-eliminar-linea">X</button>
        </td>
    `;

    tbody.appendChild(nueva);
    llenarSelectProductos(nueva.querySelector(".prod")); // Llenar el nuevo select
    asignarEventosLinea(nueva);
    recalcularResumen();
}

//  ELIMINAR UNA LÍNEA

function eliminarLinea(btn) {
    btn.closest("tr").remove();
    recalcularResumen();
}

//  Asignar eventos a inputs de una línea nueva
function asignarEventosLinea(row) {
    row.querySelectorAll("input, select").forEach(input => {
        input.addEventListener("input", () => recalcularResumen());
    });

    // Al cambiar producto, actualizar precio
    const selectProd = row.querySelector(".prod");
    if (selectProd) {
        selectProd.addEventListener("change", (e) => {
            const opt = e.target.selectedOptions[0];
            if (opt && opt.dataset.precio) {
                row.querySelector(".precio").value = opt.dataset.precio;
                recalcularResumen();
            }
        });
    }

    row.querySelector(".btn-eliminar-linea")
        .addEventListener("click", () => eliminarLinea(row.querySelector(".btn-eliminar-linea")));
}

//  AGREGAR NUEVO PAGO
function agregarPago() {
    const cont = document.getElementById("pagos-contenedor");

    const div = document.createElement("div");
    div.className = "grid-2 pago";

    div.innerHTML = `
        <div class="form-group">
            <label>Forma de pago</label>
            <select class="forma-pago">
                <option value="EFECTIVO">Efectivo</option>
                <option value="TARJETA">Tarjeta</option>
                <option value="TRANSFERENCIA">Transferencia</option>
            </select>
        </div>

        <div class="form-group">
            <label>Monto</label>
            <input class="monto-pago" type="number" value="0" step="0.01">
        </div>

        <div class="form-group">
            <label>Plazo</label>
            <input class="plazo-pago" type="number" value="0" min="0">
        </div>

        <div class="form-group">
            <label>Tiempo</label>
            <select class="unidad-tiempo-pago">
                <option value="dias">Días</option>
                <option value="meses">Meses</option>
                <option value="anios">Años</option>
            </select>
        </div>
    `;

    cont.appendChild(div);
}

//  ARMAR JSON DE LA FACTURA

function obtenerFacturaJSON() {
    // Detalles (líneas)
    const detalles = [];
    document.querySelectorAll("#lineas-tbody tr.linea").forEach(row => {
        detalles.push({
            // 👇 Ajusta estos nombres a FacturaDetalleCrearDto
            productoId: parseInt(row.querySelector(".prod").value) || null,
            cantidad: parseFloat(row.querySelector(".cant").value || "0"),
            precioUnitario: parseFloat(row.querySelector(".precio").value || "0"),
            descuento: parseFloat(row.querySelector(".desc").value || "0"), // El DTO espera 'descuento' (monto) o porcentaje? Revisar DTO. Asumimos monto por nombre en DTO.
            descripcion: row.querySelector(".prod").selectedOptions[0]?.textContent || ""
        });
    });

    // Pagos
    const pagos = [];
    document.querySelectorAll(".pago").forEach(div => {
        pagos.push({
            // Ajusta nombres a FacturaPagoCrearDto
            codigoFormaPagoSri: obtenerCodigoSri(div.querySelector(".forma-pago").value),
            total: parseFloat(div.querySelector(".monto-pago").value || "0"),
            plazo: parseInt(div.querySelector(".plazo-pago").value || "0"),
            unidadTiempo: div.querySelector(".unidad-tiempo-pago").value
        });
    });

    // Cabecera
    const factura = {
        empresaId: parseInt(document.getElementById("empresa").value) || null,
        establecimientoId: parseInt(document.getElementById("establecimiento").value) || null, // Ahora enviamos ID
        puntoEmisionId: parseInt(document.getElementById("punto-emision").value) || null, // Ahora enviamos ID
        // 🔴 IMPORTANTE: usa un ID de cliente que exista en tu BD
        clienteId: parseInt(document.getElementById("cliente-id").value) || null,
        fechaEmision: document.getElementById("fecha-emision").value,
        observacion: document.getElementById("observacion").value,
        guiaRemision: document.getElementById("guia-remision").value,
        detalles: detalles,
        pagos: pagos
    };

    console.log("Factura JSON a enviar:", JSON.stringify(factura, null, 2));

    return factura;
}

function obtenerCodigoSri(formaPago) {
    const mapa = {
        "EFECTIVO": "01",
        "TARJETA": "19",
        "TRANSFERENCIA": "20"
    };
    return mapa[formaPago] || "01";
}

//  ENVIAR FACTURA AL BACKEND
async function guardarFactura() {
    const token = obtenerToken();
    if (!token) {
        alert("Sesión expirada. Inicia sesión de nuevo.");
        return window.location.href = "/login";
    }

    const factura = obtenerFacturaJSON();

    try {
        const resp = await fetch("/api/facturas/emitir", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify(factura)
        });

        if (!resp.ok) {
            const texto = await resp.text();
            console.error("Error backend:", texto);

            // 👇 NUEVO: mostramos el mensaje real del backend
            alert("Error del backend:\n\n" + texto);
            return; // salimos sin redirigir
        }

        alert("Factura guardada correctamente");
        window.location.href = "/facturas";

    } catch (e) {
        console.error(e);
        alert("Error al guardar la factura");
    }
}

//  INICIALIZACIÓN

document.addEventListener("DOMContentLoaded", () => {

    // Cargar catálogos iniciales
    cargarProductos();
    cargarEmpresas();

    // Establecer fecha actual por defecto
    const today = new Date().toISOString().split('T')[0];
    document.getElementById("fecha-emision").value = today;

    // Eventos de cambio en selectores de cabecera
    document.getElementById("empresa").addEventListener("change", (e) => {
        cargarEstablecimientos(e.target.value);
    });

    document.getElementById("establecimiento").addEventListener("change", (e) => {
        cargarPuntosEmision(e.target.value);
    });

    // Asignar eventos a la primera línea
    document.querySelectorAll("#lineas-tbody tr.linea").forEach(row => {
        asignarEventosLinea(row);
    });

    // Botón agregar línea
    document.getElementById("btn-agregar-linea")
        .addEventListener("click", agregarLinea);

    // Botón agregar pago
    document.getElementById("btn-agregar-pago")
        .addEventListener("click", agregarPago);

    // Botón guardar
    document.getElementById("btn-guardar")
        .addEventListener("click", guardarFactura);

    // Evento input para búsqueda parcial
    const inputBusqueda = document.getElementById("identificacion-cliente");
    inputBusqueda.addEventListener("input", (e) => {
        buscarClienteParcial(e.target.value);
    });

    // Ocultar lista al hacer clic fuera
    document.addEventListener("click", (e) => {
        if (!e.target.closest("#identificacion-cliente") && !e.target.closest("#lista-resultados-cliente")) {
            document.getElementById("lista-resultados-cliente").style.display = "none";
        }
    });

    // Modal Cliente
    const modal = document.getElementById("modal-crear-cliente");
    document.getElementById("btn-abrir-modal-cliente").addEventListener("click", () => {
        modal.showModal();
    });
    document.getElementById("btn-cerrar-modal").addEventListener("click", () => {
        modal.close();
    });

    document.getElementById("form-crear-cliente-modal").addEventListener("submit", async (e) => {
        e.preventDefault();
        await crearClienteModal();
    });

    // Calcular una vez al inicio
    recalcularResumen();
});

// BUSQUEDA PARCIAL
let searchTimeout;
async function buscarClienteParcial(texto) {
    const lista = document.getElementById("lista-resultados-cliente");
    lista.innerHTML = "";

    if (!texto || texto.length < 3) {
        lista.style.display = "none";
        return;
    }

    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(async () => {
        const token = obtenerToken();
        try {
            const res = await fetch(`/api/clientes/buscar-identificacion?q=${texto}`, {
                headers: { "Authorization": "Bearer " + token }
            });
            if (res.ok) {
                const clientes = await res.json();
                mostrarResultados(clientes);
            }
        } catch (e) {
            console.error("Error buscando", e);
        }
    }, 300); // Debounce 300ms
}

function mostrarResultados(clientes) {
    const lista = document.getElementById("lista-resultados-cliente");
    lista.innerHTML = "";

    if (clientes.length === 0) {
        lista.style.display = "none";
        return;
    }

    clientes.forEach(c => {
        const li = document.createElement("li");
        li.style.padding = "0.5rem 1rem";
        li.style.cursor = "pointer";
        li.style.borderBottom = "1px solid #eee";
        li.textContent = `${c.identificacion} - ${c.razonSocial}`;
        li.onmouseover = () => li.style.backgroundColor = "#f8fafc";
        li.onmouseout = () => li.style.backgroundColor = "white";

        li.addEventListener("click", () => {
            seleccionarCliente(c);
        });
        lista.appendChild(li);
    });
    lista.style.display = "block";
}

function seleccionarCliente(c) {
    document.getElementById("identificacion-cliente").value = c.identificacion;
    document.getElementById("nombre-cliente").value = c.razonSocial;
    document.getElementById("cliente-id").value = c.id;
    document.getElementById("lista-resultados-cliente").style.display = "none";
}

// CREAR CLIENTE MODAL
async function crearClienteModal() {
    const cliente = {
        tipoIdentificacion: document.getElementById('modal-tipoIdentificacion').value,
        identificacion: document.getElementById('modal-identificacion').value,
        nombreRazonSocial: document.getElementById('modal-nombreRazonSocial').value,
        direccion: document.getElementById('modal-direccion').value,
        telefono: document.getElementById('modal-telefono').value,
        correo: document.getElementById('modal-correo').value
    };

    const token = obtenerToken();
    try {
        const res = await fetch('/api/clientes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(cliente)
        });

        if (res.ok) {
            const nuevoCliente = await res.json();
            alert('Cliente creado exitosamente');
            document.getElementById("modal-crear-cliente").close();
            document.getElementById("form-crear-cliente-modal").reset();
            // Auto-seleccionar
            seleccionarCliente(nuevoCliente);
        } else {
            const errorText = await res.text();
            alert('Error al crear cliente: ' + errorText);
        }
    } catch (e) {
        console.error(e);
        alert('Error de conexión');
    }
}
