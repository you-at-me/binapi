package cn.example.binapi.service.config;

import cn.example.binapi.service.utils.IdGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.stereotype.Component;

/**
 * 实现 MybatisPlus 的 ID生成器接口，重写其方法
 */
@Component
public class CustomerIdGenerator implements IdentifierGenerator {

    @Override
    public Long nextId(Object entity) {
        // 填充自己的Id生成器，
        return IdGenerator.generateId();
    }
}