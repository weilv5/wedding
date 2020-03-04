package com.jsict.biz.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import com.jsict.framework.core.dao.hibernate.GenericHibernateDaoImpl;
import com.jsict.biz.dao.InformationContentDao;
import com.jsict.biz.model.InformationContent;

/**
* Auto-Generated by UDP Generator
*/
@Repository
public class InformationContentDaoImpl extends GenericHibernateDaoImpl<InformationContent, String> implements InformationContentDao {

    @Override
    public InformationContent update(InformationContent entity){
        InformationContent dbInfo = getById(entity.getId());
        dbInfo.setContent(entity.getContent());
        dbInfo.setReleasedDate(entity.getReleasedDate());
        dbInfo.setInfoType(entity.getInfoType());
        dbInfo.setTitle(entity.getTitle());
        if(StringUtils.isNotBlank(entity.getAttachment()))
            dbInfo.setAttachment(entity.getAttachment());
        return super.update(dbInfo);
    }
}
