package com.example.QueryMethods_Practica2.controller;

import com.example.QueryMethods_Practica2.entity.Employee;
import com.example.QueryMethods_Practica2.repository.EmployeeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/employees")
    public String listarEmpleados(Model model) {

        List<Employee> employees = employeeRepository.listarEmpleados();

        model.addAttribute("employees", employees);

        return "employees";
    }

    @GetMapping("/employees/buscar")
    public String buscarEmpleados(@RequestParam("texto") String texto,
                                  Model model) {

        List<Employee> employees =
                employeeRepository.buscarPorNombreOApellido(texto);

        model.addAttribute("employees", employees);

        return "employees";
    }
}