package com.jsict.biz.controller;

import com.jsict.biz.model.*;
import com.jsict.biz.service.MultiCodeService;
import com.jsict.biz.service.MultiCodeValueService;
import com.jsict.framework.core.controller.*;


import com.jsict.framework.filter.EscapeScriptwrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Auto-Generated by UDP Generator
 */


@Controller
@RequestMapping("/multiValue")
public class MultiCodeValueController extends AbstractGenericController<MultiCodeValue, String, MultiCodeValue> {

    @Autowired
    private MultiCodeValueService multiCodeValueService;
    @Autowired
    private MultiCodeService multiCodeService;
    private static final Logger logger = LoggerFactory.getLogger(MultiCodeController.class);
    protected static final Integer HASCHILD = 2;


    @RequestMapping(value = "/multiValueTree", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public List<MultiCodeValue> multiTree() {
        return multiCodeValueService.findAll();
    }


    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public List<MultiCodeValue> list(@ModelAttribute MultiCodeValue query) {
        try {
            return generiService.find(query);
        } catch (Exception e) {
            logger.error("查询列表出错", e);
            throw new RestControllerException("查询列表出错", e);
        }
    }

    @RequestMapping(value = "/layUIPage", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public LayUIPageResponse<MultiCodeValue> layUIPage(@ModelAttribute MultiCodeValue query, @PageableDefault Pageable pageable) {
        try {
            Page<MultiCodeValue> page = generiService.findByPage(query, pageable.previousOrFirst());
            return new LayUIPageResponse<MultiCodeValue>("0", "查询成功", page.getTotalElements(), page.getContent());
        } catch (Exception e) {
            logger.error("翻页查询出错", e);
            return new LayUIPageResponse("-1", e.getMessage(), null, null);
        }
    }

    @Override
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response save(@ModelAttribute MultiCodeValue entity, @RequestParam(value = CSRFTokenManager.CSRF_PARAM_NAME) String paramToken, HttpServletRequest request) {
        Response response;
        if (multiCodeValueService.findValue(entity)) {
            response = super.save(entity, paramToken, request);
        } else
            response = new Response(ERROR, "代码项显示值与代码项值不可存在重复！");
        return response;
    }

    @Override
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response update(@ModelAttribute MultiCodeValue entity, @RequestParam(value = CSRFTokenManager.CSRF_PARAM_NAME) String paramToken, HttpServletRequest request) {
        Response response;
        if (multiCodeValueService.updateValue(entity) || multiCodeValueService.findByUpdate(entity)) {
            response = super.update(entity, paramToken, request);
        } else
            response = new Response(ERROR, "代码项显示值与代码项值不可存在重复！");
        return response;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response delete(@RequestParam List<String> ids) {
        Response response;
        List<MultiCodeValue> multi = new ArrayList<>();
        try {
            for (String id : ids) {
                if (!id.equals(""))
                    multi.add(multiCodeValueService.get(id));
            }
            boolean isSuccess = multiCodeValueService.batchDelete(multi);
            if (isSuccess)
                response = new Response(SUCCESS);
            else
                response = new Response(ERROR, NO_ENTITY);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            response = new Response(ERROR, e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/checkHasChild", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response checkHasChild(@RequestParam List<String> ids) {
        Response response;
        boolean hasChild = false;
        try {
            for (String id : ids) {
                if (!id.equals("")) {
                    MultiCodeValue m = multiCodeValueService.get(id);
                    if (multiCodeValueService.findByParentItemId(m.getId()).size() != 0)
                        hasChild = true;
                }
            }
            if (!hasChild)
                response = new Response(SUCCESS);
            else {
                response = new Response(HASCHILD);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            response = new Response(ERROR, e.getLocalizedMessage());
        }
        return response;
    }


    //Excel导入
    @RequestMapping(value = "/excelImport", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response excelImport(HttpServletRequest request) {
        EscapeScriptwrapper escapeScriptwrapper = (EscapeScriptwrapper) ((ShiroHttpServletRequest) request).getRequest();
        MultipartHttpServletRequest multipartReq = (MultipartHttpServletRequest) escapeScriptwrapper.getOrigRequest();
        Map<String, MultipartFile> fileMap = multipartReq.getFileMap();
        List<MultiCodeValue> multiCodeValueList = new ArrayList<>();
        for (Map.Entry<String, MultipartFile> me : fileMap.entrySet()) {
            MultipartFile multipartFile = me.getValue();
            Workbook wb = null;
            String[] split = multipartFile.getOriginalFilename().split("\\.");
            try {
                if ("xls".equals(split[1])) {
                    wb = new HSSFWorkbook(multipartFile.getInputStream());
                } else if ("xlsx".equals(split[1])) {
                    wb = new XSSFWorkbook(multipartFile.getInputStream());
                }
                if (wb != null) {
                    Sheet sheet = wb.getSheetAt(0);
                    int firstRowIndex = sheet.getFirstRowNum() + 1;
                    int lastRowIndex = sheet.getLastRowNum();
                    String codeId = request.getParameter("id");
                    List<MultiCodeValue> listAll=multiCodeValueService.findValueByCodeId(codeId);
                    for (int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {
                        Row row = sheet.getRow(rIndex);
                        MultiCodeValue multiValue = new MultiCodeValue();
                        MultiCodeValue multiParent = new MultiCodeValue();
                        if (row != null) {
                            Cell keyCell = row.getCell(0);
                            Cell valueCell = row.getCell(1);
                            Cell parentCell = row.getCell(2);
                            if (parentCell == null) {
                                DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
                                Random random = new Random();
                                int x = random.nextInt(10);
                                String id = request.getParameter("id");
                                multiValue.setItemValue(decimalFormat.format(Double.valueOf(keyCell.toString())));
                                multiValue.setCodeName(valueCell.toString());
                                multiValue.setCodeId(id);
                                multiValue.setSortSq(x);
                                multiValue.setItemCode(multiCodeValueService.getCode(multiValue));
                                multiValue.setChildNum(0);
                                boolean bool=true;
                                for(MultiCodeValue reName:listAll){
                                    if(multiValue.getCodeName().equals(reName.getCodeName())){
                                        bool=false;
                                        break;
                                    }
                                    if(multiValue.getItemValue().equals(reName.getItemValue())){
                                        bool=false;
                                        break;
                                    }
                                }
                                if (bool) {
                                    multiCodeValueList.add(multiValue);
                                } else {
                                    for (MultiCodeValue multiCodeValue : multiCodeValueList) {
                                        multiCodeValueService.delete(multiCodeValue);
                                    }
                                    return new Response(ERROR, "存在代码项值或代码显示值重复！");
                                }
                            } else {
                                DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
                                String id = request.getParameter("id");
                                multiParent.setCodeName(parentCell.toString());
                                multiParent.setCodeId(id);
                                multiParent.setDelFlag(0);
                                List<MultiCodeValue> listParent = multiCodeValueService.find(multiParent);
                                int i = 0;
                                if (listParent.isEmpty()) {
                                    for (MultiCodeValue multiCodeValue : multiCodeValueList) {
                                        if (parentCell.toString().equals(multiCodeValue.getCodeName())) {
                                            multiCodeValueService.save(multiCodeValue);
                                            i++;
                                            multiCodeValueList.remove(multiCodeValue);
                                            listParent.add(multiCodeValue);
                                            break;
                                        }
                                    }
                                    if (i == 0) {
                                        return new Response(ERROR, "父节点代码显示值出错！");
                                    }
                                }
                                String parentId = listParent.get(0).getId();
                                Random random = new Random();
                                int x = random.nextInt(10);
                                multiValue.setItemValue(decimalFormat.format(Double.valueOf(keyCell.toString())));
                                multiValue.setCodeName(valueCell.toString());
                                multiValue.setCodeId(id);
                                multiValue.setSortSq(x);
                                multiValue.setItemCode(multiCodeValueService.getCode(multiValue));
                                multiValue.setParentItemId(parentId);
                                multiValue.setChildNum(0);
                                boolean bool=true;
                                for(MultiCodeValue reName:listAll){
                                    if(multiValue.getCodeName().equals(reName.getCodeName())){
                                        bool=false;
                                        break;
                                    }
                                    if(multiValue.getItemValue().equals(reName.getItemValue())){
                                        bool=false;
                                        break;
                                    }
                                }
                                if (bool) {
                                    multiCodeValueList.add(multiValue);
                                } else {
                                    for (MultiCodeValue multiCodeValue : multiCodeValueList) {
                                        multiCodeValueService.delete(multiCodeValue);
                                    }
                                    return new Response(ERROR, "存在代码项值或代码显示值重复！");
                                }
                            }
                        }
                    }
                    multiCodeValueService.batchSave(multiCodeValueList);
                }
            } catch (Exception e) {
                logger.error("导入异常，请检查文件！", e);
                return new Response(ERROR, "导入异常，请检查文件！");
            }
        }
        return new Response(SUCCESS);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String execute2(HttpServletRequest request, @RequestParam String method, @RequestParam(required = false) String id, @RequestParam(required = false) String codeName, @RequestParam(required = false) String moduleId) {
        String controllerMapping = this.getClass().getAnnotation(RequestMapping.class).value()[0];
        String csrfToken = CSRFTokenManager.getTokenForSession(request.getSession());
        if (StringUtils.isNotBlank(id))
            request.setAttribute("id", id);
        if (StringUtils.isNotBlank(codeName))
            request.setAttribute("codeName", codeName);
        if (StringUtils.isNotBlank(moduleId))
            request.getSession().setAttribute("moduleId", moduleId);
        request.setAttribute(CSRFTokenManager.CSRF_PARAM_NAME, csrfToken);
        return controllerMapping + "/" + method;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Response delete(@PathVariable String id) {
        Response response;
        try {
            boolean isSuccess = multiCodeValueService.deleteById(id);
            if (isSuccess)
                response = new Response(SUCCESS);
            else
                response = new Response(ERROR, NO_ENTITY);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            response = new Response(ERROR, e.getLocalizedMessage());
        }
        return response;
    }


}