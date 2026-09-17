package com.example.QueryMethods_Practica2.controller;

import com.example.QueryMethods_Practica2.entity.Employee;
import com.example.QueryMethods_Practica2.entity.Job;
import com.example.QueryMethods_Practica2.repository.EmployeeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.QueryMethods_Practica2.repository.JobRepository;

import java.util.List;

@Controller
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final JobRepository jobRepository;

    public EmployeeController(EmployeeRepository employeeRepository, JobRepository jobRepository) {
        this.employeeRepository = employeeRepository;
        this.jobRepository = jobRepository;
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

    @GetMapping("/employees/editar")
    public String mostrarFormularioEdicion(@RequestParam("id") Integer id,
                                           Model model) {

        Employee employee = employeeRepository.buscarPorId(id);

        List<Job> jobs = jobRepository.listarJobs();

        model.addAttribute("employee", employee);
        model.addAttribute("jobs", jobs);

        return "editar-empleado";
    }

    @PostMapping("/employees/actualizar")
    public String actualizarEmpleado(@ModelAttribute("employee") Employee employee) {

        employeeRepository.actualizarEmpleado(employee);

        return "redirect:/employees";
    }



}

//El controller hará dos consultas:
//1️⃣ EmployeeRepository
//        ↓
//   buscarPorId(100)
//        ↓
//   Employee
//
//2️⃣ JobRepository
//        ↓
//   listarJobs()
//        ↓
//   List<Job>



//Editar
//  ↓
//?id=100
//  ↓
//@Query buscarPorId()
//  ↓
//Employee
//  ↓
//Formulario + Data Binding
//  ↓
//<select> de Jobs
//  ↓
//POST
//  ↓
//@ModelAttribute
//  ↓
//@Query UPDATE
//  ↓
//redirect:/employees