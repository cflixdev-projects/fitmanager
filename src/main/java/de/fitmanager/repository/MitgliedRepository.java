package de.fitmanager.repository;

import de.fitmanager.model.Mitglied;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MitgliedRepository extends JpaRepository<Mitglied, Long> {
}
