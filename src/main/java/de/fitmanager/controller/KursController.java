package de.fitmanager.controller;

import de.fitmanager.model.Kurs;
import de.fitmanager.service.BenachrichtigungService;
import de.fitmanager.service.KursService;
import de.fitmanager.service.VerteilungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kurse")
@CrossOrigin
public class KursController {

    @Autowired
    private KursService kursService;

    @Autowired
    private BenachrichtigungService benachrichtigungService;

    @Autowired
    private VerteilungService verteilungService;

    // kurse erstellen
    @GetMapping
    public List<Kurs> alleKurse() {
        return kursService.alleKurse();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kurs> einKurs(@PathVariable Long id) {
        Kurs kurs = kursService.findeKurs(id);
        if (kurs == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(kurs);
    }

    @GetMapping("/trainer/{trainerId}")
    public List<Kurs> kurseVonTrainer(@PathVariable Long trainerId) {
        return kursService.kurseVonTrainer(trainerId);
    }

    @PostMapping
    public Kurs kursErstellen(@RequestBody Kurs kurs) {
        Kurs gespeichert = kursService.kursErstellen(kurs);

        // kein trainer ausgewaehlt -> gleich passenden suchen
        if (gespeichert.getTrainerId() == null) {
            verteilungService.kurseAutomatischVerteilen();
            gespeichert = kursService.findeKurs(gespeichert.getId());
        }

        return gespeichert;
    }

    // kurse bearbeiten
    @PutMapping("/{id}")
    public ResponseEntity<Kurs> kursBearbeiten(@PathVariable Long id, @RequestBody Kurs kurs) {
        Kurs aktualisiert = kursService.kursBearbeiten(id, kurs);
        if (aktualisiert == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(aktualisiert);
    }

    // kurse loeschen
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> kursLoeschen(@PathVariable Long id) {
        kursService.kursLoeschen(id);
        return ResponseEntity.ok().build();
    }

    // weist allen kursen ohne trainer einen zu
    @PostMapping("/verteilen")
    public String kurseVerteilen() {
        return verteilungService.kurseAutomatischVerteilen();
    }

    // kurs absagen
    @PostMapping("/{id}/absagen")
    public ResponseEntity<Kurs> kursAbsagen(@PathVariable Long id) {
        Kurs kurs = kursService.findeKurs(id);
        if (kurs == null) {
            return ResponseEntity.notFound().build();
        }
        kurs.setStatus("ABGESAGT");
        Kurs gespeichert = kursService.kursBearbeiten(id, kurs);

        benachrichtigungService.mitgliederInformieren(id,
                "Der Kurs '" + kurs.getName() + "' am " + kurs.getWochentag()
                        + " wurde leider abgesagt.");

        return ResponseEntity.ok(gespeichert);
    }
}
