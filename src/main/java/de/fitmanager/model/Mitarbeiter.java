package de.fitmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// admin oder trainer, steuert das feld "rolle"
@Entity
public class Mitarbeiter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vorname;
    private String nachname;
    private String email;

    // todo: echtes hashing (bcrypt), aktuell nur base64
    private String passwort;

    private String rolle; // "ADMIN" oder "TRAINER"

    private String typ; // "VOLLZEIT" oder "TEILZEIT" (nur relevant fuer Trainer)

    private int maxKurseProWoche; // Vollzeit = 20, Teilzeit = 10 (siehe Rahmenbedingungen)

    // standort des trainers, wichtig fuer vertretungssuche
    private Long studioId;

    public Mitarbeiter() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public String getRolle() {
        return rolle;
    }

    public void setRolle(String rolle) {
        this.rolle = rolle;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public int getMaxKurseProWoche() {
        return maxKurseProWoche;
    }

    public void setMaxKurseProWoche(int maxKurseProWoche) {
        this.maxKurseProWoche = maxKurseProWoche;
    }

    public Long getStudioId() {
        return studioId;
    }

    public void setStudioId(Long studioId) {
        this.studioId = studioId;
    }
}
