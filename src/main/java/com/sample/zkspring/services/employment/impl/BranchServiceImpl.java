package com.sample.zkspring.services.employment.impl;

import com.sample.zkspring.entity.employment.Branch;
import com.sample.zkspring.services.employment.BranchService;
import com.sample.zkspring.services.employment.dao.BranchDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("branchService")
public class BranchServiceImpl implements BranchService {
    @Autowired
    BranchDao branchDao;

    @Override
    public List<Branch> findAll() {
        return branchDao.queryAll();
    }

    @Override
    public Branch find(final Long id) {
        return (Branch)branchDao.find(id, Branch.class);
    }

    @Override
    public Branch create(final Branch branch) {
        return (Branch) branchDao.create(branch);
    }

    @Override
    public Branch update(Branch branch) {
        return (Branch) branchDao.update(branch);
    }

    @Override
    public void delete(Branch branch) {
        branchDao.delete(branch);
    }
}
