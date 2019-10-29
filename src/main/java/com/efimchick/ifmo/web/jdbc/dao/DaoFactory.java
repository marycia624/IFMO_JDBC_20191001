package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DaoFactory {
    private List<Employee> allemployees = getEmployees();
    private List<Department> alldepartments = getDepartment();

    private List<Employee> getEmployees() {
        try {
            ResultSet rs = createConnection().createStatement().executeQuery("SELECT * FROM EMPLOYEE");
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

    private List<Department> getDepartment() {
        try {
            ResultSet rs = createConnection().createStatement().executeQuery("SELECT * FROM DEPARTMENT");
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
                List<Employee> result = new LinkedList<>();
                for(int i = 0; i < allemployees.size(); i++) {
                    if (allemployees.get(i).getDepartmentId().equals(department.getId())) {
                        result.add(allemployees.get(i));
                    }
                }
                return result;
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                List<Employee> result = new LinkedList<>();
                for(int i = 0; i < allemployees.size(); i++) {
                    if (allemployees.get(i).getManagerId().equals(employee.getId())) {
                        result.add(allemployees.get(i));
                    }
                }
                return result;
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                Optional<Employee> result = Optional.empty();
                for (int i = 0; i < allemployees.size(); i++) {
                    if (allemployees.get(i).getId().equals(Id)) {
                        result = Optional.of(allemployees.get(i));
                        break;
                    }
                }
                return result;
            }

            @Override
            public List<Employee> getAll() {
                return allemployees;
            }

            @Override
            public Employee save(Employee employee) {
                allemployees.add(employee);
                return employee;
            }

            @Override
            public void delete(Employee employee) {
                allemployees.remove(employee);
            }
        };
        return empDao;
    }

    public DepartmentDao departmentDAO() {
        DepartmentDao depDao = new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id) {
                Optional<Department> res = Optional.empty();
                for (int i = 0; i < alldepartments.size(); i++) {
                    if (alldepartments.get(i).getId().equals(Id)) {
                        res = Optional.of(alldepartments.get(i));
                    }
                }
                return res;
            }

            @Override
            public List<Department> getAll() {
                return alldepartments;
            }

            @Override
            public Department save(Department department) {
                for (int i = 0; i < alldepartments.size(); i++) {
                    if (alldepartments.get(i).getId().equals(department.getId())) {
                        alldepartments.remove(alldepartments.get(i));
                    }
                }
                alldepartments.add(department);
                return department;
            }

            @Override
            public void delete(Department department) {
                alldepartments.remove(department);
            }
        };
        return depDao;
    }
}
