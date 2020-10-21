package com.tomqi.aop_mask.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


/**
 * @author TOMQI
 * @Title: ClassScanner
 * @ProjectName: aop_mask
 * @Description :遍历有所的class文件
 * @data 2020/10/210:15
 **/
public class ClassScanner {

    private ClassScanner(){}

    /**
     * 项目类路径下，查找特定类的所有子类或实现类
     * @param filterClass 指定的特定类
     * @return
     */
    public static Set<Class<?>> scannerAll(Class<?> filterClass){
        String rootPath = rootPath();
        File rootDir = new File(rootPath);
        Set<Class<?>> clazzSet = new HashSet<>();
        if (!rootDir.isDirectory()){
            return clazzSet;
        }
        doScanner(rootDir,clazzSet,rootPath,filterClass);
        return clazzSet;
    }

    private static void doScanner(File dir,Set<Class<?>> set,String rootPath,Class<?> filterClass){
        dir.listFiles(file->{
            if (file.getName().endsWith(".class")){
                clazzAddSet(file,set,rootPath,filterClass);
            }else if(file.isDirectory()) {
                doScanner(file,set,rootPath,filterClass);
            }
            return false;
        });

    }

    public static void clazzAddSet (File file,Set<Class<?>> set,String rootPath,Class<?> filterClass){
        String absolutePath = file.getAbsolutePath();
        absolutePath = absolutePath.substring(rootPath.length()-1,absolutePath.lastIndexOf("."));
        String fullName = absolutePath.replace(File.separator,".");

        try {
            Class<?> clazz = Class.forName(fullName);
            if (filterClass.isAssignableFrom(clazz) && !clazz.equals(filterClass)){
                set.add(clazz);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    /**
     * 获取项目根路径地址
     * @return
     * @throws IOException
     */
    public static String rootPath() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource("");
        return resource.getPath();
    }
}
