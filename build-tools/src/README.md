# build-tools
This Module holds the MTS-services build tools.

## Tools list

### 1. json-schema-generator
- This tool generates jsonSchema DRAFT-07 from POJOs using Jackson @Annotations.
- The jsonSchemas are written to the current directory.
- This project uses [mbknor-jackson-jsonschema](https://github.com/mbknor/mbknor-jackson-jsonSchema) package.

##### Running the jsonSchema tool
- Run the attached jar: `java -jar json-schema-generator.jar`
- Alternatively, import the tool as a maven project and run the Main class.
- When compiling the project the jar file is created under 'target'.

#### Modify json schemas
- Under 'model' are the different POJOs that represent different configuration files.
