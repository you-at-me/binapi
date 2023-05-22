package cn.example.binapi.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 一言语录实体类
 * @author Carl
 * @since 2023-05-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OneWord implements Serializable {

    /**
     * 不是必填，可选择输出分类[hitokoto|en|social|soup|fart|zha]，为空默认hitokoto
     */
    private String type;

    /**
     * 不是必填，选择输出格式[json|js]
     */
    private String code;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
