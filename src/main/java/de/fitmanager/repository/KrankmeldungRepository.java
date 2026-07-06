package de.fitmanager.repository;

import de.fitmanager.model.Krankmeldung;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KrankmeldungRepository extends JpaRepository<Krankmeldung, Long> {
}
