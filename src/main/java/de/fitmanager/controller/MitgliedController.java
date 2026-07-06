package de.fitmanager.controller;

import de.fitmanager.model.Mitglied;
import de.fitmanager.model.Teilnahme;
import de.fitmanager.repository.MitgliedRepository;
import de.fitmanager.repository.TeilnahmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mitglieder")
@CrossOrigin
public class MitgliedController {

    @Autowired
    private MitgliedRepository mitgliedRepository;

    @Autowired
    private TeilnahmeRepository teilnahmeRepository;

    @GetMapping
    public List<Mitglied> alle() {
        return mitgliedRepository.findAll();
    }

    @PostMapping
    public Mitglied erstellen(@RequestBody Mitglied mitglied) {
        return mitgliedRepository.save(mitglied);
    }

    @DeleteMapping("/{id}")
    public void loeschen(@PathVariable Long id) {
        mitgliedRepository.deleteById(id);
    }

    // mitglied fuer kurs anmelden
    @PostMapping("/teilnahme")
    public Teilnahme teilnahmeErstellen(@RequestBody Teilnahme teilnahme) {
        return teilnahmeRepository.save(teilnahme);
    }

    @GetMapping("/teilnahme/kurs/{kursId}")
    public List<Teilnahme> teilnahmenVonKurs(@PathVariable Long kursId) {
        return teilnahmeRepository.findByKursId(kursId);
    }
}
