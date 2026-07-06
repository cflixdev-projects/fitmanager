package de.fitmanager.repository;

import de.fitmanager.model.Kurs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KursRepository extends JpaRepository<Kurs, Long> {
    List<Kurs> findByTrainerId(Long trainerId);
    List<Kurs> findByWochentag(String wochentag);
}
