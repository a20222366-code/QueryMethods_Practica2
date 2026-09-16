package com.example.QueryMethods_Practica2.repository;

import com.example.QueryMethods_Practica2.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, String> {

}
