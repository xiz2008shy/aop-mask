import com.tomqi.aop_mask.mask_core.FastDataMaskTemplate;
import com.tomqi.aop_mask.utils.ClassScanner;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * 360 Financial Copyright
 *
 * @author YanWenqi
 * @description
 * @date 2020/10/28 10:30
 */
public class AssistDemoTest {

    private static final Logger log               = LoggerFactory.getLogger(AssistDemoTest.class);
    private static final String CORE_METHOD_NAME  = "maskData";
    private static final String NEW_CLASS_PACKAGE = "com.tomqi.aop_mask.remark.";
    public static final String  NEW_CLASS_SUFFIX  = "$Mask";
    private static final String MASK_MESSAGE      = "com.tomqi.aop_mask.pojo.MaskMessage";

    public static void main(String[] args) {
        // 查找指定class的子类或实现
        Set<Class<?>> classes = ClassScanner.scannerAll(FastDataMaskTemplate.class);

        for (Class<?> clazz : classes) {

            ClassPool pool = new ClassPool();
            Loader jClassLoader = new Loader();

            pool.insertClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
            try {
                CtClass ctClass = pool.get(clazz.getName());
                CtClass assistCreateClazz = pool
                        .makeClass(NEW_CLASS_PACKAGE + clazz.getSimpleName().concat(NEW_CLASS_SUFFIX), ctClass);

                // 构造方法
                CtConstructor ctConstructor = new CtConstructor(new CtClass[0], assistCreateClazz);
                ctConstructor.setBody("{ System.out.println(\"success create!\");}");
                assistCreateClazz.addConstructor(ctConstructor);

                // 准备重写maskData方法
                CtMethod method = ctClass.getMethod(CORE_METHOD_NAME,"(Lcom/tomqi/aop_mask/pojo/MaskMessage;)Ljava/lang/Object;");
                CtMethod subMaskData = CtNewMethod.copy(method, assistCreateClazz, null);

                StringBuilder methodText = new StringBuilder();

                //这里写入maskData的具体的执行代码
                if (clazz.getSimpleName().equals("FastTemplateTest")) {
                    methodText.append("{\n")
                            .append("String methodName = $1.getMethodName();\n")
                            .append("System.out.println(\"开始maskData---->\"+ methodName);\n")
                            .append("switch(methodName){\n")
                            .append("case \"fast\": \n")
                            .append("fastPerHandle($1);\n")
                            .append("$1.setJoinPoint(com.tomqi.aop_mask.utils.MaskContext.getPoint());\n")
                            .append("handle($1);\n")
                            .append("$1.setJoinPoint(null);\n")
                            .append("break;\n")
                            .append("case \"hello\": \n")
                            .append("$1.setJoinPoint(com.tomqi.aop_mask.utils.MaskContext.getPoint());\n")
                            .append("handle($1);\n")
                            .append("$1.setJoinPoint(null);\n")
                            .append("postHandle($1);\n")
                            .append("break;\n}\n")
                            .append("return $1.getResult();\n")
                            .append("}");
                    subMaskData.setBody(methodText.toString());
                    assistCreateClazz.addMethod(subMaskData);
                }

                //是否生成class文件
                if (true) {
                    assistCreateClazz.writeFile(ClassScanner.rootPath());
                }

                Class<?> assistClazz = assistCreateClazz.toClass();

            } catch (Exception e) {
                log.info("FastDataMaskTemplate子类加载错误!", e);
            }
        }
    }
}
