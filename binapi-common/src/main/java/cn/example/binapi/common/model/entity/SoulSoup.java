package cn.example.binapi.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 心灵鸡汤表
 *
 * @TableName soul_soup
 */
@Data
public class SoulSoup implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 鸡汤内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除，0-未删除，1-已删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}