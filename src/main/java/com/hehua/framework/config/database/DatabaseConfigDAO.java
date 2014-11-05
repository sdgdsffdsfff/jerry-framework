/**
 * 
 */
package com.hehua.framework.config.database;

import java.util.List;

import javax.inject.Named;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author zhihua
 *
 */
@Named
public interface DatabaseConfigDAO {

    @Select("select `id`, `name`, `value` from `config`")
    public List<DatabaseConfig> getAll();

    @Update("insert into `config` (`name`, `value`) values (#{name}, #{value}) on duplicate key update `value` = values(`value`)")
    public int update(@Param("name") String name, @Param("value") String value);
}
