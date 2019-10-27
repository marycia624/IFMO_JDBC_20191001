package com.efimchick.ifmo.web.jdbc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {
        SetMapper<Set<Employee>> result = new SetMapper<Set<Employee>>() {
            @Override
            public Set<Employee> mapSet(ResultSet rs) {
                Set<Employee> SetOfEmployees = new HashSet<Employee>();
                try {
                    while (rs.next()) {
                        Employee emp = mapRow(rs);
                        SetOfEmployees.add(emp);
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                return SetOfEmployees;
            }
        };
        return result;
    }

    private Employee mapRow(ResultSet rs) {
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
        if (rs.getString("manager") != null) {
            int managerId = Integer.valueOf(rs.getString("manager"));
            int now = rs.getRow();
            rs.beforeFirst();
            while (rs.next() && manager == null) {
                if (rs.getInt("ID") == managerId) {
                    manager = mapRow(rs);
                }
            }
            rs.absolute(now);
        }
        Employee emp = new Employee(BigInteger.valueOf(thisId), fullName, pos, date, salary, manager);
        return emp;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

   /* private Employee getManager (ResultSet rs) throws SQLException {
        Employee manager = null;
        if (rs.getString("manager") != null) {
            int managerId = Integer.valueOf(rs.getString("manager"));
            int now = rs.getRow();
            rs.beforeFirst();
            while (rs.next() && manager == null) {
                if (rs.getInt("ID") == managerId) {
                    manager = mapRow(rs);
                }
            }
            rs.absolute(now);

        }
        return manager;
    } */
}

