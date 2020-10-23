package com.tomqi.aop_mask.mask_core;

import com.tomqi.aop_mask.annotation.TimeNode;
import java.util.Iterator;
import java.util.Set;

/**
 * 360 Financial Copyright
 *
 * @author YanWenqi
 * @description
 * @date 2020/10/23 19:15
 */
public class MaskClazzCreater {

    public static void methodBodyCreate(Set<String> originMethodNames,StringBuilder methodText,FastDataMaskTemplateSubRegister.ConversionMethodCollector collector){

        if (originMethodNames.isEmpty()) {
            return;
        }

        if (originMethodNames.size() == 1) {
            Iterator<String> iterator = originMethodNames.iterator();
            String methodName = iterator.next();

            while (collector.hasNextNode(methodName)) {

                boolean isHandleTiming = collector.curTiming == TimeNode.HANDLE.getValue();
                //当前循环中是否存在handle节点，不存在handle处理将一次性写入
                if (isHandleTiming) {
                    if (!collector.hasHandleTiming()) {
                        if (!collector.handleAfter()) {
                            methodText.append("message.setJoinPoint(com.tomqi.aop_mask.utils.MaskContext.getPoint());");
                            methodText.append("handle($1);");
                            methodText.append("message.setJoinPoint(null);");
                        }
                        //存在handle节点将分两次写入
                    }else {
                        if ( collector.curNodeIndex == 0) {
                            methodText.append("message.setJoinPoint(com.tomqi.aop_mask.utils.MaskContext.getPoint());");
                            FastDataMaskTemplateSubRegister.MethodNode node = collector.nextNode();
                            methodText.append(node.methodName);
                            methodText.append("($1);\n");
                        }
                        if ( collector.curNodeIndex == collector.handleNodeLength()){
                            methodText.append("message.setJoinPoint(null);");
                        }
                    }
                }else {
                    FastDataMaskTemplateSubRegister.MethodNode node = collector.nextNode();
                    methodText.append(node.methodName);
                    methodText.append("($1);\n");
                }

            }
        } else {
            methodText.append("String methodName = message.simpleClassName();\n");
            methodText.append("Switch(methodName){\n");
            for (String name : originMethodNames) {
                methodText.append("case(");
                methodText.append(name);
                methodText.append("){");
                while (collector.hasNextNode(name)) {
                    FastDataMaskTemplateSubRegister.MethodNode node = collector.nextNode();
                    methodText.append(node.methodName);
                    methodText.append("($1);\n");
                }
                methodText.append("}");
            }
        }
    }

}
