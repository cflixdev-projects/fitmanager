package de.fitmanager.service;

import de.fitmanager.model.Mitglied;
import de.fitmanager.model.Teilnahme;
import de.fitmanager.repository.MitgliedRepository;
import de.fitmanager.repository.TeilnahmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// informiert mitglieder. keine echten mails, nur konsole + liste fuers frontend
@Service
public class BenachrichtigungService {

    @Autowired
    private TeilnahmeRepository teilnahmeRepository;

    @Autowired
    private MitgliedRepository mitgliedRepository;

    private final List<String> gesendeteNachrichten = new ArrayList<>();

    public void mitgliederInformieren(Long kursId, String nachricht) {
        List<Teilnahme> teilnahmen = teilnahmeRepository.findByKursId(kursId);

        for (Teilnahme t : teilnahmen) {
            Mitglied mitglied = mitgliedRepository.findById(t.getMitgliedId()).orElse(null);
            if (mitglied != null) {
                String text = "An " + mitglied.getVorname() + " " + mitglied.getNachname()
                        + " (" + mitglied.getEmail() + "): " + nachricht;
                // todo: echten mail versand einbauen
                System.out.println("[Benachrichtigung] " + text);
                gesendeteNachrichten.add(text);
            }
        }
    }

    public List<String> getGesendeteNachrichten() {
        return gesendeteNachrichten;
    }
}
