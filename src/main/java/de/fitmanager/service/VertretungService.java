package de.fitmanager.service;

import de.fitmanager.model.Kurs;
import de.fitmanager.model.Mitarbeiter;
import de.fitmanager.model.Raum;
import de.fitmanager.model.Verfuegbarkeit;
import de.fitmanager.repository.KursRepository;
import de.fitmanager.repository.MitarbeiterRepository;
import de.fitmanager.repository.RaumRepository;
import de.fitmanager.repository.VerfuegbarkeitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// sucht ersatztrainer bei krankmeldung. erster trainer der passt (gleicher
// standort, verfuegbar, kein zeitkonflikt, limit nicht voll) wird genommen
@Service
public class VertretungService {

    @Autowired
    private MitarbeiterRepository mitarbeiterRepository;

    @Autowired
    private VerfuegbarkeitRepository verfuegbarkeitRepository;

    @Autowired
    private KursRepository kursRepository;

    @Autowired
    private RaumRepository raumRepository;

    public Mitarbeiter ersatzTrainerFinden(Kurs kurs, Long krankerTrainerId) {
        List<Mitarbeiter> alleTrainer = mitarbeiterRepository.findByRolle("TRAINER");

        Long standortVomKurs = standortDesKurses(kurs);

        for (Mitarbeiter trainer : alleTrainer) {

            // kranker trainer faellt raus
            if (trainer.getId().equals(krankerTrainerId)) {
                continue;
            }

            // nur gleicher standort
            if (standortVomKurs != null && trainer.getStudioId() != null
                    && !standortVomKurs.equals(trainer.getStudioId())) {
                continue;
            }

            if (!istVerfuegbar(trainer.getId(), kurs)) {
                continue;
            }

            if (hatSchonKursZurGleichenZeit(trainer.getId(), kurs)) {
                continue;
            }

            if (hatWochenlimitErreicht(trainer)) {
                continue;
            }

            // passt -> nehmen
            return trainer;
        }

        // nichts gefunden
        return null;
    }

    // raum -> standort
    private Long standortDesKurses(Kurs kurs) {
        if (kurs.getRaumId() == null) {
            return null;
        }
        Raum raum = raumRepository.findById(kurs.getRaumId()).orElse(null);
        return raum != null ? raum.getStudioId() : null;
    }

    private boolean istVerfuegbar(Long trainerId, Kurs kurs) {
        List<Verfuegbarkeit> verfuegbarkeiten = verfuegbarkeitRepository.findByTrainerId(trainerId);

        // keine eintraege -> gilt als immer verfuegbar
        if (verfuegbarkeiten.isEmpty()) {
            return true;
        }

        for (Verfuegbarkeit v : verfuegbarkeiten) {
            if (v.getWochentag().equalsIgnoreCase(kurs.getWochentag())
                    && kurs.getStartzeit().compareTo(v.getVon()) >= 0
                    && kurs.getStartzeit().compareTo(v.getBis()) < 0) {
                return true;
            }
        }
        return false;
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

    private boolean hatWochenlimitErreicht(Mitarbeiter trainer) {
        int anzahlKurse = kursRepository.findByTrainerId(trainer.getId()).size();
        return anzahlKurse >= trainer.getMaxKurseProWoche();
    }
}
