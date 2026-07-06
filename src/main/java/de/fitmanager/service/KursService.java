package de.fitmanager.service;

import de.fitmanager.model.Kurs;
import de.fitmanager.repository.KursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KursService {

    @Autowired
    private KursRepository kursRepository;

    public List<Kurs> alleKurse() {
        return kursRepository.findAll();
    }

    public Kurs kursErstellen(Kurs kurs) {
        // default 60 min
        if (kurs.getDauerMinuten() == 0) {
            kurs.setDauerMinuten(60);
        }
        return kursRepository.save(kurs);
    }

    public Kurs kursBearbeiten(Long id, Kurs neueDaten) {
        Kurs kurs = kursRepository.findById(id).orElse(null);
        if (kurs == null) {
            return null;
        }
        kurs.setName(neueDaten.getName());
        kurs.setWochentag(neueDaten.getWochentag());
        kurs.setStartzeit(neueDaten.getStartzeit());
        kurs.setDauerMinuten(neueDaten.getDauerMinuten());
        kurs.setTrainerId(neueDaten.getTrainerId());
        kurs.setRaumId(neueDaten.getRaumId());
        kurs.setStatus(neueDaten.getStatus());
        return kursRepository.save(kurs);
    }

    public void kursLoeschen(Long id) {
        kursRepository.deleteById(id);
    }

    public List<Kurs> kurseVonTrainer(Long trainerId) {
        return kursRepository.findByTrainerId(trainerId);
    }

    public Kurs findeKurs(Long id) {
        return kursRepository.findById(id).orElse(null);
    }
}
