package com.jsict.biz.service.impl;

import com.jsict.biz.service.CodeService;
import com.jsict.framework.core.security.UdpShiroFilterFactoryBean;
import com.jsict.framework.core.service.impl.GeneriServiceImpl;
import com.jsict.biz.model.Department;
import com.jsict.biz.service.DepartmentService;

import com.jsict.framework.utils.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Auto-Generated by UDP Generator
 */
@Service
public class DepartmentServiceImpl extends GeneriServiceImpl<Department, String> implements DepartmentService {

    private static final String SHIRO_FILTER = "shiroFilter";

    @Autowired
    private CodeService codeService;
    @Transactional(readOnly = true)
    @Override
    public List<Department> findAll() {
        Map<String, Object> params = new HashMap<>();
        params.put("delFlag", 0);
        return genericDao.findByProperty(params);
    }

    @Transactional(readOnly = true)
    @Override
    public Department get(String id){
        Department dept = super.get(id);
        if(StringUtils.isNotBlank(dept.getParentDeptId())){
            dept.setParentDept(get(dept.getParentDeptId()));
        }
        return dept;
    }

    /**
     * 自动生成部门编码
     *
     * @param department
     * @return
     */
    @Transactional
    @Override
    public Department save(Department department){
        String deptCode;
        if(StringUtils.isBlank(department.getParentDeptId())){
            deptCode = codeService.generateCode("D");
        }else{
            Department parentDept = get(department.getParentDeptId());
            deptCode = codeService.generateCode(parentDept.getDeptCode());
        }
        department.setDeptCode(deptCode);
        Department d = super.save(department);
        UdpShiroFilterFactoryBean.SpringShiroFilter shiroFilter =
                (UdpShiroFilterFactoryBean.SpringShiroFilter) SpringContextUtil.getBean(SHIRO_FILTER);
        shiroFilter.updateFilterChainDefinitionMap();
        return d;
    }

    /**
     * 保存部门前先判断层级是否变化，如果变化了需要把子层部门的层级也变了
     *
     * @param department
     * @return
     */
    @Transactional
    @Override
    public Department update(Department department){
        if(StringUtils.isBlank(department.getParentDeptId()) || "root".equals(department.getParentDeptId()))
            department.setParentDeptId(null);
        Department oldDepartment = get(department.getId());
        boolean isChanged = true;
        if((StringUtils.isBlank(oldDepartment.getParentDeptId()) && StringUtils.isBlank(department.getParentDeptId()))
                ||(StringUtils.isNotBlank(oldDepartment.getParentDeptId()) && oldDepartment.getParentDeptId().equals(department.getParentDeptId())))
            isChanged = false;
        if(isChanged){
            String parentDeptId = department.getParentDeptId();
            String deptCode;
            if(StringUtils.isNotBlank(parentDeptId)){
                Department parentDept = get(parentDeptId);
                deptCode = codeService.generateCode(parentDept.getDeptCode());
            }else{
                deptCode = codeService.generateCode("D");
            }

            department.setDeptCode(deptCode);
            String deptId = department.getId();
            updateDeptCode(deptId, deptCode);
        }else
            department.setDeptCode(oldDepartment.getDeptCode());
        Department d = super.update(department);
        UdpShiroFilterFactoryBean.SpringShiroFilter shiroFilter =
                (UdpShiroFilterFactoryBean.SpringShiroFilter) SpringContextUtil.getBean(SHIRO_FILTER);
        shiroFilter.updateFilterChainDefinitionMap();
        return d;
    }

    /**
     * 更新部门编码
     *
     * @param parentDeptId
     * @param parentDeptCode
     */
    private void updateDeptCode(String parentDeptId, String parentDeptCode){
        Map<String, Object> params = new HashMap<>();
        params.put("parentDeptId", parentDeptId);
        params.put("delFlag", 0);
        List<Department> childDepts = genericDao.find("selectByPage", params);
        if(childDepts.isEmpty())
            return;
        for(Department childDept: childDepts){
            String newDeptCode = codeService.generateCode(parentDeptCode);
            childDept.setDeptCode(newDeptCode);
            update(childDept);
            updateDeptCode(childDept.getId(), newDeptCode);
        }
    }

    @Transactional
    @Override
    public boolean delete(Department dept){
        boolean b = super.delete(dept);
        UdpShiroFilterFactoryBean.SpringShiroFilter shiroFilter =
                (UdpShiroFilterFactoryBean.SpringShiroFilter) SpringContextUtil.getBean(SHIRO_FILTER);
        shiroFilter.updateFilterChainDefinitionMap();
        return b;
    }

    @Transactional
    @Override
    public void enable(String id) {
        troggleEnable(id, 1);
    }

    @Transactional
    @Override
    public void disable(String id) {
        troggleEnable(id, 0);
    }

    private void troggleEnable(String id, Integer enable){
        Department department = get(id);
        department.setEnable(enable);
        genericDao.update(department);
    }

    @Transactional
    @Override
    public List<Department> findByParentDeptId(String parentDeptId){
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(parentDeptId))
            params.put("parentDeptId", parentDeptId);
        params.put("delFlag", 0);
        return genericDao.find("queryAllProperties", params);
    }
}