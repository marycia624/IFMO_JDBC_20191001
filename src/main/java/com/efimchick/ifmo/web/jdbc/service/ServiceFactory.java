package com.efimchick.ifmo.web.jdbc.service;

import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ServiceFactory {

    DaoFactory allWeHave = new DaoFactory();

    protected List<Employee> getPage (Paging paging, List<Employee> tmp) {
        List<Employee> result = new LinkedList<>();
        int i = paging.itemPerPage*(paging.page - 1);
        while (i != tmp.size() && i <= (paging.itemPerPage*paging.page - 1)) {
            result.add(tmp.get(i));
            i++;
        }
        return  result;
    }

    private List<Employee> getByDepartment(Department department) {
        List<Employee> empByDep = new LinkedList<>();
        for(int i = 0; i < allWeHave.allemployees.size(); i++) {
            if (allWeHave.allemployees.get(i).getDepartment() != null) {
            if (allWeHave.allemployees.get(i).getDepartment().getId().equals(department.getId())) {
                empByDep.add(allWeHave.allemployees.get(i));
            }
            }
        }
        return empByDep;
    }

    private List<Employee> getByManager (Employee manager) {
        List<Employee> result = new LinkedList<>();
        for(int i = 0; i < allWeHave.allemployees.size(); i++) {
            if (allWeHave.allemployees.get(i).getManager() != null) {
            if (allWeHave.allemployees.get(i).getManager().getId().equals(manager.getId())) {
                result.add(allWeHave.allemployees.get(i));
            }
            }
        }
        return result;
    }

    private List<Employee> sortByHire (List<Employee> tmp) {
        tmp.sort(new Comparator<Employee>() {
            @Override
            public int compare(Employee e1, Employee e2) {
                LocalDate first = e1.getHired();
                LocalDate second = e2.getHired();
                return first.compareTo(second);
            }
        });
        return tmp;
    }

    private List<Employee> sortByLastName (List<Employee> tmp) {
        tmp.sort(new Comparator<Employee>() {
            @Override
            public int compare(Employee e1, Employee e2) {
                String first = e1.getFullName().getLastName();
                String second = e2.getFullName().getLastName();
                return first.compareTo(second);
            }
        });
        return tmp;
    }

    private List<Employee> sortBySalary (List<Employee> tmp) {
        tmp.sort(new Comparator<Employee>() {
            @Override
            public int compare(Employee e1, Employee e2) {
                BigDecimal first = e1.getSalary();
                BigDecimal second = e2.getSalary();
                return first.compareTo(second);
            }
        });
        return tmp;
    }

    private List<Employee> sortByDepartmentAndLastName (List<Employee> tmp) {
        tmp.sort(new Comparator<Employee>() {
            @Override
            public int compare(Employee e1, Employee e2) {

                if (e1.getDepartment() == null) {
                    return -1;
                } else if (e2.getDepartment() == null){
                    return 1;
                }

                String depName1 = e1.getDepartment().getName();
                String depName2 = e2.getDepartment().getName();
                int comp = depName1.compareTo(depName2);

                if (comp != 0) {
                    return comp;
                }

                String first = e1.getFullName().getLastName();
                String second = e2.getFullName().getLastName();
                return first.compareTo(second);
            }
        });
        return tmp;
    }


    public EmployeeService employeeService(){
        EmployeeService EmpSer = new EmployeeService() {
            @Override
            public List<Employee> getAllSortByHireDate(Paging paging) {
                List<Employee> tmp = allWeHave.allemployees;
                return  getPage(paging,sortByHire(tmp));
            }

            @Override
            public List<Employee> getAllSortByLastname(Paging paging) {
                List<Employee> tmp = allWeHave.allemployees;
                return  getPage(paging,sortByLastName(tmp));
            }

            @Override
            public List<Employee> getAllSortBySalary(Paging paging) {
                List<Employee> tmp = allWeHave.allemployees;
                return  getPage(paging,sortBySalary(tmp));
            }

            @Override
            public List<Employee> getAllSortByDepartmentNameAndLastname(Paging paging) {
                List<Employee> tmp = allWeHave.allemployees;

                return  getPage(paging,sortByDepartmentAndLastName(tmp));
            }

            @Override
            public List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging) {
                List<Employee> empByDep = getByDepartment(department);
                return  getPage(paging, sortByHire(empByDep));


            }

            @Override
            public List<Employee> getByDepartmentSortBySalary(Department department, Paging paging) {
                List<Employee> empByDep = getByDepartment(department);
                return  getPage(paging, sortBySalary(empByDep));
            }

            @Override
            public List<Employee> getByDepartmentSortByLastname(Department department, Paging paging) {
                List<Employee> empByDep = getByDepartment(department);
                return  getPage(paging, sortByLastName(empByDep));
            }

            @Override
            public List<Employee> getByManagerSortByLastname(Employee manager, Paging paging) {
                List<Employee> empByMan = getByManager(manager);
                return getPage(paging, sortByLastName(empByMan));
            }

            @Override
            public List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging) {
                List<Employee> empByMan = getByManager(manager);
                return getPage(paging, sortByHire(empByMan));
            }

            @Override
            public List<Employee> getByManagerSortBySalary(Employee manager, Paging paging) {
                List<Employee> empByMan = getByManager(manager);
                return getPage(paging, sortBySalary(empByMan));
            }

            @Override
            public Employee getWithDepartmentAndFullManagerChain(Employee employee) {
                for (int i = 0; i < allWeHave.allemployeeswithchain.size(); i++) {
                    if (allWeHave.allemployeeswithchain.get(i).getId().equals(employee.getId())) {
                        return allWeHave.allemployeeswithchain.get(i);
                    }
                }
                return null;
            }

            @Override
            public Employee getTopNthBySalaryByDepartment(int salaryRank, Department department) {
                List<Employee> empByDep = getByDepartment(department);
                Collections.sort(empByDep, new Comparator<Employee>() {
                    public int compare(Employee e1, Employee e2) {
                        BigDecimal first = e1.getSalary();
                        BigDecimal second = e2.getSalary();
                        return second.compareTo(first);
                    }
                });
                return empByDep.get(salaryRank - 1);
            }
        };
        return EmpSer;
    }
}
