package com.example.QueryMethods_Practica2.repository;

import com.example.QueryMethods_Practica2.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, String> {

    //Query para registrar trabajo
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO jobs (job_id, job_title, min_salary, max_salary) " +
            "VALUES (:#{#job.jobId}, :#{#job.jobTitle}, :#{#job.minSalary}, :#{#job.maxSalary})",
            nativeQuery = true)
    void registrarJob(@Param("job") Job job);

    //Listamos trabajos
    @Query("SELECT j FROM Job j")
    List<Job> listarJobs();


}