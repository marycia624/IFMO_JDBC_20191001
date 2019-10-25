package com.efimchick.ifmo.web.jdbc;

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

public class RowMapperFactory {

    public RowMapper<Employee> employeeRowMapper() {
        RowMapper<Employee> RM = new RowMapper<Employee>() {
            @Override
            public Employee mapRow(ResultSet rs) {
                try {
                    int thisId = rs.getInt("id");
                    String fn = rs.getString("firstname");
                    String ln = rs.getString("lastname");
                    String mn = rs.getString("middlename");
                    FullName fullName = new FullName(fn, ln, mn);
                    Position pos = Position.valueOf(rs.getString("position"));
                    LocalDate date = LocalDate.parse(String.valueOf(rs.getDate("hiredate")));
                    BigDecimal salary = rs.getBigDecimal("salary");
                    Employee emp = new Employee(BigInteger.valueOf(thisId), fullName, pos, date, salary);
                    return emp;

                } catch (SQLException e) {
                    //Employee emp = new Employee(null, null, null, null, null);
                    return null;
                }
            }
        };

    return RM;
    }
}
