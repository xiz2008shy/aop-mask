import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tomqi.aop_mask.pojo.MethodArgs;

/**
 * 360 Financial Copyright
 *
 * @author YanWenqi
 * @description
 * @date 2020/10/30 10:56
 */
public class Json {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        MethodArgs methodArgs = new MethodArgs(new Object[]{"我是1","我是2",3,4});
        String s = objectMapper.writeValueAsString(methodArgs);

        System.out.println(s);
    }
}
