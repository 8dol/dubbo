package com.alibaba.com.caucho.hessian.io.java8;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.alibaba.com.caucho.hessian.io.SerializerFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by mind on 9/16/15.
 */
public class PageableSerializerTest {
    private static SerializerFactory factory;
    private static ByteArrayOutputStream os;

    @BeforeClass
    public static void setUp() {
        factory = new SerializerFactory(Thread.currentThread().getContextClassLoader());
        os = new ByteArrayOutputStream();
    }

    @Test
    public void testPageableInterface() throws IOException {
        testPageable(new PageRequest(1, 2));
    }

    private void testPageable(Pageable expected) throws IOException {
        os.reset();

        Hessian2Output output = new Hessian2Output(os);
        output.setSerializerFactory(factory);
        output.writeObject(expected);
        output.flush();

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        Hessian2Input input = new Hessian2Input(is);
        input.setSerializerFactory(factory);
        Object actual = input.readObject();

        Assert.assertEquals(expected, actual);
    }
}
