package com.alibaba.com.caucho.hessian.io.spring;

import com.alibaba.com.caucho.hessian.io.JavaDeserializer;
import org.springframework.data.domain.PageRequest;

/**
 * Created by mind on 9/16/15.
 */
public class PageableDeserializer extends JavaDeserializer {
    public PageableDeserializer() {
        super(PageRequest.class);
    }

    @Override
    protected Object instantiate() throws Exception {
        return new PageRequest(0, 10);
    }
}