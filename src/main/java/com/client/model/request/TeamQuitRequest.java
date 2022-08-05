package com.client.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @ClassName: TeamQuitRequest
 * @author: mafangnian
 * @date: 2023/5/27 17:12
 */
@Data
public class TeamQuitRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 5882957788292228746L;

    /**
     * id
     */
    private Long teamId;
}
