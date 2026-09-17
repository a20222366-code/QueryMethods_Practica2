# @Query vs Query Methods — Comparación punto por punto

Basado en tu proyecto `QueryMethods_Practica2`. La idea clave para el examen:

> **Lo único que cambia es el REPOSITORIO.** El Controller casi no cambia (salvo 1-2 detalles con `Optional`), y el HTML **no cambia nada**, porque a la vista le da igual de dónde salió la lista/objeto — solo recibe el `Model`.

---

## 1. Listado de empleados (Pregunta 2)

### Con @Query (lo que hiciste)
```java
// EmployeeRepository
@Query("SELECT e FROM Employee e")
List<Employee> listarEmpleados();
```
```java
// Controller
List<Employee> employees = employeeRepository.listarEmpleados();
```

### Con Query Method
```java
// EmployeeRepository
// No hace falta declarar nada propio: JpaRepository ya lo trae
List<Employee> findAll(); // heredado, ni siquiera hay que escribirlo
```
```java
// Controller
List<Employee> employees = employeeRepository.findAll();
```

**Diferencia:** con Query Methods usas el método `findAll()` que **ya viene incluido** en `JpaRepository`. No escribes JPQL, no hay anotación `@Query`. El HTML (`employees.html`) queda exactamente igual, porque sigue recibiendo `List<Employee>` en el modelo con la misma clave `"employees"`.

---

## 2. Búsqueda por nombre/apellido (Pregunta 3)

### Con @Query (lo que hiciste)
```java
@Query("SELECT e FROM Employee e " +
        "WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :texto, '%')) " +
        "OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :texto, '%'))")
List<Employee> buscarPorNombreOApellido(@Param("texto") String texto);
```

### Con Query Method
```java
// El nombre del método ES la consulta. Spring lo traduce solo.
List<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
        String firstName, String lastName);
```
```java
// Controller
List<Employee> employees =
    employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(texto, texto);
```

**Diferencia:** en vez de escribir JPQL a mano, compones el nombre del método con palabras clave que Spring Data entiende:
- `findBy` → inicio obligatorio
- `FirstName` / `LastName` → nombre del atributo de la entidad (case-sensitive, debe coincidir)
- `Containing` → equivale al `LIKE %texto%`
- `IgnoreCase` → equivale al `LOWER(...)`
- `Or` → equivale al `OR` de tu JPQL

No hay `@Param`, no hay `@Query`, no hay JPQL manual. El riesgo: si escribes mal el nombre del atributo (ej. `FristName`), **no compila / falla en tiempo de arranque**, porque Spring intenta parsear el nombre del método literalmente.

---

## 3. Buscar empleado por ID (Pregunta 5 - parte 1)

### Con @Query (lo que hiciste)
```java
@Query("SELECT e FROM Employee e WHERE e.employeeId = :id")
Employee buscarPorId(@Param("id") Integer id);
```
```java
// Controller
Employee employee = employeeRepository.buscarPorId(id);
```

### Con Query Method
```java
// Heredado de JpaRepository, no lo escribes tú
Optional<Employee> findById(Integer id);
```
```java
// Controller — OJO: findById() devuelve Optional<Employee>, no Employee directo
Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
```

**Diferencia clave (la más importante para el examen):** `findById()` devuelve `Optional<Employee>`, **no** `Employee`. Esto es lo que más suele cambiar el Controller: tienes que "desempaquetar" el Optional con `.get()`, `.orElseThrow()` o `.orElse(null)`. Con `@Query`, tú decides el tipo de retorno (`Employee` directo), así que te ahorras ese paso.

---

## 4. Listar Jobs para el `<select>` (Pregunta 5 - parte 2)

### Con @Query (lo que hiciste)
```java
@Query("SELECT j FROM Job j")
List<Job> listarJobs();
```

### Con Query Method
```java
List<Job> findAll(); // heredado
```

Igual que el caso 1: `findAll()` reemplaza tu `listarJobs()`. El `<select>` en `editar-empleado.html` no cambia en nada.

---

## 5. Registrar un Job nuevo (Pregunta 4)

### Con @Query (lo que hiciste)
```java
@Modifying
@Transactional
@Query(value = "INSERT INTO jobs (job_id, job_title, min_salary, max_salary) " +
        "VALUES (:#{#job.jobId}, :#{#job.jobTitle}, :#{#job.minSalary}, :#{#job.maxSalary})",
        nativeQuery = true)
void registrarJob(@Param("job") Job job);
```
```java
// Controller
jobRepository.registrarJob(job);
```

