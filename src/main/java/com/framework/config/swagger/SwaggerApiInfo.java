package com.framework.config.swagger;

import lombok.Data;

/**
 * 配置Swagger Docket 的信息
 * 
 * @author nbbjack
 */
@Data
public class SwaggerApiInfo {

    private String groupName;

    private String basePackage;

    private String version;

    public SwaggerApiInfo() {
        super();
    }

    public SwaggerApiInfo(String groupName, String basePackage, String version) {
        this.groupName = groupName;
        this.basePackage = basePackage;
        this.version = version;
    }
}
