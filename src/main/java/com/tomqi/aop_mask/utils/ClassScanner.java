package com.tomqi.aop_mask.utils;

import com.tomqi.aop_mask.annotation.MLog;
import com.tomqi.aop_mask.annotation.MValid;
import com.tomqi.aop_mask.annotation.MaskOn;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
     * @param classSet 用于收集@MLog标注的class
     * @return map中value存FastMaskTemplate的子类class，key存该class注解@MaskOn的value（即被mask的原类名）
     */
    public static Map<String,Class<?>> scannerAll(Class<?> filterClass, Set<Class<?>> classSet){
        String rootPath = rootPath();
        File rootDir = new File(rootPath);
        Map<String,Class<?>> clazzMap = new HashMap<>();
        if (!rootDir.isDirectory()){
            return clazzMap;
        }
        doScanner(rootDir,clazzMap,classSet,rootPath,filterClass);
        return clazzMap;
    }


    private static void doScanner(File dir,Map<String,Class<?>> clazzMap,Set<Class<?>> classSet,String rootPath,Class<?> filterClass){
        dir.listFiles(file->{
            if (file.getName().endsWith(".class")){
                clazzAddMap(file,clazzMap,classSet,rootPath,filterClass);
            }else if(file.isDirectory()) {
                doScanner(file,clazzMap,classSet,rootPath,filterClass);
            }
            return false;
        });
    }


    public static void clazzAddMap (File file,Map<String,Class<?>> clazzMap,Set<Class<?>> classSet,String rootPath,Class<?> filterClass){
        String absolutePath = file.getAbsolutePath();
        absolutePath = absolutePath.substring(rootPath.length()-1,absolutePath.lastIndexOf("."));
        String fullName = absolutePath.replace(File.separator,".");
        try {
            Class<?> clazz = Class.forName(fullName);
            if (filterClass.isAssignableFrom(clazz) && !clazz.equals(filterClass)) {
                MaskOn maskOn = clazz.getAnnotation(MaskOn.class);
                clazzMap.put(maskOn.value(),clazz);
            }else if (AnnotationUtils.findAnnotation(clazz,MLog.class) != null || AnnotationUtils.findAnnotation(clazz,MValid.class) != null) {
                classSet.add(clazz);
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
