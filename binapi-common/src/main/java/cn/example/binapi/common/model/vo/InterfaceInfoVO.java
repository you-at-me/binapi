package cn.example.binapi.common.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息
 * @TableName interface_info
 */
@Data
public class InterfaceInfoVO implements Serializable {
    /**
     * 主键
     */
    private Long id;

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
     * 请求参数
     */
    private String requestParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 计费规则(元/条)
     */
    private Float price;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    private Integer status;

    /**
     * 剩余次数
     */
    @TableField(exist = false)
    private Integer leftNum;

    /**
     * 总调用次数
     */
    @TableField(exist = false)
    private Integer totalNum;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 创建时间
     */
    @TableField
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}