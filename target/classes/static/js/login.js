// login seite, schickt an /api/auth/login, leitet je nach rolle weiter

function login() {
    const email = document.getElementById("email").value;
    const passwort = document.getElementById("passwort").value;
    const fehlerBox = document.getElementById("login-fehler");

    fehlerBox.style.display = "none";

    fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: email, passwort: passwort })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("falsch");
        }
        return response.json();
    })
    .then(mitarbeiter => {
        // mitarbeiter in sessionStorage speichern
        sessionStorage.setItem("mitarbeiter", JSON.stringify(mitarbeiter));

        if (mitarbeiter.rolle === "ADMIN") {
            window.location.href = "admin.html";
        } else {
            window.location.href = "trainer.html";
        }
    })
    .catch(err => {
        fehlerBox.innerText = "E-Mail oder Passwort ist falsch.";
        fehlerBox.style.display = "block";
    });
}

// login auch per enter
document.addEventListener("keydown", function(e) {
    if (e.key === "Enter") {
        login();
    }
});
