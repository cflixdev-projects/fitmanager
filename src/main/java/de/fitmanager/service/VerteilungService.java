package de.fitmanager.service;

import de.fitmanager.model.Kurs;
import de.fitmanager.model.Mitarbeiter;
import de.fitmanager.model.Raum;
import de.fitmanager.repository.KursRepository;
import de.fitmanager.repository.MitarbeiterRepository;
import de.fitmanager.repository.RaumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// verteilt kurse ohne trainer automatisch, per knopf im admin bereich
// nimmt jeweils den trainer mit den wenigsten kursen (gleicher standort,
// kein zeitkonflikt, wochenlimit nicht voll)
@Service
public class VerteilungService {

    @Autowired
    private KursRepository kursRepository;

    @Autowired
    private MitarbeiterRepository mitarbeiterRepository;

    @Autowired
    private RaumRepository raumRepository;

    public String kurseAutomatischVerteilen() {
        List<Kurs> alleKurse = kursRepository.findAll();
        List<Mitarbeiter> alleTrainer = mitarbeiterRepository.findByRolle("TRAINER");

        // anzahl kurse pro trainer, key = trainerId
        Map<Long, Integer> kurseProTrainer = new HashMap<>();
        for (Mitarbeiter t : alleTrainer) {
            int schon = kursRepository.findByTrainerId(t.getId()).size();
            kurseProTrainer.put(t.getId(), schon);
        }

        int zaehlerVerteilt = 0;

        for (Kurs kurs : alleKurse) {
            if (kurs.getTrainerId() != null) {
                continue; // hat schon einen Trainer, nichts zu tun
            }

            Long standortKurs = standortDesKurses(kurs);
            Mitarbeiter bester = null;
            int wenigsteKurse = Integer.MAX_VALUE;

            for (Mitarbeiter trainer : alleTrainer) {

                if (standortKurs != null && trainer.getStudioId() != null
                        && !standortKurs.equals(trainer.getStudioId())) {
                    continue; // anderer standort
                }

                int aktuelleAnzahl = kurseProTrainer.get(trainer.getId());
                if (aktuelleAnzahl >= trainer.getMaxKurseProWoche()) {
                    continue; // limit voll
                }

                if (hatSchonKursZurGleichenZeit(trainer.getId(), kurs)) {
                    continue;
                }

                // wenigste kurse gewinnt
                if (aktuelleAnzahl < wenigsteKurse) {
                    wenigsteKurse = aktuelleAnzahl;
                    bester = trainer;
                }
            }

            if (bester != null) {
                kurs.setTrainerId(bester.getId());
                kursRepository.save(kurs);
                kurseProTrainer.put(bester.getId(), kurseProTrainer.get(bester.getId()) + 1);
                zaehlerVerteilt++;
            }
        }

        return zaehlerVerteilt + " Kurs(e) wurden automatisch einem Trainer zugewiesen.";
    }

    private Long standortDesKurses(Kurs kurs) {
        if (kurs.getRaumId() == null) {
            return null;
        }
        Raum raum = raumRepository.findById(kurs.getRaumId()).orElse(null);
        return raum != null ? raum.getStudioId() : null;
    }

    private boolean hatSchonKursZurGleichenZeit(Long trainerId, Kurs kurs) {
        List<Kurs> kurseVomTrainer = kursRepository.findByTrainerId(trainerId);
        for (Kurs k : kurseVomTrainer) {
            if (k.getWochentag().equalsIgnoreCase(kurs.getWochentag())
                    && k.getStartzeit().equals(kurs.getStartzeit())) {
                return true;
            }
        }
        return false;
    }
}
