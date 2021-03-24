package uk.ac.ox.ndph.mts.json_schema_generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import uk.ac.ox.ndph.mts.json_schema_generator.model.InitTrialConfig;
import uk.ac.ox.ndph.mts.json_schema_generator.model.PractitionerConfig;
import uk.ac.ox.ndph.mts.json_schema_generator.model.SiteConfig;
import uk.ac.ox.ndph.mts.json_schema_generator.model.TrialDefinitionFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public final class Main {

    private static class GeneratedTuple {
        private Class cls;
        private String filename;

        public Class getCls() {
            return cls;
        }

        public String getFilename() {
            return filename;
        }

        public GeneratedTuple(Class cls, String filename) {
            this.cls = cls;
            this.filename = filename;
        }
    }

    public static void main(final String[] args) throws IOException {
        ArrayList<GeneratedTuple> itemToProcess = Lists.newArrayList(
            new GeneratedTuple(TrialDefinitionFile.class, "definition-schema.json"),
            new GeneratedTuple(InitTrialConfig.class, "init-service-trial-schema.json"),
            new GeneratedTuple(PractitionerConfig.class, "practitioner-service-configuration-schema.json"),
            new GeneratedTuple(SiteConfig.class, "site-service-configuration-schema.json"));

        for (int i = 0; i < itemToProcess.size(); i++) {
            process(itemToProcess.get(i));
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
