package cn.example.binapi.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户接口关系表，供管理员查看的
 *
 * @TableName user_interface_info
 */
@Data
public class UserInterfaceInfo implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 调用用户 id
     */
    private Long userId;

    /**
     * 接口 id
     */
    private Long interfaceInfoId;

    /**
     * 每个用户操作该接口还剩余的调用次数
     */
    private Integer leftNum;

    /**
     * 每个用户操作该接口的总调用次数
     */
    private Integer totalNum;

    /**
     * 0-禁用，1-正常
     */
    private Integer status;

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

    /**
     * 是否删除(0-未删, 1-已删)
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}