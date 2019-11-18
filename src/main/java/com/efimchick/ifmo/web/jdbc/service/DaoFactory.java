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

    public List<Employee> getEmployees(int chain, String request) {
        try {
            final ConnectionSource connectionSource = ConnectionSource.instance();
            final Connection conn = connectionSource.createConnection();
            final Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            final ResultSet rs = statement.executeQuery(request);
            List<Employee> allEmployees = new LinkedList<>();
            while (rs.next()) {
                Employee emp = employeeMapRow(rs, chain);
                allEmployees.add(emp);
            }
            return allEmployees;
        } catch (SQLException e) {
            return  null;
        }
    }

    private Department getDepartment(int id) {
        try {
            final ConnectionSource connectionSource = ConnectionSource.instance();
            final Connection conn = connectionSource.createConnection();
            final Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            final ResultSet rs = statement.executeQuery("SELECT * FROM DEPARTMENT WHERE ID = " + id);
            rs.next();
            return departmentMapRow(rs);
        } catch (SQLException e) {
            return  null;
        }
    }

    private Employee getManager (int id, int chain) {
        return getEmployees((chain == 3) ? 3 : 2, "SELECT * FROM EMPLOYEE WHERE ID = " + id).get(0);
    }


    private Employee employeeMapRow(ResultSet rs, int chain) {
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
            if (chain != 2) {
                if (rs.getString("manager") != null) {
                    int managerId = Integer.valueOf(rs.getString("manager"));
                    manager = getManager(managerId, chain);
                }
            }
            Department department = null;
            if (rs.getString("department") != null) {
                department = getDepartment(rs.getInt("department"));
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


    private Department departmentMapRow(ResultSet rs) {
        try {
            BigInteger id = BigInteger.valueOf(rs.getInt("id"));
            String name = rs.getString("name");
            String location = rs.getString("location");
            return new Department(id, name,location);

        } catch (SQLException e) {
            return null;
        }

    }
}
