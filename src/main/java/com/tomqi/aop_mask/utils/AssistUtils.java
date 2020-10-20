package com.tomqi.aop_mask.utils;

import com.tomqi.aop_mask.mask_core.FastDataMaskTemplate;
import com.tomqi.demo.orgin_impl.FastMask;
import javassist.*;


/**
 * 360 Financial Copyright
 *
 * @author YanWenqi
 * @description
 * @date 2020/10/20 10:24
 */
public class AssistUtils {

    public static void main(String[] args) throws Exception {
        Class<?> aClass = rewriteMaskTemplate(FastMask.class);
        System.out.println(aClass.getName());
    }

    public static Class<?> rewriteMaskTemplate(Class<? extends FastDataMaskTemplate> clazz) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass originClazz = null;
        try {
            originClazz = pool.get(clazz.getName());
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        CtClass newClazz = null;
        if ( originClazz !=null ) {
            newClazz = pool.makeClass("com.tomqi.aop_mask.remark." + clazz.getSimpleName().concat("_Re"),originClazz);
        }

        /*CtMethod[] methods = newClazz.getMethods();
        for (CtMethod method : methods){
            System.out.println(method.getName());
        }*/

        CtMethod maskData = newClazz.getMethod("maskData","(Lcom/tomqi/aop_mask/pojo/MaskMessage;)Ljava/lang/Object;");
        maskData.setBody("{System.out.println(\"hello\");\nreturn null;}");
        newClazz.addMethod(maskData);
        System.out.println(maskData.getName());
        newClazz.writeFile(AssistDemo.rootPath());
        return newClazz.toClass();
    }
}
