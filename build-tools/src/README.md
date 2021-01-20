# build-tools
This Module holds the MTS-servies build tools.

## Tools list

### 1. json-schema-generator
- This tool generates jsonSchema DRAFT-07 from POJOs using Jackson @Annotations.
- The jsonSchema is being written to the current directory as "JsonSchema.json".
- This project uses [mbknor-jackson-jsonschema](https://github.com/mbknor/mbknor-jackson-jsonSchema) package.

##### Running the jsonSchema tool
- Run the attached jar: `java -jar json-schema-generator.jar`
- Alternatively, import the tool as a maven project and run the Main class.
- When compiling the project the jar file is created under 'target'.

#### Modify the json schema
- Under 'model' are the different POJOs that represent the trial config file definitions.
- The main config file is **Model/TrialConfigFile**; When adding a new field to the configuration, add the POJO as a field in this class with the right json annotation.
