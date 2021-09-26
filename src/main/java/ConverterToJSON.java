import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConverterToJSON {
    private final static String baseFile = "src/main/java/jsonResult.json";
    private final static String baseFileOrders = "src/main/java/jsonResultOrders.json";

    public static void toJSON(List<Product> productList) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(baseFile), productList);
    }

    public static void toJSONOrders(List<Product> productList) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(baseFileOrders), productList);
    }
}
