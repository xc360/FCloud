package com.xc.file.web.rest;

import com.xc.core.annotation.Authority;
import com.xc.core.bean.PagingBean;
import com.xc.core.dto.PagingDto;
import com.xc.core.model.TokenModel;
import com.xc.file.bean.SafetyChainBean;
import com.xc.file.dto.SafetyChainDto;
import com.xc.file.service.DiskService;
import com.xc.file.service.SafetyChainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>【用户】安全链接</p>
 *
 * @author xc
 * @version v1.0
 */
@Api(tags = "【用户】安全链接")
@RestController
public class SafetyChainRest {

    @Autowired
    private SafetyChainService safetyChainService;
    @Autowired
    private DiskService diskService;

    @ApiOperation(value = "安全链接分页", notes = "获取当前磁盘的安全链接页")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "当前页", name = "current", paramType = "path", required = true),
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @GetMapping("/disk/{diskId}/safety_chain_pages/{current}")
    @Authority
    public PagingDto<SafetyChainDto> getDiskSafetyChainPage(TokenModel tokenModel, @PathVariable String diskId, @PathVariable Integer current,
                                                            @ModelAttribute PagingBean pagingBean, @ModelAttribute SafetyChainBean safetyChainBean) {
        diskService.verifyUserDisk(tokenModel, diskId);
        return safetyChainService.getSafetyChainPage(diskId, current, pagingBean, safetyChainBean);
    }

    @ApiOperation(value = "创建安全链接", notes = "创建当前磁盘的安全链接")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @PostMapping("/disk/{diskId}/safety_chain")
    @Authority
    public SafetyChainDto createDiskSafetyChain(TokenModel tokenModel, @PathVariable String diskId, @RequestBody SafetyChainBean safetyChainBean) {
        diskService.verifyUserDisk(tokenModel, diskId);
        return safetyChainService.createSafetyChain(diskId, safetyChainBean);
    }

    @ApiOperation(value = "修改安全链接", notes = "修改当前磁盘的安全链接")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "安全链接id", name = "safetyChainId", paramType = "path", required = true),
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @PutMapping("/disk/{diskId}/safety_chain/{safetyChainId}")
    @Authority
    public SafetyChainDto updateDiskSafetyChain(TokenModel tokenModel, @PathVariable String diskId, @PathVariable String safetyChainId,
                                                @RequestBody SafetyChainBean safetyChainBean) {
        diskService.verifyUserDisk(tokenModel, diskId);
        return safetyChainService.updateSafetyChain(diskId, safetyChainId, safetyChainBean);
    }

    @ApiOperation(value = "删除安全链接", notes = "删除当前磁盘的安全链接")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "安全链接id", name = "safetyChainId", paramType = "path", required = true),
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @DeleteMapping("/disk/{diskId}/safety_chain/{safetyChainId}")
    @Authority
    public void deleteDiskSafetyChain(TokenModel tokenModel, @PathVariable String diskId, @PathVariable String safetyChainId) {
        diskService.verifyUserDisk(tokenModel, diskId);
        safetyChainService.deleteSafetyChain(diskId, safetyChainId);
    }
}
