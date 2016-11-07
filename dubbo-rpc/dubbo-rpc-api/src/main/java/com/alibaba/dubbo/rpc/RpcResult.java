/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.rpc;

import com.alibaba.dubbo.common.utils.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * RPC Result.
 * 
 * @serial Don't change the class name and properties.
 * @author qianlei
 */
public class RpcResult implements Result, Serializable {

    private static final long        serialVersionUID = -6925924956850004727L;

    private Object                   result;

    private Throwable                exception;

    private Map<String, String>      attachments = new HashMap<String, String>();

    public RpcResult(){
    }

    public RpcResult(Object result){
        this.result = result;
    }

    public RpcResult(Throwable exception){
        this.exception = exception;
    }

    public Object recreate() throws Throwable {
        if (exception != null) {
            throw exception;
        }
        return result;
    }

    /**
     * @deprecated Replace to getValue()
     * @see com.alibaba.dubbo.rpc.RpcResult#getValue()
     */
    @Deprecated
    public Object getResult() {
        return getValue();
    }

    /**
     * @deprecated Replace to setValue()
     * @see com.alibaba.dubbo.rpc.RpcResult#setValue()
     */
    @Deprecated
    public void setResult(Object result) {
        setValue(result);
    }

    public Object getValue() {
        return result;
    }

    public void setValue(Object value) {
        this.result = value;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable e) {
        this.exception = e;
    }

    public boolean hasException() {
        return exception != null;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public String getAttachment(String key) {
        return attachments.get(key);
    }

    public String getAttachment(String key, String defaultValue) {
        String result = attachments.get(key);
        if (result == null || result.length() == 0) {
            result = defaultValue;
        }
        return result;
    }

    public void setAttachments(Map<String, String> map) {
        if (map != null && map.size() > 0) {
            attachments.putAll(map);
        }
    }

    public String addTimeChain(String source, String timeChain){
        String time = source+":"+System.currentTimeMillis();
        timeChain = timeChain+","+time;
        setAttachment("time-chain", timeChain);
        return timeChain;
    }

    public String updateTimeChain(String source){
        String timeChain = getAttachment("time-chain");
        String time = source+":"+System.currentTimeMillis();
        if(StringUtils.isEmpty(timeChain)){
            timeChain = time;
        }
        else{
            timeChain = timeChain+","+time;
        }
        setAttachment("time-chain", timeChain);
        return timeChain;
    }

    public void setAttachment(String key, String value) {
        attachments.put(key, value);
    }

    @Override
    public String toString() {
        return "RpcResult [result=" + result + ", exception=" + exception + "]";
    }
}