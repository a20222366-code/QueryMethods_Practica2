package com.example.QueryMethods_Practica2.repository;

import com.example.QueryMethods_Practica2.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    //Listamos a todos los empleados
    @Query("SELECT e FROM Employee e")
    List<Employee> listarEmpleados();

}