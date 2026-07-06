package de.fitmanager.controller;

import de.fitmanager.model.Raum;
import de.fitmanager.repository.RaumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/raeume")
@CrossOrigin
public class RaumController {

    @Autowired
    private RaumRepository raumRepository;

    @GetMapping
    public List<Raum> alle() {
        return raumRepository.findAll();
    }
}
