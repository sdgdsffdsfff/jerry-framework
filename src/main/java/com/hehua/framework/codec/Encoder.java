/**
 * 
 */
package com.hehua.framework.codec;

/**
 * @author zhihua
 *
 */
public interface Encoder<S, T> {

    public T encode(S object);
}
