package com.sample.ZKSpringJPA.services.employment.impl;

import com.sample.ZKSpringJPA.entity.authentication.User;
import com.sample.ZKSpringJPA.entity.employment.Employee;
import com.sample.ZKSpringJPA.services.employment.EmployeeService;
import com.sample.ZKSpringJPA.services.employment.dao.EmployeeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    EmployeeDao employeeDao;

    @Override
    public List<Employee> findAll() {
        return employeeDao.queryAll();
    }

    @Override
    public Employee find(Long id) {
        return (Employee) employeeDao.find(id, Employee.class);
    }

    @Override
    public Employee create(Employee employee) {
        return (Employee) employeeDao.create(employee);
    }

    @Override
    public Employee update(Employee employee) {
        return (Employee) employeeDao.update(employee);
    }

    @Override
    public void delete(Employee employee) {
        employeeDao.delete(employee);
    }

    @Override
    public Employee findByUser(User user){
        return employeeDao.findByUser(user);
    }

    @Override
    public int count() {
        return employeeDao.count(Employee.class);
    }

    @Override
    public List<Employee> findPaging(final int offset, final int limit) {
        return employeeDao.findPaging(offset, limit, Employee.class);
    }

    @Override
    public int count(String filter, String filterBy) {
        return employeeDao.count(Employee.class, filter, filterBy);
    }

    @Override
    public List<Employee> findPaging(int offset, int limit, String filter, String filterBy) {
        return employeeDao.findPaging(offset, limit, Employee.class, filter, filterBy);
    }

    @Override
    public int count(final String filter, final String filterBy, final HashMap<String, Object> filters) {
        return employeeDao.count(Employee.class, filter, filterBy, filters);
    }

    @Override
    public List<Employee> findPaging(final int offset, final int limit, final String filter, final String filterBy, final HashMap<String, Object> filters) {
        return employeeDao.findPaging(offset, limit, Employee.class, filter, filterBy, filters);
    }
}