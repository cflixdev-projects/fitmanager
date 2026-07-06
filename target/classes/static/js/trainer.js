// logik traineransicht

let eingeloggterTrainer = null;
let alleTrainerListe = []; // fuer die Anzeige von Vertretungsnamen

window.onload = function () {
    const mitarbeiterJson = sessionStorage.getItem("mitarbeiter");
    if (!mitarbeiterJson) {
        window.location.href = "index.html";
        return;
    }
    eingeloggterTrainer = JSON.parse(mitarbeiterJson);

    if (eingeloggterTrainer.rolle !== "TRAINER") {
        window.location.href = "admin.html";
        return;
    }

    document.getElementById("trainer-name").innerText =
        eingeloggterTrainer.vorname + " " + eingeloggterTrainer.nachname;

    fetch("/api/mitarbeiter/trainer").then(r => r.json()).then(liste => {
        alleTrainerListe = liste;
    });

    uebersichtLaden();
    kurseLaden();

    // alle 4s neu laden, z.b. fuer vertretung
    setInterval(function () {
        uebersichtLaden();
        kurseLaden();
    }, 4000);
};

function logout() {
    sessionStorage.removeItem("mitarbeiter");
    window.location.href = "index.html";
}

function tabWechseln(name, button) {
    document.querySelectorAll(".tab-inhalt").forEach(el => el.classList.remove("sichtbar"));
    document.getElementById("tab-" + name).classList.add("sichtbar");

    document.querySelectorAll(".tab-button").forEach(el => el.classList.remove("aktiv"));
    button.classList.add("aktiv");
}

function uebersichtLaden() {
    fetch("/api/kurse/trainer/" + eingeloggterTrainer.id).then(r => r.json()).then(kurse => {
        document.getElementById("anzahl-eigene-kurse").innerText = kurse.length;
        wochenansichtRendern(kurse);
    });
    document.getElementById("max-kurse").innerText = eingeloggterTrainer.maxKurseProWoche;
    document.getElementById("mein-typ").innerText = eingeloggterTrainer.typ;
}

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
        statusText = "Vertretung: " + findeTrainerName(kurs.vertretungTrainerId);
    }

    div.className = "kurs-kachel " + farbe;
    div.innerHTML = `<div class="kk-name">${kurs.name}</div>${kurs.startzeit} Uhr<br>${statusText}`;
    return div;
}

function kurseLaden() {
    fetch("/api/kurse/trainer/" + eingeloggterTrainer.id).then(r => r.json()).then(kurse => {
        const tbody = document.getElementById("eigene-kurse-body");
        tbody.innerHTML = "";

        if (kurse.length === 0) {
            tbody.innerHTML = "<tr><td colspan='6'>Keine Kurse zugewiesen.</td></tr>";
            return;
        }

        kurse.forEach(kurs => {
            const vertretung = kurs.vertretungTrainerId ? findeTrainerName(kurs.vertretungTrainerId) : "-";

            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${kurs.name}</td>
                <td>${kurs.wochentag}</td>
                <td>${kurs.startzeit}</td>
                <td class="${kurs.status === 'GEPLANT' ? 'status-geplant' : 'status-abgesagt'}">${kurs.status}</td>
                <td>${vertretung}</td>
                <td>
                    ${kurs.status === 'GEPLANT'
                        ? `<button class="btn btn-rot" onclick="krankMelden(${kurs.id})">Krank melden</button>`
                        : "-"}
                </td>
            `;
            tbody.appendChild(tr);
        });
    });
}

function findeTrainerName(id) {
    const t = alleTrainerListe.find(x => x.id === id);
    return t ? t.vorname + " " + t.nachname : "-";
}

function krankMelden(kursId) {
    if (!confirm("Moechtest du dich fuer diesen Kurs wirklich krankmelden? Das System sucht dann automatisch einen Ersatz.")) {
        return;
    }

    const heute = new Date().toISOString().split("T")[0];

    fetch("/api/krankmeldungen", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            trainerId: eingeloggterTrainer.id,
            kursId: kursId,
            datum: heute
        })
    })
    .then(r => r.json())
    .then(ergebnis => {
        if (ergebnis.ersatzTrainerId) {
            alert("Krankmeldung gespeichert. Ein Ersatztrainer wurde automatisch gefunden.");
        } else {
            alert("Krankmeldung gespeichert. Es wurde leider kein Ersatztrainer gefunden, der Kurs faellt aus.");
        }
        kurseLaden();
        uebersichtLaden();
    });
}
