package uk.ac.ox.ndph.mts.json_schema_generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import uk.ac.ox.ndph.mts.json_schema_generator.model.InitTrialConfig;
import uk.ac.ox.ndph.mts.json_schema_generator.model.PractitionerConfig;
import uk.ac.ox.ndph.mts.json_schema_generator.model.SiteConfig;
import uk.ac.ox.ndph.mts.json_schema_generator.model.TrialDefinitionFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public final class Main {

    private static class GeneratedTuple {
        private final Class<?> cls;
        private final String filename;

        public Class<?> getCls() {
            return cls;
        }

        public String getFilename() {
            return filename;
        }

        public GeneratedTuple(Class<?> cls, String schemaName) {
            this.cls = cls;
            this.filename = schemaName + ".json";
        }
    }

    public static void main(final String[] args) throws IOException {
        List<GeneratedTuple> itemToProcess = Arrays.asList(
            new GeneratedTuple(TrialDefinitionFile.class, TrialDefinitionFile.SCHEMA_NAME),
            new GeneratedTuple(InitTrialConfig.class, InitTrialConfig.SCHEMA_NAME),
            new GeneratedTuple(PractitionerConfig.class, PractitionerConfig.SCHEMA_NAME),
            new GeneratedTuple(SiteConfig.class, SiteConfig.SCHEMA_NAME));

        for (GeneratedTuple toProcess : itemToProcess) {
            process(toProcess);
        }
    }

    public static void process(GeneratedTuple tuple) throws IOException {
        SchemaGenerator jsonSchemaGenerator;

        // Generate JsonSchema object
        jsonSchemaGenerator = new SchemaGenerator(tuple.getCls());
        JsonNode jsonSchema = jsonSchemaGenerator.getJsonSchema();

        // Write JsonSchema to file
        ObjectMapper jsonObjectMapper = new ObjectMapper();

        // Apply indentation
        jsonObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jsonObjectMapper.writeValue(new File(tuple.getFilename()), jsonSchema);
    }
}
