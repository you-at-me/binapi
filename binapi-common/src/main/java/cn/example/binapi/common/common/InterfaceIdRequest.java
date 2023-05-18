package cn.example.binapi.common.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class InterfaceIdRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}