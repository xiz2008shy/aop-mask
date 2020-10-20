package com.tomqi.aop_mask.utils;

import javassist.*;

/**
 * @author TOMQI
 * @Title: AssistPraticeDemo
 * @ProjectName: aop_mask
 * @Description :TODO
 * @data 2020/10/2022:29
 **/
public class AssistPraticeDemo {

    public static void main(String[] args) throws NotFoundException, CannotCompileException {
        ClassPool pool = new ClassPool();
        pool.insertClassPath(new LoaderClassPath(AssistPraticeDemo.class.getClassLoader()));
        //作用与insertClassPath相同，但classpool内部classloader是通过一个链表维护，所以insert相当于在头部位置添加classloader
        //而append相当于在尾部添加classloader
        //pool.appendClassPath();

        CtClass newClazz = pool.makeClass("com.tomqi.demo.test.hello");
        newClazz.addInterface(pool.get(IHello.class.getName()));

        CtMethod sayHello = new CtMethod(CtClass.voidType, "sayHello", new CtClass[]{pool.get(String.class.getName())}, newClazz);
        sayHello.setBody("System.out.println(\"helloWorld\");");
        newClazz.addMethod(sayHello);

        Class<?> aClass = newClazz.toClass();
    }


    public interface IHello {
        public void sayHello(String str);
    }
}
