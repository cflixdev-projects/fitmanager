package de.fitmanager.repository;

import de.fitmanager.model.Teilnahme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeilnahmeRepository extends JpaRepository<Teilnahme, Long> {
    List<Teilnahme> findByKursId(Long kursId);
}
