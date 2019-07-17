package com.sprainkle.ueh.licence.constant;

import com.sprainkle.spring.cloud.advance.common.core.exception.assertion.BusinessExceptionAssert;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>返回结果</p>
 *
 * @author sprainkle
 * @date 2018.09.17
 */
@Getter
@AllArgsConstructor
public enum ResponseEnum implements BusinessExceptionAssert {

    /**
     *
     */
    BAD_LICENCE_TYPE(7001, "Bad licence type."),
    /**
     *
     */
    LICENCE_NOT_FOUND(7002, "Licence not found."),

    //正常
    OK(20000, "操作成功"),

    //后台错误
    ERROR(50000, "操作失败，请稍后重试"),

    //后台验证失败
    INVALID(50001, "VALIDATION_ERROR"),

    //业务逻辑异常
    LOGIC_EXCEPTION(50002, "LOGIC_EXCEPTION");

    /**
     * 返回码
     */
    private int code;
    /**
     * 返回消息
     */
    private String message;
}
