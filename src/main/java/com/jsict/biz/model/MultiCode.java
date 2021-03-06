package com.jsict.biz.model;

import com.jsict.framework.core.model.BaseEntity;
import com.jsict.framework.core.dao.annotation.LogicDel;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Auto-Generated by UDP Generator
 */
@LogicDel
@Entity
@Table(name = "multiCode")
public class MultiCode extends BaseEntity<String> {

    /** code名 */
    @Column(name = "code_name", length = 40)
    private String codeName;
    /** 排序 */
    @Column(name = "sort_sq", length = 30)
    private Integer sortSq;
    /** 评论 */
    @Column(name = "comments", length = 200)
    private String comments;

    public void setCodeName(String codeName){
        this.codeName = codeName;
    }

    public String getCodeName(){
        return this.codeName;
    }
    public void setSortSq(Integer sortSq){
        this.sortSq = sortSq;
    }

    public Integer getSortSq(){
        return this.sortSq;
    }
    public void setComments(String comments){
        this.comments = comments;
    }

    public String getComments(){
        return this.comments;
    }
}