### Con Query Method (en realidad, ni siquiera es un "Query Method")
```java
// No existe un "Query Method" para INSERT. Se usa el método heredado save().
<S extends Job> S save(S job);
```
```java
// Controller
jobRepository.save(job);
```

**Diferencia:** esto es importante entenderlo para el examen — **los Query Methods no generan INSERT/UPDATE**, solo generan `SELECT` (y `deleteBy...` para borrar). Para insertar o actualizar sin `@Query`, la forma "automática" de Spring Data JPA es `save()`, que internamente hace un `INSERT` (si la PK es null/no existe) o un `UPDATE` (si ya existe). Por eso el enunciado de tu práctica dice explícitamente *"no se permite utilizar save()"*: es la alternativa automática que reemplazaría tu `@Query` de inserción.

---

## 6. Actualizar empleado (Pregunta 5 - parte 3)

### Con @Query (lo que hiciste)
```java
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
```
```java
// Controller
employeeRepository.actualizarEmpleado(employee);
```

### Con Query Method / forma automática
```java
// No hay Query Method para UPDATE. Se usa save() (heredado de JpaRepository)
<S extends Employee> S save(S employee);
```
```java
// Controller
employeeRepository.save(employee); // como employeeId ya existe, hace UPDATE
```

**Diferencia:** igual que el INSERT, aquí `save()` detecta que el `employeeId` ya existe en la BD y genera un `UPDATE` de **todas** las columnas mapeadas. Tu versión con `@Query` es más controlada porque tú decides exactamente qué columnas se actualizan (por eso el enunciado pide no tocar `manager_id` ni `department_id`; con `save()` esos campos también se sobrescribirían si el objeto `Employee` no los trae seteados, ¡y podrías dejarlos en `null` por accidente!).

---

## Resumen: qué reemplaza a qué

| Operación en tu práctica | Tu solución con `@Query` | Equivalente "automático" (Query Method / heredado) |
|---|---|---|
| Listar todos los empleados | `listarEmpleados()` con `@Query` | `findAll()` (heredado) |
| Buscar por texto en nombre/apellido | `buscarPorNombreOApellido()` con `@Query` + `@Param` | `findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(...)` |
| Buscar empleado por ID | `buscarPorId()` con `@Query` → devuelve `Employee` | `findById(id)` (heredado) → devuelve `Optional<Employee>` |
| Listar todos los Jobs | `listarJobs()` con `@Query` | `findAll()` (heredado) |
| Insertar Job | `registrarJob()` con `@Query` nativo + `@Modifying` | `save(job)` (heredado) |
| Actualizar Employee | `actualizarEmpleado()` con `@Query` UPDATE + `@Modifying` | `save(employee)` (heredado) |

## Lo que NO cambia nunca
- **Entidades** (`Employee.java`, `Job.java`): idénticas en ambos enfoques.
- **HTML/Thymeleaf**: idéntico, porque solo lee del `Model` (`employees`, `employee`, `jobs`), sin importar cómo se obtuvieron.
- **Rutas del Controller** (`@GetMapping`, `@PostMapping`): las mismas URLs y los mismos parámetros de entrada.

## Lo único que SÍ te puede romper el Controller
`findById()` devuelve `Optional<T>` en vez del objeto directo. Es el único punto donde, si migraras tu código a Query Methods, tendrías que tocar el Controller además del Repository.

---

### Cómo se llamarían tus métodos si el profesor te pide "conviértelo a Query Methods"

```java
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findAll(); // heredado, no hace falta declararlo
    List<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String fn, String ln);
    Optional<Employee> findById(Integer id); // heredado
}

public interface JobRepository extends JpaRepository<Job, String> {
    List<Job> findAll(); // heredado
    // save() heredado para insertar/actualizar
}
```

Nota: al ser todos heredados o basados en convención de nombres, en una migración real ni siquiera necesitarías escribir estas interfaces con contenido — `JpaRepository<Employee, Integer>` vacío ya te da `findAll()`, `findById()` y `save()`. Solo tendrías que escribir la línea de `findByFirstNameContaining...` porque es una búsqueda personalizada.
