package de.fitmanager.controller;

import de.fitmanager.model.Krankmeldung;
import de.fitmanager.model.Kurs;
import de.fitmanager.model.Mitarbeiter;
import de.fitmanager.repository.KrankmeldungRepository;
import de.fitmanager.service.BenachrichtigungService;
import de.fitmanager.service.KursService;
import de.fitmanager.service.VertretungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// trainer krank -> ersatz suchen -> kursplan updaten -> mitglieder informieren
@RestController
@RequestMapping("/api/krankmeldungen")
@CrossOrigin
public class KrankmeldungController {

    @Autowired
    private KrankmeldungRepository krankmeldungRepository;

    @Autowired
    private KursService kursService;

    @Autowired
    private VertretungService vertretungService;

    @Autowired
    private BenachrichtigungService benachrichtigungService;

    @GetMapping
    public List<Krankmeldung> alle() {
        return krankmeldungRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> krankMelden(@RequestBody Krankmeldung krankmeldung) {

        Kurs kurs = kursService.findeKurs(krankmeldung.getKursId());
        if (kurs == null) {
            return ResponseEntity.badRequest().body("Kurs nicht gefunden");
        }

        // 1. speichern
        krankmeldung.setStatus("OFFEN");
        Krankmeldung gespeichert = krankmeldungRepository.save(krankmeldung);

        // 2. ersatz suchen
        Mitarbeiter ersatz = vertretungService.ersatzTrainerFinden(kurs, krankmeldung.getTrainerId());

        if (ersatz != null) {
            kurs.setVertretungTrainerId(ersatz.getId());
            kursService.kursBearbeiten(kurs.getId(), kurs);

            gespeichert.setErsatzTrainerId(ersatz.getId());
            gespeichert.setStatus("ERLEDIGT");
            krankmeldungRepository.save(gespeichert);

            // 3. mitglieder informieren
            benachrichtigungService.mitgliederInformieren(kurs.getId(),
                    "Der Kurs '" + kurs.getName() + "' wird heute von "
                            + ersatz.getVorname() + " " + ersatz.getNachname() + " vertreten.");
        } else {
            // kein ersatz -> absagen
            kurs.setStatus("ABGESAGT");
            kursService.kursBearbeiten(kurs.getId(), kurs);

            benachrichtigungService.mitgliederInformieren(kurs.getId(),
                    "Der Kurs '" + kurs.getName() + "' faellt leider aus (keine Vertretung gefunden).");
        }

        return ResponseEntity.ok(gespeichert);
    }

    @GetMapping("/nachrichten")
    public List<String> gesendeteNachrichten() {
        return benachrichtigungService.getGesendeteNachrichten();
    }
}
