package com.jsict.biz.controller;

import com.jsict.biz.service.MultiCodeService;
import com.jsict.framework.core.controller.AbstractGenericController;
import com.jsict.biz.model.MultiCode;


import com.jsict.framework.core.controller.CSRFTokenManager;
import com.jsict.framework.core.controller.Response;
import com.jsict.framework.core.controller.RestControllerException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Auto-Generated by UDP Generator
 */
@Controller
@RequestMapping("/multi")
public class MultiCodeController extends
        AbstractGenericController<MultiCode, String,
                MultiCode> {
    @Autowired
    private MultiCodeService multiCodeService;
    private static final Logger logger = LoggerFactory.getLogger(MultiCodeController.class);
    protected static final Integer HASCHILD = 2;

    @RequestMapping(value = "multiTree", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public List<MultiCode> dictTree() {
        return multiCodeService.findAll();
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public List<MultiCode> list(@ModelAttribute MultiCode query) {
        try {
            return generiService.find(query);
        } catch (Exception e) {
            logger.error("查询列表出错", e);
            throw new RestControllerException("查询列表出错", e);
        }
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String execute1(HttpServletRequest request, @RequestParam String method, @RequestParam(required = false) String id, @RequestParam(required = false) String codeName, @RequestParam(required = false) String pId, @RequestParam(required = false) String moduleId) {
        String controllerMapping = this.getClass().getAnnotation(RequestMapping.class).value()[0];
        String csrfToken = CSRFTokenManager.getTokenForSession(request.getSession());
        if (StringUtils.isNotBlank(id))
            request.setAttribute("id", id);
        if (StringUtils.isNotBlank(codeName))
            request.setAttribute("codeName", codeName);
        if (StringUtils.isNotBlank(pId))
            request.setAttribute("pId", pId);
        if (StringUtils.isNotBlank(moduleId))
            request.getSession().setAttribute("moduleId", moduleId);
        request.setAttribute(CSRFTokenManager.CSRF_PARAM_NAME, csrfToken);
        return controllerMapping + "/" + method;
    }

    @Override
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response save(@ModelAttribute MultiCode entity, @RequestParam(value = CSRFTokenManager.CSRF_PARAM_NAME) String paramToken, HttpServletRequest request) {
        Response response;
        if (multiCodeService.findValue(entity)) {
            response = super.save(entity, paramToken, request);
        } else
            response = new Response(ERROR, "代码名称存在重复");
        return response;
    }

    @Override
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response update(@ModelAttribute MultiCode entity, @RequestParam(value = CSRFTokenManager.CSRF_PARAM_NAME) String paramToken, HttpServletRequest request) {
        Response response;
        if (multiCodeService.findValue(entity)||multiCodeService.updateValue(entity)) {
            response = super.update(entity, paramToken, request);
        } else
            response = new Response(ERROR, "代码名称存在重复");
        return response;
    }


}