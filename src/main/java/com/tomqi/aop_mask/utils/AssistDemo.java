package com.tomqi.aop_mask.utils;

import javassist.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;


/**
 * 360 Financial Copyright
 *
 * @author YanWenqi
 * @description
 * @date 2020/10/19 16:40
 */
public class AssistDemo {

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ClassPool pool = ClassPool.getDefault();
        CtClass newCtClazz = pool.makeClass("com.tomqi.aop_mask.utils.MaskAssist");

        newCtClazz.setModifiers(Modifier.PUBLIC);

        //给newCtClazz新加一个maskName字段
        CtField maskName = new CtField(pool.get("java.lang.String"), "maskName", newCtClazz);
        maskName.setModifiers(Modifier.PRIVATE);
        newCtClazz.addField(maskName);

        CtField age = new CtField(pool.get("int"), "age", newCtClazz);
        age.setModifiers(Modifier.PRIVATE);
        newCtClazz.addField(age);

        //创建一个无参构造器
        CtConstructor constructor0 = new CtConstructor(new CtClass[0], newCtClazz);
        constructor0.setBody("{}");
        newCtClazz.addConstructor(constructor0);

        //创建一个全参构造器
        CtConstructor ctConstructor1 = new CtConstructor(new CtClass[]{pool.get("java.lang.String"),pool.get("int")}, newCtClazz);
        StringBuffer sb = new StringBuffer();
        //$0 代表this。 $1、$2...代表形参（与形参索引对应）
        sb.append("{$0.maskName = $1 ;");
        sb.append("$0.age = $2 ;}");
        ctConstructor1.setBody(sb.toString());
        newCtClazz.addConstructor(ctConstructor1);

        //创建一个MaskAssist的工厂方法
        CtMethod createMask = new CtMethod(pool.get("com.tomqi.aop_mask.utils.MaskAssist"), "createMask", null, newCtClazz);
        createMask.setModifiers(Modifier.PUBLIC + Modifier.STATIC);
        StringBuffer sb2 = new StringBuffer("{");
        sb2.append("com.tomqi.aop_mask.utils.MaskAssist mask = new com.tomqi.aop_mask.utils.MaskAssist();\n");
        sb2.append("mask.maskName = \"helloWorld\";");
        sb2.append("mask.age = 18 ;");
        sb2.append("return mask;");
        sb2.append("}");
        createMask.setBody(sb2.toString());
        newCtClazz.addMethod(createMask);

        //创建toString方法
        CtMethod toString = new CtMethod(pool.get("java.lang.String"), "toString", null, newCtClazz);
        toString.setBody("{return \"maskName = \" + $0.maskName +\"; age = \" + $0.age ;}");
        newCtClazz.addMethod(toString);

        //生成class文件，输出至对应目录
        String path = rootPath();
        newCtClazz.writeFile(path);

        Class<?> clazz = newCtClazz.toClass();
        System.out.println(clazz);

        Method method = clazz.getDeclaredMethod("createMask");
        Object res = method.invoke(null, null);
        System.out.println(res.toString());
    }

    /**
     * 获取项目根路径地址
     * @return
     * @throws IOException
     */
    private static String rootPath() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource("");
        return resource.getPath();
    }
}
