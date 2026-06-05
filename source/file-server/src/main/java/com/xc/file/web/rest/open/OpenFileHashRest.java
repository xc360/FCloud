package com.xc.file.web.rest.open;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xc.api.basic.BasicApi;
import com.xc.api.file.enums.FileRestCode;
import com.xc.core.aspect.BasicConstants;
import com.xc.core.bean.SignBean;
import com.xc.file.dto.FileHashDto;
import com.xc.file.entity.FileHashEntity;
import com.xc.file.service.FileHashService;
import com.xc.tool.utils.ObjectUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>【开放】文件hash管理</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Api(tags = "【开放】文件hash管理")
@RestController
public class OpenFileHashRest {
    @Autowired
    private FileHashService fileHashService;
    @Autowired
    private BasicConstants basicConstants;
    @Autowired
    private BasicApi basicApi;

    @ApiOperation(value = "获取文件hash集合", notes = "获取文件hash集合")
    @GetMapping(value = "/open/file_hash_list")
    public List<FileHashDto> getOpenFileHashList(@ModelAttribute SignBean signBean) {
        signBean.setMyAppId(basicConstants.getAppId());
        signBean.setAuthorityCode(FileRestCode.getOpenFileHashList.getCode());
        basicApi.verifySign(signBean);
        List<FileHashEntity> entities = fileHashService.list(new QueryWrapper<>(new FileHashEntity()));
        return ObjectUtils.convertList(entities, FileHashDto::new);
    }
}
