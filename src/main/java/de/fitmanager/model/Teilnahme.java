package de.fitmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// mitglied nimmt an kurs teil
@Entity
public class Teilnahme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long kursId;
    private Long mitgliedId;

    public Teilnahme() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getKursId() {
        return kursId;
    }

    public void setKursId(Long kursId) {
        this.kursId = kursId;
    }

    public Long getMitgliedId() {
        return mitgliedId;
    }

    public void setMitgliedId(Long mitgliedId) {
        this.mitgliedId = mitgliedId;
    }
}
