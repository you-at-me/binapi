package cn.example.binapi.common.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口调用请求
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户请求参数
     * [
     *   {"name": "username", "type": "String"}
     * ]
     */
    private String requestParams;

    /**
     * 请求要调用的接口地址
     */
    private String url;

    /**
     * 请求方法
     */
    private String method;

    private static final long serialVersionUID = 1L;
}
