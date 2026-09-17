package com.example.QueryMethods_Practica2.repository;

import com.example.QueryMethods_Practica2.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    //Listamos a todos los empleados
    @Query("SELECT e FROM Employee e")
    List<Employee> listarEmpleados();

    //Buscamos empleados por nombre o apellido
    @Query("SELECT e FROM Employee e " +
            "WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :texto, '%')) " +
            "OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :texto, '%'))")
    //El parámetro llamado texto de la consulta corresponde al parámetro texto del método Java
    List<Employee> buscarPorNombreOApellido(@Param("texto") String texto);

    //Buscamos empleado por id
    @Query("SELECT e FROM Employee e WHERE e.employeeId = :id")

    //Busca el objeto Employee cuyo employeeId sea igual al valor que recibo en id
    Employee buscarPorId(@Param("id") Integer id);


    //Actualizamos empleado
    @Modifying
    @Transactional
    @Query("UPDATE Employee e SET " +
            "e.firstName = :#{#employee.firstName}, " +
            "e.lastName = :#{#employee.lastName}, " +
            "e.email = :#{#employee.email}, " +
            "e.phoneNumber = :#{#employee.phoneNumber}, " +
            "e.jobId = :#{#employee.jobId}, " +
            "e.hireDate = :#{#employee.hireDate}, " +
            "e.salary = :#{#employee.salary}, " +
            "e.commissionPct = :#{#employee.commissionPct} " +
            "WHERE e.employeeId = :#{#employee.employeeId}")
    void actualizarEmpleado(@Param("employee") Employee employee);



}