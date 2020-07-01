package com.framework.authority.service;

/**
 * 验证码校验抽象类
 * 
 * @author nbbjack
 */
public interface AbstractCheckSmsCode {
    /**
     * 短信验证码校验逻辑
     * 
     * @param mobile
     * @param code
     * @return
     */
    Boolean checkCode(String mobile, String code);
}