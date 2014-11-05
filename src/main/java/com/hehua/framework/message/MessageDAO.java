/**
 * 
 */
package com.hehua.framework.message;

import java.util.List;

import javax.inject.Named;

import org.apache.ibatis.annotations.Select;

/**
 * @author zhihua
 *
 */
@Named
public interface MessageDAO {

    @Select("select `id`, `code`, `message`, `comment` from `message`")
    public List<Message> getAll();
}
