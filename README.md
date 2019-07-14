# spring-cloud-advance
 @ControllerAdvice+@ExceptionHandler全局处理Controller层异常 消灭95%以上的 try catch 
   UnifiedExceptionHandler:
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
 
 #数据源的配置
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
