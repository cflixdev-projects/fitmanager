package de.fitmanager.controller;

import de.fitmanager.model.Studio;
import de.fitmanager.repository.StudioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/studios")
@CrossOrigin
public class StudioController {

    @Autowired
    private StudioRepository studioRepository;

    @GetMapping
    public List<Studio> alle() {
        return studioRepository.findAll();
    }
}
