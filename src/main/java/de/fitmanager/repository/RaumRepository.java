package de.fitmanager.repository;

import de.fitmanager.model.Raum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaumRepository extends JpaRepository<Raum, Long> {
}
