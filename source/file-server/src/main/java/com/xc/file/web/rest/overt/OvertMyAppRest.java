package com.xc.file.web.rest.overt;

import com.xc.api.basic.BasicApi;
import com.xc.api.basic.bean.CaptchaBean;
import com.xc.api.basic.dto.AppDto;
import com.xc.api.basic.dto.InfoDto;
import com.xc.api.basic.enums.BasicRestCode;
import com.xc.api.basic.enums.InfoType;
import com.xc.core.aspect.BasicConstants;
import com.xc.core.bean.SignBean;
import com.xc.core.dto.ImageCaptchaDto;
import com.xc.core.dto.TokenDto;
import com.xc.core.enums.CoreRedisPrefix;
import com.xc.core.model.ClientIpModel;
import com.xc.core.utils.RedisUtils;
import com.xc.file.config.Constants;
import com.xc.file.entity.DiskEntity;
import com.xc.file.service.DiskService;
import com.xc.tool.utils.JSONUtils;
import com.xc.tool.utils.ObjectUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>【公开】我调用的开放接口</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Api(tags = {"【公开】我调用的开放接口"})
@RestController
public class OvertMyAppRest {
    @Autowired
    private BasicConstants basicConstants;
    @Autowired
    private BasicApi basicApi;
    @Autowired
    private DiskService diskService;
    @Autowired
    private Constants constants;

    @ApiOperation(value = "获取应用信息")
    @GetMapping("/my_app")
    public AppDto getMyApp() {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.getOpenApp.getCode());
        return basicApi.getOpenApp(signBean);
    }

    @ApiOperation(value = "获取参数信息")
    @GetMapping("/my_app/info")
    public List<InfoDto> getMyAppInfo() {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.getOpenInfoList.getCode());
        return basicApi.getOpenInfoList(signBean, InfoType.CLIENT_INFO.getType());
    }

    @ApiOperation(value = "获取token")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "授权code", name = "code", paramType = "query", required = true),
    })
    @GetMapping("/my_app/token")
    public TokenDto getMyAppToken(@RequestParam(required = false) String code, @RequestParam(required = false) String accessToken) {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.getOpenToken.getCode());
        TokenDto tokenDto = basicApi.getOpenToken(signBean, code, accessToken);
        // 创建本地用户信息
        List<DiskEntity> entities = diskService.getDiskByUserId(tokenDto.getUserId());
        if (entities.size() == 0) {
            DiskEntity entity = new DiskEntity();
            entity.setName("我的磁盘");
            entity.setUserId(tokenDto.getUserId());
            entity.setSignValidTime(constants.getSignValidTime());
            diskService.createDisk(entity);
        }
        RedisUtils.set(CoreRedisPrefix.ACCESS.getKey() + tokenDto.getAccessToken(), JSONUtils.getStringByObject(tokenDto), tokenDto.getValidTime());
        return tokenDto;
    }

    @ApiOperation(value = "刷新token信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "刷新token", name = "refreshToken", paramType = "path", required = true),
    })
    @PutMapping("/my_app/token/{refreshToken}")
    public TokenDto updateMyAppToken(@PathVariable String refreshToken) {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.updateOpenToken.getCode());
        TokenDto tokenDto = basicApi.updateOpenToken(signBean, refreshToken);
        RedisUtils.set(CoreRedisPrefix.ACCESS.getKey() + tokenDto.getAccessToken(), JSONUtils.getStringByObject(tokenDto), tokenDto.getValidTime());
        return ObjectUtils.convert(new TokenDto(), tokenDto);
    }

    @ApiOperation(value = "发送验证码")
    @PostMapping("/my_app/captcha/{messageCode}/{accountType}")
    public void createMyAppCaptcha(ClientIpModel clientIpModel, @PathVariable String messageCode, @PathVariable String accountType, @RequestBody CaptchaBean captchaBean) {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.createOpenCaptcha.getCode());
        captchaBean.setClientIp(clientIpModel.getClientIp());
        basicApi.createOpenCaptcha(signBean, messageCode, accountType, captchaBean);
    }

    @ApiOperation(value = "获取验证码信息")
    @GetMapping("/my_app/captcha/{messageCode}")
    public void getMyAppCaptcha(@PathVariable String messageCode, @RequestParam String account,
                                @RequestParam String captcha, @RequestParam String isDelete) {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.getOpenCaptcha.getCode());
        basicApi.getOpenCaptcha(signBean, account, messageCode, captcha, isDelete);
    }

    @ApiOperation(value = "获取图片验证码")
    @GetMapping("/my_app/img_captcha")
    public ImageCaptchaDto getMyAppImgCaptcha() {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.getOpenImgCaptcha.getCode());
        return basicApi.getOpenImgCaptcha(signBean, null);
    }
}
