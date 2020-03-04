package com.jsict.biz.controller;

import com.jsict.biz.model.WeddingPhoto;
import com.jsict.biz.service.WeddingPhotoService;
import com.jsict.framework.core.controller.AbstractGenericController;
import com.jsict.framework.core.controller.CSRFTokenManager;
import com.jsict.framework.core.controller.Response;
import com.jsict.framework.core.security.model.IUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Auto-Generated by UDP Generator
 *
 * 继承<code>com.jsict.framework.core.controller.AbstractGenericController</code>，封装通用的CRUD方法<br>
 * 可重写相关方法实现自定义CRUD的操作<br>
 * 可参考<a href="http://10.1.159.16/udp/unified_development_platform/blob/5.0-alpha/framework/src/main/java/com/jsict/framework/core/controller/AbstractGenericController.java">AbstractGenericController.java</a>
 */
@Controller
@RequestMapping("photoManage")
public class WeddingPhotoManageController extends
        AbstractGenericController<WeddingPhoto, String,
                WeddingPhoto> {

    @Autowired
    private WeddingPhotoService weddingPhotoService;

    @Override
    public Response save(@ModelAttribute WeddingPhoto entity, @RequestParam("CSRFToken") String paramToken, HttpServletRequest request) {
        Response response;
        try {

            String sessionToken = CSRFTokenManager.getTokenForSession(request.getSession());
            if (paramToken != null && paramToken.equals(sessionToken)) {
                CSRFTokenManager.refreshTokenForSession(request.getSession());

                String photoSort = entity.getPhotoSort();
                String albumSort = entity.getAlbumSort();
                String photos = entity.getPhoto();

                IUser user = (IUser) SecurityUtils.getSubject().getPrincipal();
                if(StringUtils.isNotBlank(photos))
                {
                    String[] photosArr = photos.split("\\|");
                    for(int i = 0; i < photosArr.length; i++)
                    {
                        String photo = photosArr[i];
                        if(StringUtils.isNotBlank(photo))
                        {
                            WeddingPhoto weddingPhoto = new WeddingPhoto();
                            weddingPhoto.setPhoto(photo);
                            weddingPhoto.setPhotoSort(photoSort);
                            weddingPhoto.setAlbumSort(albumSort);
                            if (user != null) {
                                weddingPhoto.setCreatorId(user.getId());
                            }
                            if ("true".equals(this.sysConfig.getConfig().get("open_sensitive_replace"))) {
                                this.sensitiveWordsProcService.sensitiveWordsReplace(weddingPhoto, this.sysConfig.getConfig().getString("sensitive_replace_words"));
                            }
                            weddingPhotoService.save(weddingPhoto);
                        }
                    }

                }
                response = new Response(SUCCESS, entity);
            } else {
                response = new Response(ERROR, "CSRF attack detected");
            }
        } catch (Exception var9) {
            response = new Response(ERROR, var9.getLocalizedMessage(), entity);
        }

        return response;
    }
}