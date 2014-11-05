/**
 * 
 */
package com.hehua.framework.codec;

/**
 * @author zhihua
 *
 */
public interface Decoder<S, T> {

    public S decode(T object);
}
