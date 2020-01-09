package com.sprainkle.ueh.licence.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sprainkle.spring.cloud.advance.common.core.pojo.param.QueryPage;
import com.sprainkle.spring.cloud.advance.common.core.pojo.response.QueryData;
import com.sprainkle.spring.cloud.advance.common.core.util.ClientUtil;
import com.sprainkle.spring.cloud.advance.proto.licence.param.LicenceParam;
import com.sprainkle.spring.cloud.advance.proto.licence.request.LicenceAddRequest;
import com.sprainkle.spring.cloud.advance.proto.licence.response.LicenceAddRespData;
import com.sprainkle.spring.cloud.advance.proto.licence.response.LicenceDTO;
import com.sprainkle.spring.cloud.advance.proto.licence.response.SimpleLicenceDTO;
import com.sprainkle.spring.cloud.advance.proto.organization.response.OrganizationDTO;
import com.sprainkle.ueh.licence.client.OrganizationClient;
import com.sprainkle.ueh.licence.constant.LicenceTypeEnum;
import com.sprainkle.ueh.licence.constant.ResponseEnum;
import com.sprainkle.ueh.licence.entity.Licence;
import com.sprainkle.ueh.licence.mapper.LicenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <pre>
 *
 * </pre>
 *
 * @author sprainkle
 * @date 2019/5/3
 */
@Service
public class LicenceService extends ServiceImpl<LicenceMapper, Licence> {

    @Autowired
    private OrganizationClient organizationClient;



    /**
     *      多条事务 全局异常 回滚
     *      如果注解上只写 @Transactional  默认只对 RuntimeException 回滚，而非 Exception 进行回滚;
     *  如果要对 checked Exceptions 进行回滚，则需要 @Transactional(rollbackFor = Exception.class)
     *
     *      springboot全局异常处理(包含404错误处理) https://blog.csdn.net/q_linchao/article/details/80763353
     */
    @Transactional(rollbackFor = Throwable.class)
    public LicenceAddRespData addLicence2(LicenceAddRequest request) {
        // 省略校验参数
        Licence licence = new Licence();
        licence.setOrganizationId(request.getOrganizationId());
        licence.setLicenceType(request.getLicenceType());
        licence.setProductName(request.getProductName());
        licence.setLicenceMax(request.getLicenceMax());
        licence.setLicenceAllocated(request.getLicenceAllocated());
        licence.setComment(request.getComment());
        this.save(licence);

        //自定义异常BaseException回滚
//        if (userInfoVo != null) {
//            //这里假设抛个自定义异常,返回结果{code:10228 msg:"用户存在！"}
//            throw new BaseException(ResultEnum.USER_EXIST);
//        }

        // 第二个save2出现异常，全局异常回滚，从而上一级别的save不会做持久化操作！
//        this.save2(licence);

        return new LicenceAddRespData(licence.getLicenceId());
    }



    /**
     * 新增{@link Licence}
     * @param request 请求体
     * @return
     */
    @Transactional(rollbackFor = Throwable.class)
    public LicenceAddRespData addLicence(LicenceAddRequest request) {
        // 省略校验参数
        Licence licence = new Licence();
        licence.setOrganizationId(request.getOrganizationId());
        licence.setLicenceType(request.getLicenceType());
        licence.setProductName(request.getProductName());
        licence.setLicenceMax(request.getLicenceMax());
        licence.setLicenceAllocated(request.getLicenceAllocated());
        licence.setComment(request.getComment());
        this.save(licence);

        return new LicenceAddRespData(licence.getLicenceId());
    }


    /**
     * 查询{@link Licence} 详情
     * @param licenceId
     * @return
     */
    public LicenceDTO queryDetail(Long licenceId) {
        Licence licence = this.getById(licenceId);
        // 校验非空
        ResponseEnum.LICENCE_NOT_FOUND.assertNotNull(licence);

        OrganizationDTO org = ClientUtil.execute(() -> organizationClient.getOrganization(licence.getOrganizationId()));
        return toLicenceDTO(licence, org);
    }

    /**
     * 分页获取
     * @param licenceParam 分页查询参数
     * @return
     */
    public QueryData<SimpleLicenceDTO> getLicences(LicenceParam licenceParam) {
        String licenceType = licenceParam.getLicenceType();
        LicenceTypeEnum licenceTypeEnum = LicenceTypeEnum.parseOfNullable(licenceType);
        // 断言, 非空
        ResponseEnum.BAD_LICENCE_TYPE.assertNotNull(licenceTypeEnum);

        LambdaQueryWrapper<Licence> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Licence::getLicenceType, licenceType);
        IPage<Licence> page = this.page(new QueryPage<>(licenceParam), wrapper);
        return new QueryData<>(page, this::toSimpleLicenceDTO);
    }


    /**
     * entity -> simple dto
     * @param licence {@link Licence} entity
     * @return {@link SimpleLicenceDTO}
     */
    private SimpleLicenceDTO toSimpleLicenceDTO(Licence licence) {
        SimpleLicenceDTO simpleLicenceDTO= new SimpleLicenceDTO();
        simpleLicenceDTO.setLicenceId(String.valueOf(licence.getLicenceId()));
        simpleLicenceDTO.setOrganizationId(String.valueOf(licence.getOrganizationId()));
        simpleLicenceDTO.setLicenceType(licence.getLicenceType());
        simpleLicenceDTO.setProductName(licence.getProductName());
        simpleLicenceDTO.setLicenceMax(licence.getLicenceMax());
        simpleLicenceDTO.setLicenceAllocated(licence.getLicenceAllocated());
        simpleLicenceDTO.setComment(licence.getComment());
        return simpleLicenceDTO;
    }

    /**
     * entity -> dto
     * @param licence {@link Licence} entity
     * @param org {@link OrganizationDTO}
     * @return {@link LicenceDTO}
     */
    private LicenceDTO toLicenceDTO(Licence licence, OrganizationDTO org) {
        LicenceDTO licenceDTO= new LicenceDTO();
        licenceDTO.setLicenceId(String.valueOf(licence.getLicenceId()));
        licenceDTO.setOrganizationId(String.valueOf(licence.getOrganizationId()));
        licenceDTO.setOrganizationName(org.getName());
        licenceDTO.setContactName(org.getContactName());
        licenceDTO.setContactEmail(org.getContactEmail());
        licenceDTO.setContactPhone(org.getContactPhone());
        licenceDTO.setLicenceType(licence.getLicenceType());
        licenceDTO.setProductName(licence.getProductName());
        licenceDTO.setLicenceMax(licence.getLicenceMax());
        licenceDTO.setLicenceAllocated(licence.getLicenceAllocated());
        licenceDTO.setComment(licence.getComment());
        return licenceDTO;
    }

    /**
     * 校验{@link Licence}存在
     * @param licence
     */
    private void checkNotNull(Licence licence) {

    }

}
