package com.jsict.biz.controller;

import com.jsict.biz.dao.UserDao;
import com.jsict.biz.model.Department;
import com.jsict.biz.model.Role;
import com.jsict.biz.service.UserService;
import com.jsict.framework.core.controller.AbstractGenericController;
import com.jsict.biz.model.User;

import com.jsict.framework.core.controller.CSRFTokenManager;
import com.jsict.framework.core.controller.Response;
import com.jsict.framework.core.controller.RestControllerException;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * Auto-Generated by UDP Generator
 */
@Controller
@RequestMapping("/user")
public class UserController extends
    AbstractGenericController<User, String,
            User> {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDao userDao;

    /**
     * 加入对关联部门的校验
     *
     * @param entity 实体对象
     * @param paramToken CSRF Token
     * @param request HttpServletRequest
     * @return
     */
    @Override
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response save(@ModelAttribute User entity, @RequestParam(value = CSRFTokenManager.CSRF_PARAM_NAME) String paramToken, HttpServletRequest request){

        String deptId = entity.getDeptId();
        List<Department> depts = entity.getDeptList();
        for(Department dept: depts){
            if(dept.getId().equals(deptId)){
                return new Response(-1,"关联部门包含用户所在部门");
            }
        }
        return super.save(entity, paramToken, request);
    }

    /**
     * 加入对关联部门的校验
     *
     * @param entity 实体对象
     * @param paramToken CSRF Token
     * @param request HttpServletRequest
     * @return
     */
    @Override
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response update(@ModelAttribute User entity, @RequestParam(value = CSRFTokenManager.CSRF_PARAM_NAME) String paramToken, HttpServletRequest request){

        String deptId = entity.getDeptId();
        List<Department> depts = entity.getDeptList();
        for(Department dept: depts){
            if(dept.getId().equals(deptId)){
                return new Response(-1,"关联部门包含用户所在部门");
            }
        }
        return super.update(entity, paramToken, request);
    }

    @ResponseBody
    @RequestMapping(value = "/clearUserLock", method = RequestMethod.POST)
    public Response clearUserLock(@RequestParam String id){
        try{
            ((UserService)generiService).clearUserLock(id);
            return new Response(0);
        }catch(Exception e){
            logger.debug("解除用户锁定出错", e);
            return new Response(-1, e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/initPassword", method = RequestMethod.POST)
    public Response initPassword(@RequestParam String id){
        try{
            ((UserService)generiService).initPassword(id);
            return new Response(0);
        }catch(Exception e){
            logger.debug("初始化密码出错", e);
            return new Response(-1, e.getMessage());
        }
    }

    @Override
    @RequestMapping(value = "/page", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Page<User> page(@ModelAttribute User query, @PageableDefault Pageable pageable){
        try{
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            if(user.isAdmin() || StringUtils.isNotBlank(query.getDeptId()))
                return generiService.findByPage(query, pageable);
            else{
                return getUserPage(query, user, pageable);
            }
        }catch(Exception e){
            logger.error("翻页查询出错", e);
            throw new RestControllerException("翻页查询出错", e);
        }
    }

    private PageImpl getUserPage(User query, User user, Pageable pageable){
        List<User> userList = new ArrayList<>();
        Department department = user.getDepartment();
        query.setDeptId(department.getId());
        List<User> users = generiService.find(query);
        userList.addAll(users);
        List<Department> departmentList = user.getDeptList();
        if(departmentList!=null){
            for(Department dept: departmentList){
                query.setDeptId(dept.getId());
                users = generiService.find(query);
                userList.addAll(users);
            }
        }
        List<User> users1 = new ArrayList<>();
        for(User user1: userList){
            if(!users1.contains(user1))
                users1.add(user1);
        }
        return new PageImpl<>(users1, pageable, users1.size());
    }

    @ResponseBody
    @RequestMapping(value = "/enable", method = RequestMethod.POST)
    public Response enable(@RequestParam String id){
        try{
            ((UserService)generiService).enable(id);
            return new Response(0);
        }catch(Exception e){
            logger.debug("激活用户出错", e);
            return new Response(-1, e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/disable", method = RequestMethod.POST)
    public Response disable(@RequestParam String id){
        try{
            ((UserService)generiService).disable(id);
            return new Response(0);
        }catch(Exception e){
            logger.debug("禁用用户出错", e);
            return new Response(-1, e.getMessage());
        }
    }

    @RequestMapping(value = "/queryUsers", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Role> queryUsers(@RequestParam String userQuery){
        Map<String, Object> params = new HashMap<>();
        params.put("name", userQuery);
        params.put("userId",userQuery);
        return userDao.find("selectByPage", params);
    }

    /**
     * 获取部门的角色、关联部门信息信息
     * @param id
     * @return
     */
    @RequestMapping(
            value = {"/extension/{id}"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    @ResponseBody
    public Map getRoles(@PathVariable String id){
        User user = super.get(id);
        Map map = new HashMap();
        map.put("deptList",user.getDeptList());
        map.put("roleList",user.getRoleList());
        return map;
    }
}