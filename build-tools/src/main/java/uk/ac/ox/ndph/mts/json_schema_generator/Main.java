package uk.ac.ox.ndph.mts.json_schema_generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import uk.ac.ox.ndph.mts.json_schema_generator.model.TrialConfigFile;

import java.io.File;
import java.io.IOException;


public final class Main {
    static final String fileName = "jsonSchema.json";

    public static void main(final String[] args) throws IOException, ClassNotFoundException {
        SchemaGenerator jsonSchemaGenerator;

        // Generate JsonSchema object
        jsonSchemaGenerator = new SchemaGenerator(TrialConfigFile.class);
        JsonNode jsonSchema = jsonSchemaGenerator.getJsonSchema();

        // Write JsonSchema to file
        ObjectMapper jsonObjectMapper = new ObjectMapper();

        // Apply indentation
        jsonObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jsonObjectMapper.writeValue(new File(fileName), jsonSchema);
    }
}
