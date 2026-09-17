package com.example.QueryMethods_Practica2.controller;

import com.example.QueryMethods_Practica2.entity.Job;
import com.example.QueryMethods_Practica2.repository.JobRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class JobController {

    private final JobRepository jobRepository;

    public JobController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @GetMapping("/jobs/nuevo")
    public String mostrarFormulario(Model model) {

        model.addAttribute("job", new Job());

        return "nuevo-job";
    }

    @PostMapping("/jobs/guardar")
    public String guardarJob(@ModelAttribute("job") Job job) {

        jobRepository.registrarJob(job);

        return "redirect:/employees";
    }
}