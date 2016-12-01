package org.tridiots.ipc;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

public class RPC {

    private static class ClientInvocationHandler implements InvocationHandler {

        private Client client;

        public ClientInvocationHandler(InetSocketAddress addr) {
            this.client = new Client(addr);
        }

        @Override
        public Object invoke(Object object, Method method, Object[] params) throws Throwable {
            return this.client.call(method.getName(), method.getParameterTypes(), params);
        }
    }
    
    public static Server getServer(Object instance, int port) {
        try {
            return new Server(instance, port);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getProxy(Class<?> protocol, InetSocketAddress addr) {
        return Proxy.newProxyInstance(
                protocol.getClassLoader(), 
                new Class<?>[] {protocol}, 
                new ClientInvocationHandler(addr));
    }
}
