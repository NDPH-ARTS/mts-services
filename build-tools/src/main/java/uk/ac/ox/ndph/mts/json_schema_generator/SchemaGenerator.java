package uk.ac.ox.ndph.mts.json_schema_generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig;
import com.kjetland.jackson.jsonSchema.JsonSchemaDraft;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;

public class SchemaGenerator {

    private final JsonNode jsonSchema;

    public SchemaGenerator(final Class trailConfigFile) {
        this.jsonSchema = generateJsonSchema(trailConfigFile);
    }

    private JsonNode generateJsonSchema(final Class trailConfigFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonSchemaConfig config = JsonSchemaConfig.vanillaJsonSchemaDraft4()
                .withJsonSchemaDraft(JsonSchemaDraft.DRAFT_07);
        JsonSchemaGenerator jsonSchemaGenerator =
                new JsonSchemaGenerator(objectMapper, config);
        return jsonSchemaGenerator.generateJsonSchema(trailConfigFile);
    }

    public JsonNode getJsonSchema() {
        return jsonSchema;
    }
}
