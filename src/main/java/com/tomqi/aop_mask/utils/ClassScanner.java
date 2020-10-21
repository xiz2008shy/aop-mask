package com.tomqi.aop_mask.utils;

import com.tomqi.aop_mask.mask_core.FastDataMaskTemplate;
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

    public static Set<Class<?>> scannerAll(){
        String rootPath = rootPath();
        File rootDir = new File(rootPath);
        Set<Class<?>> clazzSet = new HashSet<>();
        if (!rootDir.isDirectory()){
            return clazzSet;
        }
        doScanner(rootDir,clazzSet,rootPath);
        return clazzSet;
    }

    private static void doScanner(File dir,Set<Class<?>> set,String rootPath){
        dir.listFiles(file->{
            if (file.getName().endsWith(".class")){
                clazzAddSet(file,set,rootPath);
            }else if(file.isDirectory()) {
                doScanner(file,set,rootPath);
            }
            return false;
        });

    }

    public static void clazzAddSet (File file,Set<Class<?>> set,String rootPath){
        String absolutePath = file.getAbsolutePath();
        absolutePath = absolutePath.substring(rootPath.length()-1,absolutePath.lastIndexOf("."));
        String fullName = absolutePath.replace(File.separator,".");

        try {
            Class<?> clazz = Class.forName(fullName);
            if (FastDataMaskTemplate.class.isAssignableFrom(clazz) && !clazz.equals(FastDataMaskTemplate.class)){
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
