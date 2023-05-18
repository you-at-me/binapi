package cn.example.binapi.interfaces.service;

import cn.example.binapi.common.model.entity.SoulSoup;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 针对表(soul_soup(心灵鸡汤表))的数据库操作Service
 */
public interface SoulSoupService extends IService<SoulSoup> {

    /**
     * 随机查找一条心灵鸡汤
     */
    String getRandom();

}
