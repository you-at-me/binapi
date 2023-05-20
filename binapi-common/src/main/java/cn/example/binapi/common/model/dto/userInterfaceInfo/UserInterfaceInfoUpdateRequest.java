package cn.example.binapi.common.model.dto.userInterfaceInfo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户接口关系表请求更新实体类
 */
@Data
public class UserInterfaceInfoUpdateRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 0-禁用，1-正常
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}