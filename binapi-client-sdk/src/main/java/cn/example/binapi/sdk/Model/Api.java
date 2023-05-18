package cn.example.binapi.sdk.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Carl
 * @since 2023-05-17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Api implements Serializable {

    /**
     * 用户id
     */
    Long id;

    /**
     * 用户账号
     */
    String account;

    /**
     * 接口id
     */
    String interfaceId;

    /**
     * 接口地址
     */
    String url;

    /**
     * 请求体
     */
    Object body;

    /**
     * 请求方法
     */
    String method;
}
