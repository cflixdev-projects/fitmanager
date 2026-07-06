package de.fitmanager.repository;

import de.fitmanager.model.Verfuegbarkeit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerfuegbarkeitRepository extends JpaRepository<Verfuegbarkeit, Long> {
    List<Verfuegbarkeit> findByTrainerId(Long trainerId);
}
