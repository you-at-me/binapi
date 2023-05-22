package cn.example.binapi.common.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class InterfacePurchaseRequest implements Serializable {
    /**
     * 接口 id
     */
    private Long id;

    /**
     * 接口购买次数
     */
    private int purchaseNum;

    private static final long serialVersionUID = 1L;
}