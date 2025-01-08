package java16.dao.daoImpl;

import java16.config.DatabaseConnection;
import java16.dao.JobDao;
import java16.enums.Description;
import java16.enums.Position;
import java16.enums.Profession;
import java16.models.Job;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDaoImpl implements JobDao {
    private final Connection connection = DatabaseConnection.getConnection();

    @Override
    public void createJobTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS jobs (
                id SERIAL PRIMARY KEY,
                position VARCHAR(50) NOT NULL,
                profession VARCHAR(50) NOT NULL,
                description VARCHAR(50) NOT NULL,
                experience int NOT NULL
                )
                """;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addJob(Job job) {
        String sql = """
                insert into jobs (position,profession,description,experience)
                values (?,?,?,?)
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, job.getPosition().name());
            preparedStatement.setString(2, job.getProfession().name());
            preparedStatement.setString(3, job.getDescription().name());
            preparedStatement.setInt(4, job.getExperience());
            int result = preparedStatement.executeUpdate();
            System.out.println(result);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Job getJobById(Long jobId) {
        String sql = """
                select * from jobs where id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, jobId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Job job = new Job();
                job.setPosition(Position.valueOf(resultSet.getString("position")));
                job.setProfession(Profession.valueOf(resultSet.getString("profession")));
                job.setDescription(Description.valueOf(resultSet.getString("description")));
                job.setExperience(resultSet.getInt("experience"));

                return job;
            } else {
                throw new RuntimeException("Job with ID " + jobId + " not found.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Job> sortByExperience(String ascOrDesc) {
        if (!ascOrDesc.equalsIgnoreCase("asc") && !ascOrDesc.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("Invalid sorting order: " + ascOrDesc);
        }
        String sql = "select * from jobs order by experience " + ascOrDesc;
        List<Job> jobs = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Job job = new Job();
                try {
                    job.setPosition(Position.valueOf(resultSet.getString("position")));
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid position value in database: " + resultSet.getString("position"));
                }
                try {
                    job.setProfession(Profession.valueOf(resultSet.getString("profession")));
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid profession value in database: " + resultSet.getString("profession"));
                }
                try {
                    job.setDescription(Description.valueOf(resultSet.getString("description")));
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid description value in database: " + resultSet.getString("description"));
                }
                job.setExperience(resultSet.getInt("experience"));
                jobs.add(job);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error sorting jobs by experience", e);
        }

        return jobs;
    }


    @Override
    public Job getJobByEmployeeId(Long employeeId) {
        String sql = """
                select * from jobs join employee on employee.job_id = jobs.id where employee.id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Job job = new Job();
                job.setPosition(Position.valueOf(resultSet.getString("position")));
                job.setProfession(Profession.valueOf(resultSet.getString("profession")));
                job.setDescription(Description.valueOf(resultSet.getString("description")));
                job.setExperience(resultSet.getInt("experience"));
                return job;
            } else {
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void deleteDescriptionColumn(Description description) {
        String deleteEmployeesSql = "DELETE FROM employee WHERE job_id IN (SELECT id FROM jobs WHERE description = ?)";
        String deleteJobsSql = "DELETE FROM jobs WHERE description = ?";

        try (
                PreparedStatement deleteEmployeesStmt = connection.prepareStatement(deleteEmployeesSql);
                PreparedStatement deleteJobsStmt = connection.prepareStatement(deleteJobsSql)
        ) {
            deleteEmployeesStmt.setString(1, description.name());
            deleteJobsStmt.setString(1, description.name());

            int employeesDeleted = deleteEmployeesStmt.executeUpdate();
            System.out.println("Deleted " + employeesDeleted + " dependent employees.");

            int jobsDeleted = deleteJobsStmt.executeUpdate();
            if (jobsDeleted > 0) {
                System.out.println("Rows with description '" + description.name() + "' deleted successfully.");
            } else {
                System.out.println("No rows found with description '" + description.name() + "'.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error deleting rows with description: " + description.name(), e);
        }
    }
}
