package com.sprainkle.ueh.licence.aspect;

import com.sprainkle.spring.cloud.advance.common.core.exception.BaseException;
import com.sprainkle.spring.cloud.advance.common.core.pojo.response.QR;
import com.sprainkle.ueh.licence.constant.ResponseEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Enumeration;
import java.util.HashSet;


/**
 * 请求和响应处理器，请求代码时进行Bean验证，
 * 控制器响应后将返回的业务对象转换为前台可以识别的结果
 *
 * @author vincent
 */
@Aspect
@Component
public class ResponseInterceptor {

    private ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private Validator validator = validatorFactory.getValidator();

    private static final Logger logger = LoggerFactory.getLogger(ResponseInterceptor.class);

    @Around("execution(* com.sprainkle.ueh.licence.controllerTest..*Controller.*(..))")
    public Object responseHandle(ProceedingJoinPoint pjp) {
        QR response = new QR();

        String validationResult = validateBean(pjp.getArgs());
        try {
            if (null != validationResult) {
                response.setMessage(validationResult);
                response.setData(null);
                response.setCode(ResponseEnum.INVALID.getCode());
            } else {
                Object obj = pjp.proceed();
                response.setMessage(ResponseEnum.OK.getMessage());
                response.setData(obj);
                response.setCode(ResponseEnum.OK.getCode());
            }
        } catch (Throwable throwable) {
            handleException(throwable, response);
        }

        //添加暴露前台相应 add lixc 2017年11月14日17:25:28
        HttpServletResponse res = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getResponse();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        if (null != res) {
            res.addHeader("Access-Control-Expose-Headers", "Authorization");
            if (null != request) {
                Enumeration<String> enu = request.getHeaderNames();//取得全部头信息
                while (enu.hasMoreElements()) {//以此取出头信息
                    String headerName = (String) enu.nextElement();
                    if (headerName.equals("accept-sucker")) { //判断请求是不是来自app
                        return JsonUtil.convertResponseToJson(response).replace("null", "\"\"");  //app请求头唯一标识(Accept-sucker:sucker),过滤掉返回json中所有的null值
                    }
                }
            }
        }
        return JsonUtil.convertResponseToJson(response);
    }

    /**
     * 查看参数列表中的参数是否需要bean校验，如果需要，则进行校验，并返回错误信息
     *
     * @param objects 参数列表
     * @return null表示较验正常，否则返回第一个校验的错误
     */
    @SuppressWarnings("rawtypes")
    private String validateBean(Object[] objects) {
        if (null == objects || objects.length == 0) {
            return null;
        }

        String message = null;

        for (Object obj : objects) {
            if (null == obj) {
                continue;
            }

            //遍历参数列表，查看参数是否被ValidationBean声明，
            //如果是，说明该参数需要bean校验
            int length = obj.getClass().getAnnotationsByType(ValidationBean.class).length;
            if (length > 0) {
                //HashSet保证实体bean中属性校验的顺序！
                HashSet<ConstraintViolation<Object>> validateResults = (HashSet<ConstraintViolation<Object>>) validator.validate(obj, new Class[0]);
                if (!validateResults.isEmpty()) {
                    for (ConstraintViolation result : validateResults) {
                        message = result.getMessage();
                        break;
                    }
                }
            }
        }
        return message;
    }

    /**
     * 处理异常
     *
     * @param throwable 异常
     * @param response  结果
     */
    public static void handleException(Throwable throwable, QR response) {
        throwable.printStackTrace();
        logger.error(throwable.getMessage());
        if (throwable.getClass().equals(BaseException.class)) {
            Object[] args = ((BaseException) throwable).getArgs();
            response.setMessage(args == null ? null : args[0] + "");
            response.setData("");
            response.setCode(ResponseEnum.LOGIC_EXCEPTION.getCode());
        } else {
            response.setMessage(ResponseEnum.ERROR.getMessage());
            response.setData("");
            response.setCode(ResponseEnum.ERROR.getCode());
        }
    }
}