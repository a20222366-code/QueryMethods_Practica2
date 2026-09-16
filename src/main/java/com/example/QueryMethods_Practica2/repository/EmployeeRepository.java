package com.example.QueryMethods_Practica2.repository;


import com.example.QueryMethods_Practica2.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

}