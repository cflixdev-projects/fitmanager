package de.fitmanager.controller;

import de.fitmanager.model.Mitarbeiter;
import de.fitmanager.service.MitarbeiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// mitarbeiter verwalten
@RestController
@RequestMapping("/api/mitarbeiter")
@CrossOrigin
public class MitarbeiterController {

    @Autowired
    private MitarbeiterService mitarbeiterService;

    @GetMapping
    public List<Mitarbeiter> alle() {
        List<Mitarbeiter> liste = mitarbeiterService.alleMitarbeiter();
        // pw nicht mitschicken
        for (Mitarbeiter m : liste) {
            m.setPasswort(null);
        }
        return liste;
    }

    @GetMapping("/trainer")
    public List<Mitarbeiter> alleTrainer() {
        List<Mitarbeiter> liste = mitarbeiterService.alleTrainer();
        for (Mitarbeiter m : liste) {
            m.setPasswort(null);
        }
        return liste;
    }

    @PostMapping
    public Mitarbeiter erstellen(@RequestBody Mitarbeiter mitarbeiter) {
        Mitarbeiter gespeichert = mitarbeiterService.mitarbeiterErstellen(mitarbeiter);
        gespeichert.setPasswort(null);
        return gespeichert;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mitarbeiter> bearbeiten(@PathVariable Long id, @RequestBody Mitarbeiter mitarbeiter) {
        Mitarbeiter aktualisiert = mitarbeiterService.mitarbeiterBearbeiten(id, mitarbeiter);
        if (aktualisiert == null) {
            return ResponseEntity.notFound().build();
        }
        aktualisiert.setPasswort(null);
        return ResponseEntity.ok(aktualisiert);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> loeschen(@PathVariable Long id) {
        mitarbeiterService.mitarbeiterLoeschen(id);
        return ResponseEntity.ok().build();
    }
}
