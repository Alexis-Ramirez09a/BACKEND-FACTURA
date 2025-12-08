/**
 * Valida una cédula ecuatoriana.
 * @param {string} cedula - La cédula a validar.
 * @returns {boolean} - true si es válida, false si no.
 */
function validarCedula(cedula) {
    if (!cedula || cedula.length !== 10) return false;

    // Verificar que sean solo números
    if (!/^\d+$/.test(cedula)) return false;

    const provincia = parseInt(cedula.substring(0, 2));
    if (provincia < 1 || provincia > 24) return false;

    const tercerDigito = parseInt(cedula.substring(2, 3));
    if (tercerDigito >= 6) return false;

    const coeficientes = [2, 1, 2, 1, 2, 1, 2, 1, 2];
    let suma = 0;

    for (let i = 0; i < 9; i++) {
        let valor = parseInt(cedula.charAt(i)) * coeficientes[i];
        if (valor >= 10) valor -= 9;
        suma += valor;
    }

    const digitoVerificador = parseInt(cedula.charAt(9));
    let residuo = suma % 10;
    let resultado = residuo === 0 ? 0 : 10 - residuo;

    return resultado === digitoVerificador;
}

/**
 * Valida un RUC ecuatoriano (Persona Natural, Jurídica o Pública).
 * @param {string} ruc - El RUC a validar.
 * @returns {boolean} - true si es válido, false si no.
 */
function validarRuc(ruc) {
    if (!ruc || ruc.length !== 13) return false;
    if (!/^\d+$/.test(ruc)) return false;
    if (!ruc.endsWith('001')) return false; // Generalmente terminan en 001

    const provincia = parseInt(ruc.substring(0, 2));
    if (provincia < 1 || provincia > 24) return false;

    const tercerDigito = parseInt(ruc.charAt(2));

    if (tercerDigito < 6) {
        // RUC Persona Natural (mismo algoritmo que cédula + 001)
        return validarCedula(ruc.substring(0, 10));
    } else if (tercerDigito === 9) {
        // RUC Jurídica Privada
        return validarRucJuridica(ruc);
    } else if (tercerDigito === 6) {
        // RUC Pública
        return validarRucPublica(ruc);
    }

    return false;
}

function validarRucJuridica(ruc) {
    const coeficientes = [4, 3, 2, 7, 6, 5, 4, 3, 2];
    let suma = 0;
    for (let i = 0; i < 9; i++) {
        suma += parseInt(ruc.charAt(i)) * coeficientes[i];
    }
    const residuo = suma % 11;
    const resultado = residuo === 0 ? 0 : 11 - residuo;
    return resultado === parseInt(ruc.charAt(9));
}

function validarRucPublica(ruc) {
    const coeficientes = [3, 2, 7, 6, 5, 4, 3, 2];
    let suma = 0;
    for (let i = 0; i < 8; i++) {
        suma += parseInt(ruc.charAt(i)) * coeficientes[i];
    }
    const residuo = suma % 11;
    const resultado = residuo === 0 ? 0 : 11 - residuo;
    return resultado === parseInt(ruc.charAt(8));
}

/**
 * Valida un correo electrónico.
 * @param {string} email 
 * @returns {boolean}
 */
function validarEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}
