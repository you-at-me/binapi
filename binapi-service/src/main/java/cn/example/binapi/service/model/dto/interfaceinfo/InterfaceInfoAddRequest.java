package cn.example.binapi.service.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求，DTO 层，也是属于 Model 模型里的一个，只不过 Entity 实体是最全的，而这里的请求参数实体只是需要传递的请求参数实体，只是 Entity 实体的一部分。一般我们这里创建的请求，无论是增、查、改，都得分开来写，分三个对象调用，一是为了维护前后端能够很好交互，二是不至于出现传输null值的数据给前端，浪费带宽
 *
 * @TableName product
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    private static final long serialVersionUID = 1L;
}