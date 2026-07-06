package de.fitmanager.service;

import de.fitmanager.model.Mitarbeiter;
import de.fitmanager.repository.MitarbeiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class MitarbeiterService {

    @Autowired
    private MitarbeiterRepository mitarbeiterRepository;

    public List<Mitarbeiter> alleMitarbeiter() {
        return mitarbeiterRepository.findAll();
    }

    public Mitarbeiter mitarbeiterErstellen(Mitarbeiter m) {
        // pw "verschluesseln" (kein echtes hashing)
        m.setPasswort(verschluesseln(m.getPasswort()));
        if (m.getMaxKurseProWoche() == 0 && "TRAINER".equals(m.getRolle())) {
            // standardlimit je nach typ
            m.setMaxKurseProWoche("VOLLZEIT".equals(m.getTyp()) ? 20 : 10);
        }
        return mitarbeiterRepository.save(m);
    }

    public Mitarbeiter mitarbeiterBearbeiten(Long id, Mitarbeiter neueDaten) {
        Mitarbeiter m = mitarbeiterRepository.findById(id).orElse(null);
        if (m == null) {
            return null;
        }
        m.setVorname(neueDaten.getVorname());
        m.setNachname(neueDaten.getNachname());
        m.setEmail(neueDaten.getEmail());
        m.setRolle(neueDaten.getRolle());
        m.setTyp(neueDaten.getTyp());
        m.setMaxKurseProWoche(neueDaten.getMaxKurseProWoche());
        // pw nur aendern wenn neues mitgeschickt
        if (neueDaten.getPasswort() != null && !neueDaten.getPasswort().isEmpty()) {
            m.setPasswort(verschluesseln(neueDaten.getPasswort()));
        }
        return mitarbeiterRepository.save(m);
    }

    public void mitarbeiterLoeschen(Long id) {
        mitarbeiterRepository.deleteById(id);
    }

    public Mitarbeiter login(String email, String passwort) {
        Mitarbeiter m = mitarbeiterRepository.findByEmail(email);
        if (m == null) {
            return null;
        }
        if (m.getPasswort().equals(verschluesseln(passwort))) {
            return m;
        }
        return null;
    }

    public List<Mitarbeiter> alleTrainer() {
        return mitarbeiterRepository.findByRolle("TRAINER");
    }

    private String verschluesseln(String passwort) {
        return Base64.getEncoder().encodeToString(passwort.getBytes());
    }
}
