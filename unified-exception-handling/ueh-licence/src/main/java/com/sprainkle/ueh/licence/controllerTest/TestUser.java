package com.sprainkle.ueh.licence.controllerTest;

import com.sprainkle.ueh.licence.aspect.ValidationBean;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
@ValidationBean
public class TestUser {

    @NotEmpty(message = "ID不能为空")
    private String userId;
    @NotEmpty(message = "编号不能为空")
    private String userNo;

//    @Length(min = 4,max = 20,message = "必须在[4,20]" )
//    @Range(min = 0,max = 100,message = "年龄必须在[0,100]" )
//    @Range(min = 0,max = 2,message = "性别必须在[0,2]" )
    @Min(value = 18, message = "年龄最小为18岁")
    @Max(value = 30, message = "年龄最大为30岁")
    private Integer age;

}