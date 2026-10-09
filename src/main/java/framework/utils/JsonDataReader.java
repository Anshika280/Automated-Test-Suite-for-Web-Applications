package framework.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import framework.constants.FrameworkConstants;
import framework.models.LoginDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class JsonDataReader {

    private static final Logger log = LoggerFactory.getLogger(JsonDataReader.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private JsonDataReader() {
        // Prevent instantiation
    }

    public static Object[][] getLoginDataArray(String jsonFileName) {
        String filePath = FrameworkConstants.USER_DIR + File.separator + "src" + File.separator + "test"
                + File.separator + "resources" + File.separator + "testdata" + File.separator + jsonFileName;
        try {
            log.info("Loading test data from JSON file: {}", filePath);
            List<LoginDataModel> list = objectMapper.readValue(new File(filePath), new TypeReference<List<LoginDataModel>>() {});
            Object[][] data = new Object[list.size()][1];
            for (int i = 0; i < list.size(); i++) {
                data[i][0] = list.get(i);
            }
            return data;
        } catch (IOException e) {
            log.error("Failed to read JSON test data from file {}: {}", filePath, e.getMessage(), e);
            throw new RuntimeException("Could not read JSON test data", e);
        }
    }
}
