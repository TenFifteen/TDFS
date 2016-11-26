package org.tridiots.ipc;

import java.io.Serializable;

public class Param implements Serializable {

    private static final long serialVersionUID = 7929260412703977927L;

    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] params;

    public Param(String methodName, Class<?>[] paramTypes, Object[] params) {
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.params = params;
    }

    public Object[] getParams() {
        return params;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public String getMethodName() {
        return methodName;
    }
}
