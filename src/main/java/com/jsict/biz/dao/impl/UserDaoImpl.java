package com.jsict.biz.dao.impl;

import com.jsict.biz.dao.UserDao;
import com.jsict.biz.model.User;
import com.jsict.framework.core.dao.hibernate.GenericHibernateDaoImpl;
import com.jsict.framework.utils.Encodes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/**
* Auto-Generated by UDP Generator
*/
@Repository
public class UserDaoImpl extends GenericHibernateDaoImpl<User, String> implements UserDao {

    @Override
    public User save(User user){
        user.setId(null);
        String defualtPassword = Encodes.encodeMD5(sysConfig.getConfig().getString("defaultPassword"));
        user.setPassword(defualtPassword);
        return super.save(user);
    }

    @Override
    public User update(User user){
        User dbUser = getById(user.getId());
        dbUser.setSort(user.getSort());
        dbUser.setBirthday(user.getBirthday());
        dbUser.setDeptId(user.getDeptId());
        dbUser.setEmail(user.getEmail());
        if(null!=user.getGender()&&user.getGender().contains("|")){
            String[] genders = user.getGender().split("\\|");
            dbUser.setGender(genders[2]);
        }else
            dbUser.setGender(user.getGender());
        dbUser.setRoleList(user.getRoleList());
        dbUser.setMobile(user.getMobile());
        dbUser.setName(user.getName());
        dbUser.setDeptList(user.getDeptList());
        dbUser.setUserId(user.getUserId());
        dbUser.setLastPasswordErrorTime(user.getLastPasswordErrorTime());
        dbUser.setPasswordErrorTimes(user.getPasswordErrorTimes());
        if(StringUtils.isNotBlank(user.getPassword()))
            dbUser.setPassword(user.getPassword());
        return super.update(dbUser);
    }
}
