package com.client.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: DeleteRequest
 * @author: mafangnian
 * @date: 2023/5/29 18:14
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 8127634838419383868L;

    private long id;
}
