package com.tomqi.aop_mask.mask_core.fast;

import com.tomqi.aop_mask.annotation.TimeNode;
import java.util.Iterator;
import java.util.Set;


/**
 * 360 Financial Copyright
 *
 * @author YanWenqi
 * @description 具体的dataMask方法的动态编辑工具类
 * @date 2020/10/23 19:15
 */
public class MaskBodyMaker {

    private MaskBodyMaker() {
    }

    public static void methodBodyCreate(Set<String> originMethodNames,String className, StringBuilder methodText, FastMaskTemplateSubRegister.ConversionMethodCollector collector,boolean log) {

        if (originMethodNames.isEmpty()) {
            return;
        }
        if (log) {
            methodText.append("long start$ = System.currentTimeMillis();\n");
        }
        // 类中masking修饰的方法只存在一个时
        if (originMethodNames.size() == 1) {
            Iterator<String> iterator = originMethodNames.iterator();
            String methodName = iterator.next();

            while (collector.hasNextNode(methodName)) {
                oneMethodHandle(methodText,collector);
            }
        } else {
            //存在多个masking修饰的方法时
            methodText.append("String methodName = $1.getMethodName();\n");
            methodText.append("switch(methodName){\n");
            for (String name : originMethodNames) {
                methodText.append("case \"");
                methodText.append(name);
                methodText.append("\": \n");
                while (collector.hasNextNode(name)) {
                    oneMethodHandle(methodText,collector);
                }
                methodText.append("break;\n");
            }
            methodText.append("}\n");
        }
        if (log) {
            methodText.append("long end$ = System.currentTimeMillis();\n")
                    .append("$0.logExecutor.asyncLog(")
                    .append(className)
                    .append(".log,\"")
                    .append("maskData")
                    .append("\",end$-start$,$1.getMethodArgs(),$1.getResult());");
        }
        methodText.append("return $1.getResult();\n");
    }

    public static void oneMethodHandle(StringBuilder methodText, FastMaskTemplateSubRegister.ConversionMethodCollector collector) {
        boolean isHandleTiming = collector.curTiming == TimeNode.HANDLE.getValue();
        //当前循环中是否存在handle节点，不存在handle处理将一次性写入
        if (isHandleTiming) {
            if (!collector.hasHandleTiming()) {
                if (!collector.isHandleFinish) {
                    methodText.append("$1.setJoinPoint(com.tomqi.aop_mask.utils.MaskContext.getPoint());\n");
                    methodText.append("handle($1);\n");
                    methodText.append("$1.setJoinPoint(null);\n");
                    collector.isHandleFinish = true;
                }
                //存在handle节点将分两次写入
            } else {
                if (collector.curNodeIndex == 0) {
                    methodText.append("$1.setJoinPoint(com.tomqi.aop_mask.utils.MaskContext.getPoint());\n");
                }
                FastMaskTemplateSubRegister.MethodNode node = collector.nextNode();
                methodText.append(node.methodName);
                methodText.append("($1);\n");
                if (collector.curNodeIndex == collector.handleNodeLength()) {
                    methodText.append("$1.setJoinPoint(null);\n");
                }
            }
        } else {
            FastMaskTemplateSubRegister.MethodNode node = collector.nextNode();
            methodText.append(node.methodName);
            methodText.append("($1);\n");
        }
    }

}
