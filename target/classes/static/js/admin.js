// logik adminseite, alles in einer datei

let alleMitarbeiterListe = []; // wird gebraucht um bei den Kursen den Trainernamen anzuzeigen
let alleRaeumeListe = [];
let alleStudiosListe = [];

// beim laden

window.onload = function () {
    const mitarbeiterJson = sessionStorage.getItem("mitarbeiter");
    if (!mitarbeiterJson) {
        // nicht eingeloggt -> zurueck zum login
        window.location.href = "index.html";
        return;
    }
    const mitarbeiter = JSON.parse(mitarbeiterJson);
    if (mitarbeiter.rolle !== "ADMIN") {
        window.location.href = "trainer.html";
        return;
    }
    document.getElementById("admin-name").innerText = mitarbeiter.vorname + " " + mitarbeiter.nachname;

    uebersichtLaden();
    studiosLaden();
    kurseLaden();
    mitarbeiterLaden();
    raeumeLaden();

    // alle 4s neu abfragen statt reload
    setInterval(function () {
        uebersichtLaden();
        kurseLaden();
        mitarbeiterLaden();
    }, 4000);
};

function logout() {
    sessionStorage.removeItem("mitarbeiter");
    window.location.href = "index.html";
}

// tabs wechseln

function tabWechseln(name, button) {
    document.querySelectorAll(".tab-inhalt").forEach(el => el.classList.remove("sichtbar"));
    document.getElementById("tab-" + name).classList.add("sichtbar");

    document.querySelectorAll(".tab-button").forEach(el => el.classList.remove("aktiv"));
    button.classList.add("aktiv");
}

// uebersicht

function uebersichtLaden() {
    fetch("/api/kurse").then(r => r.json()).then(kurse => {
        document.getElementById("anzahl-kurse").innerText = kurse.length;
        wochenansichtRendern(kurse);
    });

    fetch("/api/mitarbeiter").then(r => r.json()).then(mitarbeiter => {
        document.getElementById("anzahl-mitarbeiter").innerText = mitarbeiter.length;
        const trainerAnzahl = mitarbeiter.filter(m => m.rolle === "TRAINER").length;
        document.getElementById("anzahl-trainer").innerText = trainerAnzahl;
    });

    fetch("/api/krankmeldungen").then(r => r.json()).then(liste => {
        document.getElementById("anzahl-krankmeldungen").innerText = liste.length;
    });

    fetch("/api/krankmeldungen/nachrichten").then(r => r.json()).then(nachrichten => {
        const ul = document.getElementById("nachrichten-liste");
        ul.innerHTML = "";
        if (nachrichten.length === 0) {
            ul.innerHTML = "<li>Noch keine Benachrichtigungen versendet.</li>";
        }
        nachrichten.slice().reverse().forEach(n => {
            const li = document.createElement("li");
            li.innerText = n;
            ul.appendChild(li);
        });
    });
}

// mo-so kachelansicht, jeder tag eine spalte
const WOCHENTAGE = ["Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"];
const WOCHENTAGE_KURZ = ["Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"];

function wochenansichtRendern(kurse) {
    const container = document.getElementById("wochenansicht");
    if (!container) return;
    container.innerHTML = "";

    WOCHENTAGE.forEach((tag, index) => {
        const spalte = document.createElement("div");
        spalte.className = "tag-spalte";

        const titel = document.createElement("div");
        titel.className = "tag-titel";
        titel.innerText = WOCHENTAGE_KURZ[index];
        spalte.appendChild(titel);

        kurse.filter(k => k.wochentag === tag)
            .sort((a, b) => a.startzeit.localeCompare(b.startzeit))
            .forEach(kurs => {
                spalte.appendChild(kursKachelBauen(kurs));
            });

        container.appendChild(spalte);
    });
}

function kursKachelBauen(kurs) {
    const div = document.createElement("div");

    let farbe = "kk-gruen";
    let statusText = "Findet statt";
    if (kurs.status === "ABGESAGT") {
        farbe = "kk-rot";
        statusText = "Abgesagt";
    } else if (kurs.vertretungTrainerId) {
        farbe = "kk-gelb";
        statusText = "Vertretung";
    }

    div.className = "kurs-kachel " + farbe;
    div.innerHTML = `<div class="kk-name">${kurs.name}</div>${kurs.startzeit} Uhr<br>${statusText}`;
    return div;
}

// kurse

