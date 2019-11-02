package com.efimchick.ifmo.web.jdbc.service;

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
import java.sql.Statement;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class DaoFactory {
    protected List<Department> alldepartments = getDepartment();
    protected List<Employee> allemployees = getEmployees(false);
    protected List<Employee> allemployeeswithchain = getEmployees(true);

    private List<Employee> getEmployees(boolean chain) {
        try {
            final ConnectionSource connectionSource = ConnectionSource.instance();
            final Connection conn = connectionSource.createConnection();
            final Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            final ResultSet rs = statement.executeQuery("SELECT * FROM EMPLOYEE");
            List<Employee> allEmployees = new LinkedList<>();
            while (rs.next()) {
                Employee emp = employeeMapRow(rs, chain, true);
                allEmployees.add(emp);
            }
            return allEmployees;
        } catch (SQLException e) {
            return  null;
        }
    }

    private List<Department> getDepartment() {
        try {
            final ConnectionSource connectionSource = ConnectionSource.instance();
            final Connection conn = connectionSource.createConnection();
            final Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            final ResultSet rs = statement.executeQuery("SELECT * FROM DEPARTMENT");
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


    private Employee employeeMapRow(ResultSet rs, boolean chain, boolean first) {
        try {
            int thisId = rs.getInt("id");
            String fn = rs.getString("firstname");
            String ln = rs.getString("lastname");
            String mn = rs.getString("middlename");
            FullName fullName = new FullName(fn, ln, mn);
            Position pos = Position.valueOf(rs.getString("position"));
            LocalDate date = LocalDate.parse(String.valueOf(rs.getDate("hiredate")));
            BigDecimal salary = rs.getBigDecimal("salary");
            Employee manager = null;
            if (first || chain) {
                if (rs.getString("manager") != null) {
                    int managerId = Integer.valueOf(rs.getString("manager"));
                    int now = rs.getRow();
                    rs.beforeFirst();
                    while (rs.next() && manager == null) {
                        if (rs.getInt("ID") == managerId) {
                            manager = employeeMapRow(rs, chain, false);
                        }
                    }
                    rs.absolute(now);
                }
            }
            Department department = null;
            if (rs.getString("department") != null) {
                department = getDepartmentById(BigInteger.valueOf(rs.getInt("department")));
            }

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

    private Department getDepartmentById (BigInteger Id){
        Department dep;
        for (int i = 0; i < alldepartments.size(); i++) {
            if (alldepartments.get(i).getId().equals(Id)) {
                dep = alldepartments.get(i);
                return dep;
            }
        }
        return null;
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
}
