/***
 * @pName management
 * @name DictionaryMapper
 * @user HongWei
 * @date 2018/8/16
 * @desc
 */
package com.management.admin.repository;

import com.management.admin.entity.db.Dictionary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface DictionaryMapper extends MyMapper<Dictionary> {
    @Select("SELECT * FROM dictionarys")
    /**
     * 查询全部
     * @return
     */
    List<Dictionary> selectAll();

    @Select("SELECT * FROM dictionarys WHERE `key` LIKE '${likeKey}%'")
    /**
     * 模糊查询  2018年8月18日13:14:02
     * @param likeKey
     * @return
     */
    List<Dictionary> selectByKey(@Param("likeKey") String likeKey);

    @Update("UPDATE dictionarys SET `value`=#{url} WHERE dictionary_id=#{dictionaryId}")
    int updateUrlById(@Param("dictionaryId") Integer dictionaryId, @Param("url") String url);


    /**
     * 根据key修改value
     * @param key
     * @param value
     * @return
     */
    @Update("UPDATE dictionarys SET `value`=#{value} WHERE `key`=#{key}")
    int updateUrlByKey(@Param("key") String key, @Param("value") String value);


}