function kurseLaden() {
    fetch("/api/kurse").then(r => r.json()).then(kurse => {
        const tbody = document.getElementById("kurs-tabelle-body");
        tbody.innerHTML = "";

        kurse.forEach(kurs => {
            const trainer = findeMitarbeiter(kurs.trainerId);
            const vertretung = kurs.vertretungTrainerId ? findeMitarbeiter(kurs.vertretungTrainerId) : null;
            const standort = standortVonRaum(kurs.raumId);

            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${kurs.name}</td>
                <td>${kurs.wochentag}</td>
                <td>${kurs.startzeit}</td>
                <td>${standort ? standort.name : "-"}</td>
                <td>${trainer ? trainer.vorname + " " + trainer.nachname : "<i>noch offen</i>"}</td>
                <td>${vertretung ? vertretung.vorname + " " + vertretung.nachname : "-"}</td>
                <td class="${kurs.status === 'GEPLANT' ? 'status-geplant' : 'status-abgesagt'}">${kurs.status}</td>
                <td>
                    <button class="btn btn-blau" onclick="kursBearbeitenAnzeigen(${kurs.id})">Bearbeiten</button>
                    <button class="btn btn-rot" onclick="kursLoeschen(${kurs.id})">Löschen</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    });
}

function standortVonRaum(raumId) {
    const raum = alleRaeumeListe.find(r => r.id === raumId);
    if (!raum) return null;
    return alleStudiosListe.find(s => s.id === raum.studioId);
}

// verteilt alle kurse ohne trainer
function kurseVerteilen() {
    fetch("/api/kurse/verteilen", { method: "POST" }).then(r => r.text()).then(text => {
        document.getElementById("verteilen-hinweis").innerText = text;
        kurseLaden();
    });
}

function findeMitarbeiter(id) {
    return alleMitarbeiterListe.find(m => m.id === id);
}

function kursFormularAnzeigen() {
    document.getElementById("kurs-formular").classList.remove("versteckt");
    document.getElementById("kurs-id").value = "";
    document.getElementById("kurs-name").value = "";
    document.getElementById("kurs-startzeit").value = "";
    kursTrainerAuswahlFuellen();
    kursRaumAuswahlFuellen();
}

function kursFormularVerstecken() {
    document.getElementById("kurs-formular").classList.add("versteckt");
}

function kursTrainerAuswahlFuellen() {
    const select = document.getElementById("kurs-trainer");
    select.innerHTML = '<option value="">-- automatisch zuweisen --</option>';
    alleMitarbeiterListe.filter(m => m.rolle === "TRAINER").forEach(t => {
        const option = document.createElement("option");
        option.value = t.id;
        option.innerText = t.vorname + " " + t.nachname;
        select.appendChild(option);
    });
}

function kursRaumAuswahlFuellen() {
    const select = document.getElementById("kurs-raum");
    select.innerHTML = "";
    alleRaeumeListe.forEach(r => {
        const option = document.createElement("option");
        option.value = r.id;
        option.innerText = r.name;
        select.appendChild(option);
    });
}

function kursBearbeitenAnzeigen(id) {
    fetch("/api/kurse/" + id).then(r => r.json()).then(kurs => {
        kursFormularAnzeigen();
        document.getElementById("kurs-id").value = kurs.id;
        document.getElementById("kurs-name").value = kurs.name;
        document.getElementById("kurs-wochentag").value = kurs.wochentag;
        document.getElementById("kurs-startzeit").value = kurs.startzeit;
        document.getElementById("kurs-trainer").value = kurs.trainerId;
        document.getElementById("kurs-raum").value = kurs.raumId;
    });
}

function kursSpeichern() {
    const id = document.getElementById("kurs-id").value;

    const trainerWert = document.getElementById("kurs-trainer").value;

    const kurs = {
        name: document.getElementById("kurs-name").value,
        wochentag: document.getElementById("kurs-wochentag").value,
        startzeit: document.getElementById("kurs-startzeit").value,
        // leer = spaeter automatisch verteilt
        trainerId: trainerWert ? parseInt(trainerWert) : null,
        raumId: parseInt(document.getElementById("kurs-raum").value),
        status: "GEPLANT"
    };

    const url = id ? "/api/kurse/" + id : "/api/kurse";
    const methode = id ? "PUT" : "POST";

    fetch(url, {
        method: methode,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(kurs)
    }).then(() => {
        kursFormularVerstecken();
        kurseLaden();
        uebersichtLaden();
    });
}

function kursLoeschen(id) {
    if (!confirm("Kurs wirklich loeschen?")) {
        return;
    }
    fetch("/api/kurse/" + id, { method: "DELETE" }).then(() => {
        kurseLaden();
        uebersichtLaden();
    });
}

// mitarbeiter

function mitarbeiterLaden() {
    fetch("/api/mitarbeiter").then(r => r.json()).then(liste => {
        alleMitarbeiterListe = liste;

        const tbody = document.getElementById("mitarbeiter-tabelle-body");
        tbody.innerHTML = "";

        liste.forEach(m => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${m.vorname} ${m.nachname}</td>
                <td>${m.email}</td>
                <td>${m.rolle}</td>
                <td>${m.typ ? m.typ : "-"}</td>
                <td>${m.studioId ? standortName(m.studioId) : "-"}</td>
                <td>${m.maxKurseProWoche}</td>
                <td>
                    <button class="btn btn-blau" onclick="mitarbeiterBearbeitenAnzeigen(${m.id})">Bearbeiten</button>
                    <button class="btn btn-rot" onclick="mitarbeiterLoeschen(${m.id})">Löschen</button>
                </td>
            `;
            tbody.appendChild(tr);
        });

        // trainer-auswahl im formular updaten
        kursTrainerAuswahlFuellen();
    });
}

function rolleGeaendert() {
    const rolle = document.getElementById("ma-rolle").value;
    document.getElementById("ma-typ").style.display = rolle === "TRAINER" ? "inline-block" : "none";
    document.getElementById("ma-studio").style.display = rolle === "TRAINER" ? "inline-block" : "none";
}

function mitarbeiterFormularAnzeigen() {
    document.getElementById("mitarbeiter-formular").classList.remove("versteckt");
    document.getElementById("ma-id").value = "";
    document.getElementById("ma-vorname").value = "";
    document.getElementById("ma-nachname").value = "";
    document.getElementById("ma-email").value = "";
    document.getElementById("ma-passwort").value = "";
    document.getElementById("ma-rolle").value = "TRAINER";
    rolleGeaendert();
}

function mitarbeiterFormularVerstecken() {
    document.getElementById("mitarbeiter-formular").classList.add("versteckt");
}

function mitarbeiterBearbeitenAnzeigen(id) {
    const m = findeMitarbeiter(id);
    if (!m) return;

    mitarbeiterFormularAnzeigen();
    document.getElementById("ma-id").value = m.id;
    document.getElementById("ma-vorname").value = m.vorname;
    document.getElementById("ma-nachname").value = m.nachname;
    document.getElementById("ma-email").value = m.email;
    document.getElementById("ma-passwort").value = ""; // Passwort wird nie angezeigt
    document.getElementById("ma-rolle").value = m.rolle;
    document.getElementById("ma-typ").value = m.typ ? m.typ : "VOLLZEIT";
    if (m.studioId) {
        document.getElementById("ma-studio").value = m.studioId;
    }
    rolleGeaendert();
}

function mitarbeiterSpeichern() {
    const id = document.getElementById("ma-id").value;
    const rolle = document.getElementById("ma-rolle").value;

    const mitarbeiter = {
        vorname: document.getElementById("ma-vorname").value,
        nachname: document.getElementById("ma-nachname").value,
        email: document.getElementById("ma-email").value,
        passwort: document.getElementById("ma-passwort").value,
        rolle: rolle,
        typ: rolle === "TRAINER" ? document.getElementById("ma-typ").value : null,
        maxKurseProWoche: rolle === "TRAINER" ? (document.getElementById("ma-typ").value === "VOLLZEIT" ? 20 : 10) : 0,
        studioId: rolle === "TRAINER" ? parseInt(document.getElementById("ma-studio").value) : null
    };

    if (!id && !mitarbeiter.passwort) {
        alert("Bitte ein Passwort vergeben.");
        return;
    }

    const url = id ? "/api/mitarbeiter/" + id : "/api/mitarbeiter";
    const methode = id ? "PUT" : "POST";

    fetch(url, {
        method: methode,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(mitarbeiter)
    }).then(() => {
        mitarbeiterFormularVerstecken();
        mitarbeiterLaden();
        uebersichtLaden();
    });
}

function mitarbeiterLoeschen(id) {
    if (!confirm("Mitarbeiter wirklich loeschen?")) {
        return;
    }
    fetch("/api/mitarbeiter/" + id, { method: "DELETE" }).then(() => {
        mitarbeiterLaden();
        uebersichtLaden();
    });
}

// raeume

function raeumeLaden() {
    // kein eigener tab, nur fuers dropdown gebraucht
    fetch("/api/raeume").then(r => r.json()).then(liste => {
        alleRaeumeListe = liste;
    });
}

// standorte

function studiosLaden() {
    fetch("/api/studios").then(r => r.json()).then(liste => {
        alleStudiosListe = liste;
        const select = document.getElementById("ma-studio");
        select.innerHTML = "";
        liste.forEach(s => {
            const option = document.createElement("option");
            option.value = s.id;
            option.innerText = s.name;
            select.appendChild(option);
        });
    });
}

function standortName(id) {
    const s = alleStudiosListe.find(x => x.id === id);
    return s ? s.name : "-";
}