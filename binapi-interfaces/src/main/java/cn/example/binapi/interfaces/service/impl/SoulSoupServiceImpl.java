package cn.example.binapi.interfaces.service.impl;


import cn.example.binapi.common.model.entity.SoulSoup;
import cn.example.binapi.interfaces.mapper.SoulSoupMapper;
import cn.example.binapi.interfaces.service.SoulSoupService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 针对表(soul_soup(心灵鸡汤表))的数据库操作Service实现
 */
@Service
public class SoulSoupServiceImpl extends ServiceImpl<SoulSoupMapper, SoulSoup> implements SoulSoupService {

    @Override
    public String getRandom() {
        QueryWrapper<SoulSoup> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("rand()").last("limit 1");
        SoulSoup soulSoup = this.getOne(queryWrapper);
        return soulSoup.getContent();
    }
}




