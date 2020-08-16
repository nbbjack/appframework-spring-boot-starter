### 前言

​ 本模块是由 SpringBoot + Spring Security + Spring Security OAuth2 + Mybatis Plus + Mysql 组成的一套开发认证授权框架。该套框架基于约定大于配置的原则实现了大部分项目开发中需要用到的功能模块，比如接口权限控制 JWT Token、App 认证、MybatisPlus 集成与配置、Swagger 成与配置、跨域配置、全局异常处理、API 接口实现统一格式返回等等。

​​ 本模块基于 Jdk 1.8、SpringBoot2 开发，同时遵循[《阿里巴巴 Java 开发手册》](https://github.com/alibaba/p3c)、API 接口遵循 Restful 风格。

建议配置

### 一、在 maven 项目使用

大家都知道怎么使用，不赘述。

### 二、一些使用说明

#### 2.1 跨域请求配置

​ 继承`GlobalCorsConfig`

```java
@Configuration
@EnableWebMvc
public class CorsConfig extends GlobalCorsConfig {

}
```

#### 2.2 全局异常处理

​ 继承`GlobalExceptionHandler`

```java
@ControllerAdvice
@Component
public class ExceptionHandler extends GlobalExceptionHandler {

}
```

##### 2.3 mybatis plus 配置

​ 继承`GlobalMybatisConfig`

```java
@Configuration
public class MybatisPlusConfig extends GlobalMybatisConfig {

}
```

#### 2.4 Swagger 文档配置

​ 继承`GloablSwaggerConfig`

```java
@Configuration
@EnableSwagger2
public class SwaggerConfig extends GloablSwaggerConfig {

}
```

##### 2.4.1 设置是否开启 Swagger

​ Swagger 通常只在本地开发环境或内网测试环境开启，生产环境中 Swagger 一般都是需要关闭的。因此我们提供了一个抽象方法 boolean swaggerEnable()需要你来指定 Swagger 是否开启，通常这个开关配置在配置文件中，开发环境设置为 true,生产设置成 false，如下：

```java
@Configuration
@EnableSwagger2
public class SwaggerConfig extends GloablSwaggerConfig {
    @Value("${swagger.enable}")
    private boolean enable;

    @Override
    public boolean swaggerEnable() {
        return enable;
    }
}
```

##### 2.4.2 设置多个 Docket

​ 有时候 API 会分为多个模块，因此需要对 API 进行分组显示，此时可以重写`configureSwaggerApiInfo()`方法，返回多个你需要的 ApiInfo，例如：

```java
@Override
protected List<SwaggerApiInfo> configureSwaggerApiInfo() {

    List<SwaggerApiInfo> swaggerApiInfos = new ArrayList<>();

    SwaggerApiInfo xxxApiInfo1 = new SwaggerApiInfo("xxx接口文档","com.xxx.xxx.controller","V1.0");
    swaggerApiInfos.add(xxxApiInfo1);

    SwaggerApiInfo xxxApiInfo2 = new SwaggerApiInfo("xxx接口文档","com.xxx.xxx.controller","V1.0");
    swaggerApiInfos.add(xxxApiInfo2);

    return swaggerApiInfos;
}
```

​ 暂时最多支持配置 10 个 Docket，正常情况下 10 个已经足够了，如果需要，框架还能提供更多的配置数量。

#### 2.5 接口权限控制配置

##### 2.5.1 实现接口`UserDetailsService`,重写`loadUserByUsername()`方法，查询数据库返回`UserDetails`

​ 密码加密方式默认用的是`BCryptPasswordEncoder`

```java
@Service
public class UserDetailServiceImpl implements UserDetailsService {
	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    IUserAdminService userAdminServiceImpl;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        String password = bCryptPasswordEncoder.encode(userAdminServiceImpl.getUserPassword(userName));
        return new User(userName, password, getAuthority());
    }

    private List getAuthority() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
```

##### 2.5.2 继承`GlobalWebSecurityConfigurer`

```java
@Configuration
@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class WebSecurityConfigurer extends GlobalWebSecurityConfigurer {

}
```

##### 2.5.3 继承`OAuth2AuthorizationServerConfig`，OAuth2 认证服务器配置

```java
@Configuration
public class Oauth2AuthentizationConfig extends OAuth2AuthorizationServerConfig {

}
```

##### 2.5.4 继承`OAuth2ResourceServerConfig`,OAuth2 资源服务器，该类配置了自定义登录和短信验证码登录

```java
@Configuration
public class Oauth2ResoucesConfig extends OAuth2ResourceServerConfig {

}
```

##### 2.5.5 指定某种环境下关闭接口权限校验

​ 为了方便开发，一般我们在本地开发环境中会关闭接口权限校验，重写资源服务配置`OAuth2ResourceServerConfig`下的`customCloseAuthorityEvironment()`方法，你可以指定某种环境下关闭接口权限校验，如下：

```java
@Configuration
public class Oauth2ResoucesConfig extends OAuth2ResourceServerConfig {

    @Value("${spring.profiles.active}")
    private String currentRunEnvironment;

    /**
     * 指定某种运行环境下关闭权限校验；为了方便开发，一般我们的dev环境会关闭接口权限校验
     * @return
     */
    @Override
    public CloseAuthorityEvironment customCloseAuthorityEvironment(){
        return new CloseAuthorityEvironment(currentRunEnvironment,"dev");
    }
}
```

##### 2.5.6 自定义放行接口

​ 如果你需要指定某些接口要放行，你可以重写重写资源服务配置`customConfigure(HttpSecurity http)`，通过 HttpSecurity 设置放行接口，然后返回设置后的 HttpSecurity

```java
/**
 * @author nbbjack
 * @create 2020-03-20 13:15
 */
@Configuration
public class Oauth2ResoucesConfig extends OAuth2ResourceServerConfig {
    @Value("${spring.profiles.active}")
    private String currentRunEnvironment;

    /**
     * 用户自定义配置，子类可覆盖自定义实现
     * @param http
     * @throws Exception
     */
    @Override
    protected HttpSecurity customConfigure(HttpSecurity http) throws Exception{
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers("/verify-code").permitAll()
                .anyRequest().authenticated();
        return http;
    }

    /**
     * 指定某种运行环境下关闭权限校验；为了方便开发，一般我们的dev环境会关闭接口权限校验
     * @return
     */
    @Override
    public CloseAuthorityEvironment customCloseAuthorityEvironment(){
        return new CloseAuthorityEvironment(currentRunEnvironment,"dev");
    }
}
```

```java
/**
 * 在资源服务器中配置第三方应用访问权限，第三放client拥有scopes权限则可以访问
 * 用户自定义配置，子类可覆盖自定义实现
 * @param http
 * @throws Exception
 */
@Override
protected HttpSecurity customConfigure(HttpSecurity http) throws Exception{
    http.cors().and().csrf().disable().authorizeRequests()
        .antMatchers("/code").permitAll()
        .antMatchers( "/read").access("#oauth2.hasScope('read')")
        .antMatchers( "/write").access("#oauth2.hasScope('write')")
        .anyRequest().authenticated();
    return http;
}
```

##### 2.5.7 OAuth2 参数配置

- 1、继承`SecurityProperties`

```java
@Component
public class FrameworkProperties extends SecurityProperties {

}
```

- 2、继承`TokenStoreConfig`使 token 配置生效,token 存储默认使用 jwt，通过配置可修改为 redis 存储

```java
@Component
public class TokenConfig extends TokenStoreConfig {

}
```

- 配置 client 参数

```yml
#spring security oauht2  参数说明见2.5.10
framework:
  security:
    oauth2:
      clients[0]:
        clientId: client
        clientSecret: clientSecret
        accessTokenValiditySeconds: 604800
        refreshTokenValiditySeconds: 2592000
        authorizedGrantTypes: ["refresh_token", "password"]
        redirectUris: "http://example.com"
        scopes: ["all", "read", "write"]
      tokenStore: jwt
      jwtSigningKey: jwtSecret
```

##### 2.5.8 OAuth2 登录

​ 登录接口及刷新 token 接口都需要设置 Authorization 为 Basic Auth，其余接口设置请求头 Authorization 参数为 token 进行访问，密码登录及验证码登录只接受 Json 格式登录，设置请求头`Content-Type`为`application/json`

- 1、密码登录 ， path: " http://localhost:8080/login "

  - 设置 Authorization

  - 设置请求头`Content-Type`为`application/json`

  - 参数

    ```json
    {
      "username": "username",
      "password": "password"
    }
    ```

- 2、短信验证码登录，path: " http://localhost:8080/login/mobile "

  - 设置 Authorization

  - 设置请求头`Content-Type`为`application/json`

  - 参数

  ```json
  {
    "smsCode": "code",
    "mobile": "mobile"
  }
  ```

- 刷新 token,path:" http://localhost:8080/oauth/token "

  - 设置设置 Authorization

  - 设置请求头`Content-Type`为`application/json`

  - 参数

  - 设置 from-data

    ```
        grant_type = refresh_token
        refresh_token = refresh_token
    ```

##### 2.5.9 自定义授权页面

​ 1、定义授权页面视图

```java
@Controller
@SessionAttributes({ "authorizationRequest" })
public class OAuthController {

    @RequestMapping("/custom/confirm_access")
    public ModelAndView getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) throws Exception {
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");
        ModelAndView view = new ModelAndView();
        view.setViewName("base-grant");
        view.addObject("clientId", authorizationRequest.getClientId());
        return view;
    }
}
// http://localhost:8080/oauth/authorize?response_type=code&client_id=client&redirect_uri=http://www.example.com&scope=all
```

​ 2、在配置文件中配置授权页面路径

```yml
framework:
  security:
    oauth2:
      confirm_url: "/custom/confirm_access"
```

##### 2.5.10 framework 参数配置说明

```yml
framework:
  security:
    oauth2:
      # 配置认证服务器，可以配置多个clients
      clients:
        - clientId: client #客户端id
          clientSecret: 123qwe #客户端密码
          accessTokenValiditySeconds: 604800 #token过期时间
          # 客户端授权模式
          authorizedGrantTypes:
            ["refresh_token", "password", "authorization_code"]
          # 回调地址
          redirectUris: "http://example.com"
          # 客户端权限
          scopes: ["all", "read", "write"]
        - clientId: client2
          clientSecret: 123qwe
          accessTokenValiditySeconds: 604800
          authorizedGrantTypes:
            ["refresh_token", "password", "authorization_code"]
          redirectUris: "http://example.com"
          scopes: ["all", "read", "write"]
      # token存储类型 默认是jwt 也可以配置为redis
      tokenStore: jwt
      # token秘钥
      jwtSigningKey: secret
      # 授权页面路径 不设置默认oauth2授权页面
      confirm_url: "/custom/confirm_access"
      # token增强信息 可配置多个
      tokenInfo:
        author: "xxx"
        project: "xxxx"
```

#### 2.6 Restful API 返回统一的数据格式到前端

##### 2.6.1 framework 框架中，统一返回到前端的格式是 ResponseResult

```java
public class ResponseResult {
    private int code;
    private String msg;
    private Object data;
}
```

##### 2.6.2 server 端的异常也会被全局拦截，统一返回 ResponseResult 格式

参见 2.2

##### 2.6.3 全局拦截 Controller 层 API，对所有返回值统一包装成 ResponseResult 格式再返回到前端

继承`GlobalReturnConfig`

```java
@EnableWebMvc
@Configuration
@RestControllerAdvice({"com.netx.web.controller"})
public class ControllerReturnConfig  extends GlobalReturnConfig {

}
```

​ 注意：@RestControllerAdvice 要设置扫描拦截包名，如：`com.netx.web.controller`，这样就只拦截 controller 包下的类。否则 swagger 也会拦截影响 swagger 正常使用

​ 全局拦截后 Controller 层 API 不需要显示地返回 ResponseResult，因为会全局拦截处理并返回 ResponseResult 格式。

```java
 @ApiOperation("新增用户")
  @PostMapping
  public ResponseResult create(@RequestBody @Valid AddUsertVO addUserVO){
      userServiceImpl.saveUser(addUserVO);
      return ResponseResult.success();
  }
```

可以改成

```java
@ApiOperation("新增用户")
@PostMapping
public void createAgents(@RequestBody @Valid AddUsertVO addUserVO){
    userServiceImpl.saveUser(addUserVO);
}
```

代码

```java
@ApiOperation("获取用户列表")
@GetMapping("/list")
public ResponseResult pageUsers(UserSearch search) {
    IPage<UserVO> page = userServiceImpl.pageUsers(search);
    return ResponseResult.success(page);
}
```

可以改成

```java
@ApiOperation("获取用户列表")
@GetMapping("/list")
public IPage<UserVO> pageUsers(UserSearch search) {
    return userServiceImpl.pageUsers(search);
}
```

### 三、使用案例
