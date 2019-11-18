package com.efimchick.ifmo.web.jdbc.service;

import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;

import java.util.LinkedList;
import java.util.List;

public class ServiceFactory {

    private DaoFactory allWeHave = new DaoFactory();

    private List<Employee> getPage (Paging paging, List<Employee> tmp) {
        List<Employee> result = new LinkedList<>();
        int i = paging.itemPerPage*(paging.page - 1);
        while (i != tmp.size() && i <= (paging.itemPerPage*paging.page - 1)) {
            result.add(tmp.get(i));
            i++;
        }
        return  result;
    }


    public EmployeeService employeeService(){
        EmployeeService EmpSer = new EmployeeService() {
            @Override
            public List<Employee> getAllSortByHireDate(Paging paging) {
                List<Employee> tmp = allWeHave.getEmployees(1, "SELECT * FROM EMPLOYEE ORDER BY HIREDATE");
                return  getPage(paging,tmp);
            }

            @Override
            public List<Employee> getAllSortByLastname(Paging paging) {
                List<Employee> tmp = allWeHave.getEmployees(1, "SELECT * FROM EMPLOYEE ORDER BY LASTNAME");
                return  getPage(paging,tmp);
            }

            @Override
            public List<Employee> getAllSortBySalary(Paging paging) {
                List<Employee> tmp = allWeHave.getEmployees(1, "SELECT * FROM EMPLOYEE ORDER BY SALARY");
                return  getPage(paging,tmp);
            }

            @Override
            public List<Employee> getAllSortByDepartmentNameAndLastname(Paging paging) {
                List<Employee> tmp = allWeHave.getEmployees(1, "SELECT * FROM EMPLOYEE ORDER BY DEPARTMENT, LASTNAME");

                return  getPage(paging,tmp);
            }

            @Override
            public List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging) {
                List<Employee> empByDep = allWeHave.getEmployees(1, "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + department.getId() + " ORDER BY HIREDATE");
                return  getPage(paging, empByDep);


            }

            @Override
            public List<Employee> getByDepartmentSortBySalary(Department department, Paging paging) {
                List<Employee> empByDep = allWeHave.getEmployees(1, "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + department.getId() + " ORDER BY SALARY");
                return  getPage(paging, empByDep);
            }

            @Override
            public List<Employee> getByDepartmentSortByLastname(Department department, Paging paging) {
                List<Employee> empByDep = allWeHave.getEmployees(1, "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + department.getId() + " ORDER BY LASTNAME");
                return  getPage(paging, empByDep);
            }

            @Override
            public List<Employee> getByManagerSortByLastname(Employee manager, Paging paging) {
                List<Employee> empByMan = allWeHave.getEmployees(1, "SELECT * FROM EMPLOYEE WHERE MANAGER = " + manager.getId() + " ORDER BY LASTNAME");
                return getPage(paging, empByMan);
            }

            @Override
            public List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging) {
                List<Employee> empByMan = allWeHave.getEmployees(1, "SELECT * FROM EMPLOYEE WHERE MANAGER = " + manager.getId() + " ORDER BY HIREDATE");
                return getPage(paging, empByMan);
            }

            @Override
            public List<Employee> getByManagerSortBySalary(Employee manager, Paging paging) {
                List<Employee> empByMan = allWeHave.getEmployees(1, "SELECT * FROM EMPLOYEE WHERE MANAGER = " + manager.getId() + " ORDER BY SALARY");
                return getPage(paging, empByMan);
            }

            @Override
            public Employee getWithDepartmentAndFullManagerChain(Employee employee) {
                List<Employee> employees = allWeHave.getEmployees(3, "SELECT * FROM EMPLOYEE");
                for (int i = 0; i < employees.size(); i++) {
                    if (employees.get(i).getId().equals(employee.getId())) {
                        return employees.get(i);
                    }
                }
                return null;
            }

            @Override
            public Employee getTopNthBySalaryByDepartment(int salaryRank, Department department) {
                List<Employee> empByDep = allWeHave.getEmployees(1, "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + department.getId() + " ORDER BY SALARY DESC");
                return empByDep.get(salaryRank - 1);
            }
        };
        return EmpSer;
    }
}
