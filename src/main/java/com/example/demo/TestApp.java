package com.example.demo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TestApp {


    public static void main(String[] args) {
        //调用实现类本体
        TestInterface testInterface = new TestInterfaceImpl();
        testInterface.test();

        //静态代理调用
        TestInterface testInterfaceProxy = new TestInterfaceProxy();
        testInterfaceProxy.test();

        //动态代理调用
        TestInterface service = new TestInterfaceImpl();
        InvocationProxy invocationProxy = new InvocationProxy();
        TestInterface serviceInvocationProxy = (TestInterface) invocationProxy.get(service);
        serviceInvocationProxy.test();



    }
}


interface TestInterface {
    void test();
}

class TestInterfaceImpl implements TestInterface {
    @Override
    public void test() {
        System.out.println("println: test");
    }
}

/**
 * 静态代理示例
 */
class TestInterfaceProxy implements TestInterface {
    TestInterface testInterface = new TestInterfaceImpl();
    @Override
    public void test() {
        System.out.println("println: test proxy");
        testInterface.test();
    }
}


/**
 * 动态代理示例
 */
class InvocationProxy implements InvocationHandler {

    private Object object;

    public Object get(Object object) {
        this.object = object;
        return Proxy.newProxyInstance(this.object.getClass().getClassLoader(), this.object.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("println: invoke proxy");
        return method.invoke(this.object, args);
    }
}


