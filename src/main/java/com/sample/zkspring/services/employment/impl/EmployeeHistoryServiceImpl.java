package com.sample.zkspring.services.employment.impl;

import com.sample.zkspring.entity.employment.EmploymentHistory;
import com.sample.zkspring.services.employment.EmployeeHistoryService;
import com.sample.zkspring.services.employment.dao.EmploymentHistoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("employeeHistoryService")
public class EmployeeHistoryServiceImpl implements EmployeeHistoryService {
    @Autowired
    EmploymentHistoryDao dao;
    @Override
    public EmploymentHistory find(Long id) {
        return (EmploymentHistory) dao.find(id, EmploymentHistory.class);
    }

    @Override
    public EmploymentHistory create(EmploymentHistory employeeHistory) {
        return (EmploymentHistory) dao.create(employeeHistory);
    }

    @Override
    public EmploymentHistory update(EmploymentHistory employeeHistory) {
        return (EmploymentHistory) dao.update(employeeHistory);
    }

    @Override
    public void delete(EmploymentHistory employeeHistory) {
        dao.delete(employeeHistory);
    }
}
