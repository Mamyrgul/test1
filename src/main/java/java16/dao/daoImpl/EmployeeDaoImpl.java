package java16.dao.daoImpl;
import java16.config.DatabaseConnection;
import java16.dao.EmployeeDao;
import java16.enums.Description;
import java16.enums.Position;
import java16.enums.Profession;
import java16.models.Employee;
import java16.models.Job;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDaoImpl implements EmployeeDao {
    private final Connection connection = DatabaseConnection.getConnection();
    @Override
    public void createEmployee() {
        String sql = """
                create table if not exists employee(
                id serial primary key,
                first_name varchar(20),
                last_name varchar(20),
                age int,
                email varchar(20),
                job_id int references jobs(id)
                )
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.execute();
            System.out.println("Table 'employee' created successfully.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addEmployee(Employee employee) {
     String sql = """
             insert into employee( first_name, last_name, age, email, job_id)
             values(?,?,?,?,?)
             """;
     try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
         preparedStatement.setString(1, employee.getFirstName());
         preparedStatement.setString(2, employee.getLastName());
         preparedStatement.setInt(3, employee.getAge());
         preparedStatement.setString(4, employee.getEmail());
         preparedStatement.setLong(5, employee.getJobId());
         preparedStatement.execute();
         System.out.println("Table 'employee' created successfully.");
     } catch (Exception e) {
         throw new RuntimeException(e);
     }
    }

    @Override
    public void dropTable() {
   String sql = "drop table if exists employee";
   try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
       preparedStatement.executeUpdate();
       System.out.println("Table 'employee' dropped successfully.");
   } catch (Exception e) {
       throw new RuntimeException(e);
   }
    }

    @Override
    public void cleanTable() {
    String sql = "delete table if exists employee";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
        preparedStatement.execute();
        System.out.println("Table 'employee' cleaned successfully.");
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    }

    @Override
    public void updateEmployee(Long id, Employee employee) {
        String sql = """
        update employee set first_name = ?, last_name = ?, age = ?, email = ?, job_id = ? where id = ?
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getLastName());
            preparedStatement.setInt(3, employee.getAge());
            preparedStatement.setString(4, employee.getEmail());

            if (employee.getJobId() > 0) {
                preparedStatement.setInt(5, employee.getJobId());
            } else {
                preparedStatement.setNull(5, java.sql.Types.INTEGER);
            }
            preparedStatement.setLong(6, id);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Employee with ID " + id + " updated successfully.");
            } else {
                System.out.println("No employee found with ID " + id + ".");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating employee with ID " + id, e);
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        String sql = "select * from employee";
        List<Employee> employees = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Employee employee = new Employee();
                employee.setFirstName(resultSet.getString("first_name"));
                employee.setLastName(resultSet.getString("last_name"));
                employee.setAge(resultSet.getInt("age"));
                employee.setEmail(resultSet.getString("email"));
                employees.add(employee);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return employees;
    }

    @Override
    public Employee findByEmail(String email) {
        String sql = "select * from employee where email = ?";
        try (PreparedStatement preparedStatement= connection.prepareStatement(sql)){
            preparedStatement.setString(1, email);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Employee employee = new Employee();
                employee.setFirstName(resultSet.getString("first_name"));
                employee.setLastName(resultSet.getString("last_name"));
                employee.setAge(resultSet.getInt("age"));
                employee.setEmail(resultSet.getString("email"));
                return employee;
            }else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Map<Employee, Job> getEmployeeById(Long employeeId) {
        String sql = """
        select first_name, last_name, age, email, id as job_id, position, profession, description, experience
        from employee  left join jobs   on employee.job_id = jobs.id where  employee.id = ?;
    """;
        Map<Employee, Job> employeeMap = new HashMap<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Employee employee = new Employee();
                employee.setFirstName(resultSet.getString("first_name"));
                employee.setLastName(resultSet.getString("last_name"));
                employee.setAge(resultSet.getInt("age"));
                employee.setEmail(resultSet.getString("email"));

                Job job = new Job();
                job.setId(resultSet.getLong("job_id"));
                job.setPosition(Position.valueOf(resultSet.getString("position")));
                job.setProfession(Profession.valueOf(resultSet.getString("profession")));
                job.setDescription(Description.valueOf(resultSet.getString("description")));
                job.setExperience(resultSet.getInt("experience"));

                employeeMap.put(employee, job);
            } else {
                throw new RuntimeException("Employee with ID " + employeeId + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid ENUM value in database", e);
        }
        return employeeMap;
    }


    @Override
    public List<Employee> getEmployeeByPosition(String position) {
        String sql = "select * from employee join jobs on employee.job_id = jobs.id where position = ?";
        List<Employee> employees = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
         preparedStatement.setString(1, position);
         ResultSet resultSet = preparedStatement.executeQuery();
         while (resultSet.next()) {
             Employee employee = new Employee();
             employee.setFirstName(resultSet.getString("first_name"));
             employee.setLastName(resultSet.getString("last_name"));
             employee.setAge(resultSet.getInt("age"));
             employee.setEmail(resultSet.getString("email"));
             employee.setJobId(resultSet.getInt("job_id"));
             employees.add(employee);
             return employees;
         }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
