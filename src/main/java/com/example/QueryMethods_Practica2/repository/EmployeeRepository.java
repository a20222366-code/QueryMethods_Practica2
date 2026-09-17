package com.example.QueryMethods_Practica2.repository;

import com.example.QueryMethods_Practica2.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    //Listamos a todos los empleados
    @Query("SELECT e FROM Employee e")
    List<Employee> listarEmpleados();

    //Buscamos empleados por nombre o apellido
    @Query("SELECT e FROM Employee e " +
            "WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :texto, '%')) " +
            "OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :texto, '%'))")
    // El parámetro llamado texto de la consulta corresponde al parámetro texto del método Java

    List<Employee> buscarPorNombreOApellido(@Param("texto") String texto);


}