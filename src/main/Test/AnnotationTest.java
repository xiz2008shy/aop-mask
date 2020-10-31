import com.tomqi.demo.controller.NonRewriteMaskData;
import org.junit.platform.commons.util.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author TOMQI
 * @Title: AnnotationTest
 * @ProjectName: aop-mask
 * @Description :TODO
 * @data 2020/10/3114:24
 **/
public class AnnotationTest {

    public static void main(String[] args) {

        Method enclosingMethod = NonRewriteMaskData.class.getEnclosingMethod();
        System.out.println(enclosingMethod);
    }
}
