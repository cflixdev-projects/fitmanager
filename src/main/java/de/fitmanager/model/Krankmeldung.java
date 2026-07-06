package de.fitmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Krankmeldung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long trainerId;
    private Long kursId;
    private String datum; // Format "yyyy-MM-dd", einfacher als LocalDate fuer das Frontend

    private String status = "OFFEN"; // OFFEN = noch keinen Ersatz gefunden, ERLEDIGT = Ersatz gefunden

    private Long ersatzTrainerId; // kann null bleiben wenn kein Ersatz gefunden wurde

    public Krankmeldung() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public Long getKursId() {
        return kursId;
    }

    public void setKursId(Long kursId) {
        this.kursId = kursId;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getErsatzTrainerId() {
        return ersatzTrainerId;
    }

    public void setErsatzTrainerId(Long ersatzTrainerId) {
        this.ersatzTrainerId = ersatzTrainerId;
    }
}
