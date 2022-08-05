package com.client.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @ClassName: TeamJoinRequest
 * @author: mafangnian
 * @date: 2023/5/27 14:13
 */
@Data
public class TeamJoinRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -681410795620326967L;

    /**
     * id
     */
    private Long teamId;

    /**
     * password
     */
    private String password;
}
