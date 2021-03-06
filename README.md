
#精简版全局异常处理： https://www.jianshu.com/p/accec85b4039

# spring-cloud-advance  祥述：https://mp.weixin.qq.com/s/nQtkqN9IwZek6LX1Bvgf6A
    统一异常处理实战：
    1、用 Assert(断言) 替换 throw exception；
    2、每增加一种异常情况，只需增加一个枚举实例即可，再也不用每一种异常都定义一个异常类了；
    3、如何让404也抛出异常呢，只需在properties文件中加入如下配置即可：
    		spring.mvc.throw-exception-if-no-handler-found=true
    		spring.resources.add-mappings=false
    	如此，就可以异常处理器中捕获它了，然后前端只要捕获到特定的状态码，立即跳转到404页面即可。
# Springboot Web层以AOP注解的方式进行接口参数统一校验 ValidationBean.class   ResponseInterceptor

# 概述
 @ControllerAdvice + @ExceptionHandler全局处理Controller层异常 消灭95%以上的 try catch 
 
   * @ExceptionHandler：统一处理某一类异常，从而能够减少代码重复率和复杂度  UnifiedExceptionHandler.class中各类型异常标识 
   * @ControllerAdvice：异常集中处理，更好的使业务逻辑与异常处理剥离开      UnifiedExceptionHandler.class
   * @ResponseStatus：可以将某种异常映射为HTTP状态码                       
   
   UnifiedExceptionHandler.class 全局异常处理器:
    	BusinessException.class	业务异常
    	BaseException.class		自定义异常
    	BindException.class		参数绑定异常	
    	MethodArgumentNotValidException.class	参数校验(Valid)异常，将校验失败的所有异常组合成一条错误信息
    	Exception.class			未定义异常
    	/**
             * Controller上一层相关异常
             *
             * @param e 异常
             * @return 异常结果
             */
            @ExceptionHandler({
                    NoHandlerFoundException.class,
                    HttpRequestMethodNotSupportedException.class,
                    HttpMediaTypeNotSupportedException.class,
                    HttpMediaTypeNotAcceptableException.class,
                    MissingPathVariableException.class,
                    MissingServletRequestParameterException.class,
                    TypeMismatchException.class,
                    HttpMessageNotReadableException.class,
                    HttpMessageNotWritableException.class,
                    // BindException.class,
                    // MethodArgumentNotValidException.class
                    ServletRequestBindingException.class,
                    ConversionNotSupportedException.class,
                    MissingServletRequestPartException.class,
                    AsyncRequestTimeoutException.class
            })
 
 
 #eureka： ueh-discovery  ： 8761		
 serviceUrl:  defaultZone: http://localhost:8761/eureka/
     服务A注册了就可以对外提供Eureka接口
     服务B注册了就可以对外提供Eureka接口.......
 
 #简易数据源配置[不需要连接db]
 spring:
   datasource:
     platform: h2
     schema: classpath:schema.sql
     data: classpath:[data.sql]
     driver-class-name: org.h2.Driver
     
# org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
[作用：]
它可以帮助SpringBoot应用将所有符合条件的@Configuration配置都加载到当前SpringBoot创建并使用的IoC容器。
总结,@EnableAutoConfiguration 作用： 
   https://www.jianshu.com/p/464d04c36fb1  			                         https://cloud.tencent.com/info/3bafc66300de1913046352fe07f99192.html
从classpath中搜索所有META-INF/spring.factories配置文件然后，将其中org.springframework.boot.autoconfigure.EnableAutoConfiguration key对应的配置项加载到spring容器
只有spring.boot.enableautoconfiguration为true（默认为true）的时候，才启用自动配置
@EnableAutoConfiguration还可以进行排除，排除方式有2中，一是根据class来排除（exclude），二是根据class name（excludeName）来排除
其内部实现的关键点有
1）ImportSelector 该接口的方法的返回值都会被纳入到spring容器管理中
2）SpringFactoriesLoader 该类可以从classpath中搜索所有META-INF/spring.factories配置文件，并读取配置

[实例：https://blog.csdn.net/zxc123e/article/details/80222967 ：]
@Configuration   
//为带有@ConfigurationProperties注解的Bean提供有效的支持。
// 这个注解可以提供一种方便的方式来将带有@ConfigurationProperties注解的类注入为Spring容器的Bean。
@EnableConfigurationProperties(HelloProperties.class)//开启属性注入,通过@autowired注入
@ConditionalOnClass(Hello.class)//判断这个类是否在classpath中存在，如果存在，才会实例化一个Bean
// The Hello bean will be created if the hello.enable property exists and has a value other than false
// or the property doesn't exist at all.
@ConditionalOnProperty(prefix="hello", value="enabled", matchIfMissing = true)
public class HelloAutoConfiguration {

    @Autowired
    private HelloProperties helloProperties;

    @Bean
    @ConditionalOnMissingBean(Hello.class)//容器中如果没有Hello这个类,那么自动配置这个Hello
    public Hello hello() {
        Hello hello = new Hello();
        hello.setMsg(helloProperties.getMsg());
        return hello;
    }

}
