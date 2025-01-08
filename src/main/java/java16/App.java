package java16;

import java16.enums.Description;
import java16.enums.Position;
import java16.enums.Profession;
import java16.models.Employee;
import java16.models.Job;
import java16.service.EmployeeService;
import java16.service.JobService;
import java16.service.ServiceImpl.EmployeeServiceImpl;
import java16.service.ServiceImpl.JobServiceImpl;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        EmployeeService employeeService = new EmployeeServiceImpl();
       // employeeService.cleanTable();
        //employeeService.dropTable();
        //System.out.println(employeeService.getEmployeeByPosition("Java"));
       // System.out.println(employeeService.findByEmail("admin@gmail.com"));
        //employeeService.updateEmployee(3L,new Employee("guij","gyuo",12,"HUIjo",2));
        // System.out.println(employeeService.getAllEmployees());
        //employeeService.addEmployee(new Employee("BHuu","HOP",14,"HPijj",2));
        //employeeService.getEmployeeById(1L);
        JobService jobService = new JobServiceImpl();
       // System.out.println(jobService.sortByExperience("asc"));
        //System.out.println(jobService.getJobByEmployeeId(3L));
        // jobService.deleteDescriptionColumn(Description.Instructor);
        //System.out.println(jobService.getJobById(1L));
        //jobService.addJob(new Job(Position.Java, Profession.Backend_developer, Description.Instructor, 12));

    }
}
