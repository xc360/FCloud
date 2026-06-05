package com.xc.file.function;

import java.util.List;

/**
 * <p>目录处理</p>
 *
 * @author xc
 * @version v1.0.0
 */
public interface FolderHandle<T> {

    /**
     * 复制处理目录回调
     *
     * @param folders       目录集合
     * @param parentNewNode 父级新节点
     */
    public void handle(List<T> folders, String parentNewNode);

}
