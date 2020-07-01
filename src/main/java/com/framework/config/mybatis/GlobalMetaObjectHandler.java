package com.framework.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * @author nbbjack
 */
@Slf4j
public class GlobalMetaObjectHandler implements MetaObjectHandler {

    String createTimeFieldName = "createTime";
    String updateTimeFieldName = "updateTime";
    String deletedFieldName = "deleted";

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            Object createTime = getFieldValByName(createTimeFieldName, metaObject);
            Object updateTime = getFieldValByName(updateTimeFieldName, metaObject);
            Object delTag = getFieldValByName(deletedFieldName, metaObject);

            Date date = new Date();
            if (null == createTime) {
                setFieldValByName(createTimeFieldName, date, metaObject);
            }
            if (null == updateTime) {
                setFieldValByName(updateTimeFieldName, date, metaObject);
            }
            if (null == delTag) {
                setFieldValByName(deletedFieldName, 0, metaObject);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            setFieldValByName(updateTimeFieldName, new Date(), metaObject);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }
}
