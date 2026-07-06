package de.fitmanager.repository;

import de.fitmanager.model.Mitarbeiter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MitarbeiterRepository extends JpaRepository<Mitarbeiter, Long> {
    Mitarbeiter findByEmail(String email);
    List<Mitarbeiter> findByRolle(String rolle);
}
