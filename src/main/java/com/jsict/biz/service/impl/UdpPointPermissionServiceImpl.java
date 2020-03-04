package com.jsict.biz.service.impl;

import com.jsict.biz.model.Role;
import com.jsict.biz.model.UdpPointPermission;
import com.jsict.biz.model.User;
import com.jsict.biz.service.UdpPointPermissionService;
import com.jsict.framework.core.service.impl.GeneriServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Auto-Generated by UDP Generator
 */
@Service
public class UdpPointPermissionServiceImpl extends GeneriServiceImpl<UdpPointPermission, String> implements UdpPointPermissionService {
    @Override
    public List<UdpPointPermission> findByUser(User u) {
        Map<String, Object> params = new HashMap<>();
        params.put("delFlag", 0);
        List<String> roleList = new ArrayList<>();
        for (Role role : u.getRoleList()) {
            roleList.add(role.getId());
        }
        if (roleList.size() > 0)
            params.put("roleList", roleList);
        return genericDao.find("queryPermissionByRoles", params);
    }
/**
 * 示例代码
 * @Override
 * @Transactional
 * public UdpPointPermission save(UdpPointPermission entity) {
 * return super.save(entity);
 * }
 */
}
