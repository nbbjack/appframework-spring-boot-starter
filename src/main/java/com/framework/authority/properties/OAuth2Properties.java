package com.framework.authority.properties;

import lombok.Data;

import java.util.Map;

/**
 * @author nbbjack
 */
@Data
public class OAuth2Properties {
    /**
     * jwtSigningKey
     */
    private String jwtSigningKey = "security";
    /**
     * 确认授权页面
     */
    private String confirmUrl = "/oauth/confirm_access";
    /**
     * token增强信息
     */
    private Map<String, Object> tokenInfo;
    /**
     * 客户端信息
     */
    private OAuth2ClientProperties[] clients = {};

}
