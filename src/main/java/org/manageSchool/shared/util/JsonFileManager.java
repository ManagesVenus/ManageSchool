package org.manageSchool.shared.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.manageSchool.shared.AppException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonFileManager {

    private static final String DATA_DIR = "data";

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private JsonFileManager() {}


    // READ

    public static <T> List<T> readAll(String fileName, Class<T> clazz) {
        File file = resolveFile(fileName);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            JavaType listType = MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, clazz);

            List<T> result = MAPPER.readValue(file, listType);

            if (result == null) {
                throw new AppException("El archivo JSON está vacío o corrupto: " + fileName);
            }

            return result;

        } catch (IOException e) {

            throw new AppException(
                    "Error al procesar JSON en " + fileName + ": " + e.getMessage(), e
            );
        }
    }


    // WRITE
    public static <T> void writeAll(String fileName, List<T> items) {
        File file = resolveFile(fileName);

        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        try {
            MAPPER.writeValue(file, items);
        } catch (IOException e) {
            throw new AppException(
                    "No se pudo escribir en el archivo " + fileName + ": " + e.getMessage(), e
            );
        }
    }

    private static File resolveFile(String fileName) {
        String basePath = System.getProperty("user.dir");

        return new File(basePath + File.separator + DATA_DIR + File.separator + fileName);
    }
}