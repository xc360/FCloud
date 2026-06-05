package com.xc.file.web.rest;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONObject;
import com.xc.api.basic.BasicApi;
import com.xc.api.basic.bean.PasswordBean;
import com.xc.api.basic.bean.UpdateMailBean;
import com.xc.api.basic.bean.UpdatePhoneBean;
import com.xc.api.basic.dto.AuthorityDto;
import com.xc.api.basic.dto.DictDto;
import com.xc.api.basic.dto.GroupDto;
import com.xc.api.basic.dto.UserDto;
import com.xc.api.basic.enums.AuthorityType;
import com.xc.api.basic.enums.BasicRestCode;
import com.xc.core.annotation.Authority;
import com.xc.core.aspect.AuthorityHandle;
import com.xc.core.aspect.BasicConstants;
import com.xc.core.bean.SignBean;
import com.xc.core.enums.CoreFailCode;
import com.xc.core.enums.CoreRedisPrefix;
import com.xc.core.enums.Whether;
import com.xc.core.model.TokenModel;
import com.xc.core.utils.RedisUtils;
import com.xc.file.config.Constants;
import com.xc.file.config.StartSuccess;
import com.xc.file.enums.RedisPrefix;
import com.xc.file.service.BasicService;
import com.xc.file.task.BasicTask;
import com.xc.tool.utils.MemoryUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>【用户】我调用的开放接口</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Slf4j
@Api(tags = {"【用户】我调用的开放接口"})
@RestController
public class MyAppRest {
    @Autowired
    private BasicConstants basicConstants;
    @Autowired
    private BasicApi basicApi;
    @Autowired
    private Constants constants;
    @Autowired
    private BasicTask basicTask;

