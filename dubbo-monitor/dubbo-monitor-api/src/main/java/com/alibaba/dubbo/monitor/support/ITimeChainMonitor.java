package com.alibaba.dubbo.monitor.support;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhangmin on 2016/11/7.
 */
public interface ITimeChainMonitor {
    void collect(Invoker<?> invoker, Invocation invocation, Result result, RpcContext context, long start, ConcurrentMap<String, AtomicInteger> concurrents);
}
