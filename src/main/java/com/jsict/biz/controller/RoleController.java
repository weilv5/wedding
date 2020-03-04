package com.jsict.biz.controller;

import com.jsict.biz.dao.RoleDao;
import com.jsict.biz.model.Role;
import com.jsict.biz.model.TreeNode;
import com.jsict.biz.model.User;
import com.jsict.biz.service.ModuleService;
import com.jsict.biz.service.RoleService;
import com.jsict.biz.service.UserService;
import com.jsict.framework.core.controller.AbstractGenericController;
import com.jsict.framework.core.controller.LayUIPageResponse;
import com.jsict.framework.core.controller.Response;
import com.jsict.framework.core.controller.RestControllerException;
import com.jsict.framework.core.security.UdpShiroFilterFactoryBean;
import com.jsict.framework.utils.JsonUtil;
import com.jsict.framework.utils.SpringContextUtil;
import com.jsict.framework.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Auto-Generated by UDP Generator
 */
@Controller
@RequestMapping("role")
public class RoleController extends
    AbstractGenericController<Role, String,
    Role> {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;


    @Autowired
    private RoleDao roleDao;

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    private static final String SELECT_MODULE_FOR_ROLE = "selectModuleForRole";
    private static final String SELECT_USER_FOR_ROLE = "selectUserForRole";

    @ResponseBody
    @RequestMapping(value = "/moduleTreeForRole", method = RequestMethod.POST)
    public TreeNode moduleTreeForRole(@RequestParam(required = false) String parentRoleId, @RequestParam String roleId){
        User user = (User)SecurityUtils.getSubject().getPrincipal();
        return moduleService.getModuleFullTreeForRole(user.isAdmin(), parentRoleId, roleId);
    }

    @ResponseBody
    @RequestMapping(value = "/saveModuleRole", method = RequestMethod.POST)
    public Response saveModuleRole(@RequestParam String roleId, @RequestParam("moduleIds[]") List<String> moduleIds){
        Response response;
        try{
            ((RoleService)generiService).saveModuleRole(moduleIds, roleId);
            UdpShiroFilterFactoryBean.SpringShiroFilter shiroFilter = (UdpShiroFilterFactoryBean.SpringShiroFilter) SpringContextUtil.getBean("shiroFilter");
            shiroFilter.updateFilterChainDefinitionMap();
            response = new Response(0);
        }catch(Exception e){
            logger.debug("模块设置角色失败", e);
            response = new Response(-1, e.getMessage());
        }
        return response;
    }

    @Override
    @RequestMapping(value = "/page", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Page<Role> page(@ModelAttribute Role query, @PageableDefault Pageable pageable){
        try{
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            if(user.isAdmin() || StringUtils.isNotBlank(query.getParentRoleId()))
                return generiService.findByPage(query, pageable);
            else{
                List<Role> roleList = getUserRole(user);
                return new PageImpl<>(roleList, pageable, roleList.size());
            }
        }catch(Exception e){
            logger.error("翻页查询出错", e);
            throw new RestControllerException("翻页查询出错", e);
        }
    }

    @Override
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public List<Role> list(@ModelAttribute Role query){
        try{
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            if(user.isAdmin() || StringUtils.isNotBlank(query.getParentRoleId()))
                return roleService.selectAll();
            else {
                return getUserRole(user);
            }
        }catch (Exception e){
            logger.error("查询列表出错", e);
            throw new RestControllerException("查询列表出错", e);
        }
    }

    @RequestMapping(value = "/queryRole", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Role> queryRole(@RequestParam String roleName){
        Map<String, Object> params = new HashMap<>();
        params.put("roleName", roleName);
       return roleDao.find("selectByPage", params);
    }

    private List<Role> getUserRole(User user){
        List<Role> roleList = new ArrayList<>();
        for(Role role: user.getRoleList())
            if(!roleList.contains(role))
                roleList.add(role);
        return roleList;
    }

    @RequestMapping(value = "/selectModuleForRole", method = RequestMethod.GET, produces = "application/json")
    public ModelAndView selectModuleForRole(@RequestParam String id){
        String controllerMapping = this.getClass().getAnnotation(RequestMapping.class).value()[0];
        Role role=roleService.get(id);
        ModelAndView mav = new ModelAndView(controllerMapping+ "/"+SELECT_MODULE_FOR_ROLE);
        mav.addObject("role", role);
        User user = (User)SecurityUtils.getSubject().getPrincipal();
        List<TreeNode> module= moduleService.getModuleTreeForRole(user.isAdmin(), role);
        String treeNode=JsonUtil.parseListToJson(module);
        mav.addObject("treeNode", treeNode);
        return mav;
    }

    @RequestMapping(value = "/selectUserForRole", method = RequestMethod.GET, produces = "application/json")
    public ModelAndView selectUserForRole(@RequestParam String id){
        String controllerMapping = this.getClass().getAnnotation(RequestMapping.class).value()[0];
        ModelAndView mav = new ModelAndView(controllerMapping+ "/"+SELECT_USER_FOR_ROLE);
        Role role=roleService.get(id);
        mav.addObject("role", role);
        return mav;
    }

    @RequestMapping(value = "/userForRolePage", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public LayUIPageResponse userForRolePage(@ModelAttribute User query, @PageableDefault Pageable pageable){
        try{
            if(null!=query.getRole()&& StringUtil.isNotEmpty(query.getRole().getId())){
            Page<User> page = userService.findByRole(query.getRole(),pageable);
            return new LayUIPageResponse<User>("0","查询成功", page.getTotalElements(), page.getContent());}
            else {
                return  new LayUIPageResponse("-1", "角色ID错误" , null, null);
            }
        }catch(Exception e){
            logger.error("翻页查询出错", e);
            return  new LayUIPageResponse("-1", e.getMessage() , null, null);
        }
    }
    @RequestMapping(value = "/addUser", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response addUser(@RequestParam String id,@RequestParam String roleId){
        if(StringUtil.isNotEmpty(id)&&StringUtil.isNotEmpty(roleId)){
            User user=userService.get(id);
            Role role=roleService.get(roleId);
            if (null==user)
                return new Response(-1, "查询不到用户");
            if (null==role)
                return new Response(-1, "查询不到角色");
            List<User> userlist=role.getUserList();
            for(User u:userlist){
                if(u.getId().equals(id))
                    return new Response(-1, "用户已存在");
            }

            if(null!=userlist)
                userlist.add(user);
            else {
                List list=new ArrayList();
                list.add(user);
                role.setUserList(list);
            }


            roleService.update(role);
            return new Response(0);
        }else
            return new Response(-1, "参数错误");
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response deleteUser(@RequestParam String id,@RequestParam String roleId){
        if(StringUtil.isNotEmpty(id)&&StringUtil.isNotEmpty(roleId)){
            User user=userService.get(id);
            Role role=roleService.get(roleId);
            if (null==user)
                return new Response(-1, "查询不到用户");
            if (null==role)
                return new Response(-1, "查询不到角色");
            List<User> userlist=role.getUserList();
            for(User u:userlist){
                if(u.getId().equals(id)){
                    userlist.remove(u);
                    break;
                }
            }
            role.setUserList(userlist);
            roleService.update(role);
            return new Response(0);
        }else
            return new Response(-1, "参数错误");
    }
}