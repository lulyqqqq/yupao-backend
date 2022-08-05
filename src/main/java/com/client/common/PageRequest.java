package com.client.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @ClassName: PageRequest
 * @author: mafangnian
 * @date: 2023/5/26 20:38
 * 通用分页请求参数
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 8688727081583881563L;

    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前第几页
     */
    protected int pageNum = 1;
}
