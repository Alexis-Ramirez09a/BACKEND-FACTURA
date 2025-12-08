document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("login-form");
    const errorBox = document.getElementById("login-error");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        errorBox.style.display = "none";
        errorBox.textContent = "";

        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value.trim();

        if (!username || !password) {
            errorBox.textContent = "Ingresa usuario y contraseña.";
            errorBox.style.display = "block";
            return;
        }

        try {
            const response = await fetch("/api/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ username, password })
            });

            console.log("status login:", response.status);

            if (!response.ok) {
                throw new Error("Usuario o contraseña incorrectos");
            }

            const data = await response.json(); // { token: "...", username: "...", rol: "..." }
            localStorage.setItem("token", data.token);
            localStorage.setItem("usuario", data.username);
            localStorage.setItem("rol", data.rol);

            window.location.href = "/facturas";
        } catch (err) {
            console.error(err);
            errorBox.textContent = err.message || "Error al iniciar sesión";
            errorBox.style.display = "block";
        }
    });
});
