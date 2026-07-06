package de.fitmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Kurs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String wochentag; // z.B. "Montag", "Dienstag" usw
    private String startzeit; // als String im Format "HH:mm", einfacher als LocalTime im Frontend
    private int dauerMinuten = 60; // laut Rahmenbedingungen dauert ein Kurs immer 60 Minuten

    private Long trainerId; // wer den Kurs normal betreut
    private Long vertretungTrainerId; // falls der Trainer krank ist, wird hier der Ersatz eingetragen (kann null sein)
    private Long raumId;

    private String status = "GEPLANT"; // GEPLANT, ABGESAGT

    public Kurs() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWochentag() {
        return wochentag;
    }

    public void setWochentag(String wochentag) {
        this.wochentag = wochentag;
    }

    public String getStartzeit() {
        return startzeit;
    }

    public void setStartzeit(String startzeit) {
        this.startzeit = startzeit;
    }

    public int getDauerMinuten() {
        return dauerMinuten;
    }

    public void setDauerMinuten(int dauerMinuten) {
        this.dauerMinuten = dauerMinuten;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public Long getVertretungTrainerId() {
        return vertretungTrainerId;
    }

    public void setVertretungTrainerId(Long vertretungTrainerId) {
        this.vertretungTrainerId = vertretungTrainerId;
    }

    public Long getRaumId() {
        return raumId;
    }

    public void setRaumId(Long raumId) {
        this.raumId = raumId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
