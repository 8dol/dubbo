/*
 * Copyright 1999-2011 Alibaba Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.alibaba.dubbo.rpc.protocol.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.Codec2;
import com.alibaba.dubbo.remoting.buffer.ChannelBuffer;
import com.alibaba.dubbo.remoting.exchange.Request;
import com.alibaba.dubbo.remoting.exchange.Response;
import com.alibaba.dubbo.remoting.exchange.support.MultiMessage;
import com.alibaba.dubbo.rpc.RpcInvocation;
import com.alibaba.dubbo.rpc.RpcResult;

import java.io.IOException;

/**
 * @author <a href="mailto:gang.lvg@alibaba-inc.com">kimi</a>
 */
public final class DubboCountCodec implements Codec2 {
    private static final Logger logger = LoggerFactory.getLogger(DubboCountCodec.class);

    private DubboCodec codec = new DubboCodec();

    public void encode(Channel channel, ChannelBuffer buffer, Object msg) throws IOException {
        long start = System.currentTimeMillis();
        codec.encode(channel, buffer, msg);

        if (logger.isInfoEnabled()) {
            if (msg instanceof Request) {
                Request req = (Request) msg;

                if (!req.isEvent()) {
                    logger.info("[Codec:" + req.getId() + ":" + channel.getLocalAddress() + "]请求编码.耗时=" + (System.currentTimeMillis() - start));
                }
            } else if (msg instanceof Response) {
                Response res = (Response) msg;
                if (!res.isHeartbeat()) {
                    logger.info("[Codec:" + res.getId() + ":" + channel.getRemoteAddress() + "]响应编码.耗时=" + (System.currentTimeMillis() - start));
                }
            }
        }
    }

    public Object decode(Channel channel, ChannelBuffer buffer) throws IOException {
        int save = buffer.readerIndex();
        MultiMessage result = MultiMessage.create();
        do {
            long start = System.currentTimeMillis();
            Object obj = codec.decode(channel, buffer);
            if (Codec2.DecodeResult.NEED_MORE_INPUT == obj) {
                buffer.readerIndex(save);
                break;
            } else {
                result.addMessage(obj);
                logMessageLength(obj, buffer.readerIndex() - save);

                if (logger.isInfoEnabled()) {
                    if (obj instanceof Request) {
                        Request req = (Request) obj;

                        if (!req.isEvent()) {
                            String inputSize = ((RpcInvocation) req.getData()).getAttachment(Constants.INPUT_KEY);

                            logger.info("[Codec:" + req.getId() + ":" + channel.getRemoteAddress() + "]请求解码.耗时=" + (System.currentTimeMillis() - start) + ", 长度=" + inputSize);
                        }
                    } else if (obj instanceof Response) {
                        Response res = (Response) obj;
                        if (!res.isHeartbeat()) {
                            String outputSize = ((RpcResult) res.getResult()).getAttachment(Constants.OUTPUT_KEY);
                            logger.info("[Codec:" + res.getId() + ":" + channel.getLocalAddress() + "]响应解码.耗时=" + (System.currentTimeMillis() - start) + ", 长度=" + outputSize);
                        }
                    }
                }
                save = buffer.readerIndex();
            }
        } while (true);
        if (result.isEmpty()) {
            return Codec2.DecodeResult.NEED_MORE_INPUT;
        }
        if (result.size() == 1) {
            return result.get(0);
        }
        return result;
    }

    private void logMessageLength(Object result, int bytes) {
        if (bytes <= 0) {
            return;
        }
        if (result instanceof Request) {
            try {
                ((RpcInvocation) ((Request) result).getData()).setAttachment(
                        Constants.INPUT_KEY, String.valueOf(bytes));
            } catch (Throwable e) {
                /* ignore */
            }
        } else if (result instanceof Response) {
            try {
                ((RpcResult) ((Response) result).getResult()).setAttachment(
                        Constants.OUTPUT_KEY, String.valueOf(bytes));
            } catch (Throwable e) {
                /* ignore */
            }
        }
    }

}
