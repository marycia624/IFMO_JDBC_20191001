package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DaoFactory {

    private List<Employee> getEmployees(String request) {
        try {
            ResultSet rs = createConnection().createStatement().executeQuery(request);
            List<Employee> allEmployees = new LinkedList<>();
            while (rs.next()) {
                Employee emp = employeeMapRow(rs);
                allEmployees.add(emp);
            }
            return allEmployees;
        } catch (SQLException e) {
            return  null;
        }
    }

    private List<Department> getDepartment(String request) {
        try {
            ResultSet rs = createConnection().createStatement().executeQuery(request);
            List<Department> allDepartments = new LinkedList<>();
            while (rs.next()) {
                Department dep = depatmentMapRow(rs);
                allDepartments.add(dep);
            }
            return allDepartments;
        } catch (SQLException e) {
            return  null;
        }
    }

    private Connection createConnection() throws SQLException{
        ConnectionSource connectionSource = ConnectionSource.instance();
        Connection con = connectionSource.createConnection();
        return con;
    }
    private Employee employeeMapRow(ResultSet rs) {
        try {
            int thisId = rs.getInt("id");
            String fn = rs.getString("firstname");
            String ln = rs.getString("lastname");
            String mn = rs.getString("middlename");
            FullName fullName = new FullName(fn, ln, mn);
            Position pos = Position.valueOf(rs.getString("position"));
            LocalDate date = LocalDate.parse(String.valueOf(rs.getDate("hiredate")));
            BigDecimal salary = rs.getBigDecimal("salary");
            BigInteger manager = getManager(rs);
            BigInteger department = getDepartment(rs);

            return new Employee(BigInteger.valueOf(thisId),
                                fullName,
                                pos,
                                date,
                                salary,
                                manager,
                                department);
        }

        catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    private BigInteger getManager(ResultSet rs) throws SQLException {
        Object manager = rs.getObject("manager");
        if (manager == null) {
            return BigInteger.valueOf(0);
        } else {
            return BigInteger.valueOf((Integer) manager);
        }
    }

    private BigInteger getDepartment (ResultSet rs) throws SQLException{
        Object department = rs.getObject("department");
        if (department == null) {
            return BigInteger.valueOf(0);
        } else {
            return BigInteger.valueOf((Integer) department);
        }
    }

    private Department depatmentMapRow(ResultSet rs) {
        try {
            BigInteger id = BigInteger.valueOf(rs.getInt("id"));
            String name = rs.getString("name");
            String location = rs.getString("location");
            return new Department(id,name,location);

        } catch (SQLException e) {
            return null;
        }

    }


    public EmployeeDao employeeDAO() {
        EmployeeDao empDao = new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department) {
                return getEmployees("SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + department.getId());
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                return getEmployees("SELECT * FROM EMPLOYEE WHERE MANAGER = " + employee.getId());
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                try {
                    return Optional.of(getEmployees("SELECT * FROM EMPLOYEE WHERE ID = " + Id).get(0));
                } catch (Exception e) {
                    return Optional.empty();
                }
            }

            @Override
            public List<Employee> getAll() {
                return getEmployees("SELECT * FROM EMPLOYEE");
            }

            @Override
            public Employee save(Employee employee) {
                try {
                    String request = "INSERT INTO EMPLOYEE " +
                            "VALUES (" +
                            employee.getId() + ", '" +
                            employee.getFullName().getFirstName() + "', '" +
                            employee.getFullName().getLastName() + "', '" +
                            employee.getFullName().getMiddleName() + "', '" +
                            employee.getPosition() + "', " +
                            employee.getManagerId() + ", '" +
                            Date.valueOf(employee.getHired()) + "', " +
                            employee.getSalary() + ", " +
                            employee.getDepartmentId() + ")";
                    createConnection()
                            .createStatement()
                            .executeUpdate(request);
                } catch (SQLException e) {
                    System.out.println(e);
                }
                return employee;
            }

            @Override
            public void delete(Employee employee) {
                String request = "DELETE FROM EMPLOYEE WHERE ID = " + employee.getId();
                try {
                    createConnection().createStatement()
                            .executeUpdate(request);
                } catch (SQLException e) {
                    System.out.println(e);
                }
            }
        };
        return empDao;
    }

    public DepartmentDao departmentDAO() {
        DepartmentDao depDao = new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id) {
                try {
                    return Optional.of(getDepartment("SELECT * FROM DEPARTMENT WHERE ID = " + Id).get(0));
                } catch (Exception e) {
                    return Optional.empty();
                }

            }

            @Override
            public List<Department> getAll() {
                return getDepartment("SELECT * FROM DEPARTMENT");
            }

            @Override
            public Department save(Department department) {
                try {
                    delete(department);
                    String request = "INSERT INTO DEPARTMENT " +
                            "VALUES (" +
                            department.getId() + ", '" +
                            department.getName() + "', '" +
                           department.getLocation() + "')";
                    createConnection()
                            .createStatement()
                            .executeUpdate(request);
                } catch (SQLException e) {
                    System.out.println(e);
                }
                return department;
            }

            @Override
            public void delete(Department department) {
                String request = "DELETE FROM DEPARTMENT WHERE ID = " + department.getId();
                try {
                    createConnection().createStatement()
                            .executeUpdate(request);
                } catch (SQLException e) {
                    System.out.println(e);
                }
            }
        };
        return depDao;
    }
}
