package com.client.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @ClassName: DeleteRequest
 * @author: mafangnian
 * @date: 2023/5/29 18:14
 */
@Data
public class DeleteRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 2004855267886403308L;

    private long id;
}