    @ApiOperation(value = "安装配置")
    @GetMapping("/install")
    public void install(@ModelAttribute SignBean signBean, @RequestParam Map<String, String> paramMap) {
        signBean.setAuthorityCode(BasicRestCode.install.getCode());
        signBean.getSignDataObj(basicConstants.getAppSecret(), constants.getSignValidTime(), signBean.getAuthorityCode());
        // 参数
        String refreshAuthority = paramMap.get("refreshAuthority");
        String refreshAuthorize = paramMap.get("refreshAuthorize");
        String watchMemory = paramMap.get("watchMemory");
        // 刷新token权限
        if (Whether.YES.getValue().equals(refreshAuthority)) {
            SignBean signBean1 = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.updateOpenAppTokenAuthority.getCode());
            basicApi.updateOpenAppTokenAuthority(signBean1);
            AuthorityHandle.clearToken();
        }
        // 刷新用户授权
        if (Whether.YES.getValue().equals(refreshAuthorize)) {
            SignBean signBean1 = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.updateOpenAppUserAuthorize.getCode());
            basicApi.updateOpenAppUserAuthorize(signBean1);
        }
        // 内存检测
        if (Whether.YES.getValue().equals(watchMemory)) {
            MemoryUtils.watchMemoryInfo(100000, null, log::info);
        }
    }

    @ApiOperation(value = "任务执行")
    @GetMapping("/task")
    public void install(@ModelAttribute SignBean signBean) {
        if (signBean.getAppId() == null) {
            throw CoreFailCode.SIGN_ERROR.getOperateException();
        }
        signBean.setAuthorityCode(BasicRestCode.task.getCode());
        JSONObject jsonObject = signBean.getSignDataObj(basicConstants.getAppSecret(), constants.getSignValidTime(), signBean.getAuthorityCode());
        String taskCode = jsonObject.getStr("taskCode");
        ReflectUtil.invoke(basicTask, taskCode);
    }

    @ApiOperation(value = "获取用户组集合")
    @GetMapping("/my_app/user/group_list")
    @Authority
    public List<GroupDto> getMyAppUserGroupList(TokenModel tokenModel) {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.getOpenUserGroupList.getCode());
        signBean.setUserId(tokenModel.getUserId());
        return basicApi.getOpenUserGroupList(signBean);
    }

    @ApiOperation(value = "获取菜单集合")
    @GetMapping("/my_app/menu_list")
    @Authority
    public List<AuthorityDto> getMyAppMenuList() {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.getOpenAuthorityList.getCode());
        return basicApi.getOpenAuthorityList(signBean, AuthorityType.MENU.getType());
    }

    @ApiOperation(value = "获取字典集合")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "字典类型,为null查询全部", name = "type", paramType = "query"),
    })
    @GetMapping("/my_app/dict_list")
    @Authority
    public List<DictDto> getMyAppDictList(@RequestParam(required = false) String type) {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.getOpenDictList.getCode());
        return basicApi.getOpenDictList(signBean, type);
    }

    @ApiOperation(value = "退出登录", notes = "删除当前我的应用的token信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true)
    })
    @DeleteMapping("/my_app/token")
    @Authority
    public void deleteMyAppToken(TokenModel tokenModel) {
        // 删除文件授权
        String key = RedisPrefix.USER_DOWNLOAD.getKey() + tokenModel.getUserId();
        RedisUtils.delete(key);
        // 删除本地token
        RedisUtils.delete(CoreRedisPrefix.ACCESS.getKey() + tokenModel.getAccessToken());
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.deleteOpenToken.getCode());
        basicApi.deleteOpenToken(signBean, tokenModel.getAccessToken(), null);
    }

    @ApiOperation(value = "获取用户", notes = "获取我的应用下的用户")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true)
    })
    @GetMapping("/my_app/user")
    @Authority
    public UserDto getMyAppUser(TokenModel tokenModel) {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.getOpenUser.getCode());
        signBean.setUserId(tokenModel.getUserId());
        return basicApi.getOpenUser(signBean);
    }

    @ApiOperation(value = "修改密码", notes = "修改我的应用下的用户密码")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
    })
    @PutMapping("/my_app/user/password")
    @Authority
    public UserDto updateMyAppUserPassword(TokenModel tokenModel, @RequestBody PasswordBean passwordBean) {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.updateOpenUserPassword.getCode());
        signBean.setUserId(tokenModel.getUserId());
        return basicApi.updateOpenUserPassword(signBean, passwordBean);
    }

    @ApiOperation(value = "修改用户邮箱")
    @PutMapping("/my_app/user/email")
    @Authority
    public UserDto updateMyAppUserMail(TokenModel tokenModel, @RequestBody UpdateMailBean updateMailBean) {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.updateOpenUserMail.getCode());
        signBean.setUserId(tokenModel.getUserId());
        return basicApi.updateOpenUserMail(signBean, updateMailBean);
    }

    @ApiOperation(value = "修改用户手机号")
    @PutMapping("/my_app/user/phone")
    @Authority
    public UserDto updateMyAppUserPhone(TokenModel tokenModel, @RequestBody UpdatePhoneBean updatePhoneBean) {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.updateOpenUserPhone.getCode());
        signBean.setUserId(tokenModel.getUserId());
        return basicApi.updateOpenUserPhone(signBean, updatePhoneBean);
    }

    @ApiOperation(value = "发送用户验证码")
    @PostMapping("/my_app/user_captcha/{messageCode}/{accountType}")
    @Authority
    public void createMyAppUserCaptcha(TokenModel tokenModel, @PathVariable String messageCode, @PathVariable String accountType) {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.createOpenUserCaptcha.getCode());
        signBean.setUserId(tokenModel.getUserId());
        basicApi.createOpenUserCaptcha(signBean, messageCode, accountType);
    }

    @ApiOperation(value = "获取用户验证码信息")
    @GetMapping("/my_app/user_captcha/{messageCode}")
    @Authority
    public void getMyAppUserCaptcha(TokenModel tokenModel, @PathVariable String messageCode,
                                    @RequestParam String captcha, @RequestParam String isDelete) {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.getOpenUserCaptcha.getCode());
        signBean.setUserId(tokenModel.getUserId());
        basicApi.getOpenUserCaptcha(signBean, messageCode, captcha, isDelete);
    }

    @ApiOperation(value = "获取用户集合")
    @GetMapping("/my_app/user_list")
    @Authority
    public List<Map<String, String>> getMyAppUserList() {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.getOpenUserList.getCode());
        List<UserDto> userInfos = basicApi.getOpenUserList(signBean, null);
        List<Map<String, String>> mapList = new ArrayList<>();
        for (UserDto userDto : userInfos) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", userDto.getId());
            map.put("account", userDto.getAccount());
            mapList.add(map);
        }
        return mapList;
    }

    @ApiOperation(value = "注销账户", notes = "删除用户")
    @DeleteMapping("/my_app/user")
    @Authority
    public UserDto deleteMyAppUser(TokenModel tokenModel, @RequestParam String captcha, @RequestParam String accountType) {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.deleteOpenUser.getCode());
        signBean.setUserId(tokenModel.getUserId());
        return basicApi.deleteOpenUser(signBean, captcha, accountType);
    }
}
