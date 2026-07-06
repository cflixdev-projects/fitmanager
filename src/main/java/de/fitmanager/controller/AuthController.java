package de.fitmanager.controller;

import de.fitmanager.model.Mitarbeiter;
import de.fitmanager.service.MitarbeiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// simpler login, kein spring security/jwt. frontend merkt sich
// daten einfach im sessionStorage
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private MitarbeiterService mitarbeiterService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> daten) {
        String email = daten.get("email");
        String passwort = daten.get("passwort");

        Mitarbeiter m = mitarbeiterService.login(email, passwort);

        if (m == null) {
            return ResponseEntity.status(401).body("E-Mail oder Passwort falsch");
        }

        // pw nicht mitschicken
        m.setPasswort(null);
        return ResponseEntity.ok(m);
    }
}
