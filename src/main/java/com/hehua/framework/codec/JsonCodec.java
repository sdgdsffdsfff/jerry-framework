/**
 * 
 */
package com.hehua.framework.codec;

import com.alibaba.fastjson.JSON;

/**
 * @author zhihua
 *
 */
public class JsonCodec<T> implements Codec<T, String> {

    private Class<T> clazz;

    /**
     * @param clazz
     */
    public JsonCodec(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public String encode(T object) {
        return JSON.toJSONString(object);
    }

    @Override
    public T decode(String object) {
        return JSON.parseObject(object, clazz);
    }

}
