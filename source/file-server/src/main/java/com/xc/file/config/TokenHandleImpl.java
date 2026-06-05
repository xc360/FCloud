package com.xc.file.config;

import com.xc.api.basic.BasicApi;
import com.xc.api.basic.enums.BasicRestCode;
import com.xc.core.aspect.BasicConstants;
import com.xc.core.aspect.TokenHandle;
import com.xc.core.bean.SignBean;
import com.xc.core.dto.TokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>token处理实现</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Component
public class TokenHandleImpl implements TokenHandle {
    @Autowired
    private BasicConstants basicConstants;
    @Autowired
    private BasicApi basicApi;

    @Override
    public TokenDto getToken(String accessToken) {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.getOpenToken.getCode());
        return basicApi.getOpenToken(signBean, null, accessToken);
    }

    @Override
    public TokenDto updateOpenToken(String refreshToken) {
        SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.updateOpenToken.getCode());
        return basicApi.updateOpenToken(signBean, refreshToken);
    }
}
