# Practitioner Service
Practitioner service validates the input practitioner details and adds it as a FHIR practitioner entity to the FHIR store.

## API Endpoints
### Create Practitioner
Creates a new practitioner entity.

**URL** : `/practitioner`

**Payload** :

```json
{
    "prefix": "sir",
    "givenName": "John",
    "familyName": "Smith"
}
```

| Parameter | Data Type Expected |                                                       Description                                                      |
|:---------:|:------------------:|:----------------------------------------------------------------------------------------------------------------------:|
|     prefix    |       string       | Parts that come before the name |
|     givenName    |       string       | Given names (not always 'first'). Includes middle names |
|     familyName    |       string       | Family name (often called 'Surname') |


**Method** : `POST`

#### Success Response

**Condition** : If practitioner was created succesfully in the FHIR store.

**Code** : `201 CREATED`

**Content example**

```json
{
    "id": "787485bc-7c4e-4d2d-bf42-f9db010e5fb5",
}
```

#### Error Responses

<<<<<<< HEAD
**Condition** : If is input is malformed.
=======
**Condition** : If input is malformed.
>>>>>>> main

**Code** : `400 Bad Request`

**Content** :
```json
{"error": "JSON parse error: Unexpected character..."}
```

#### Or

**Condition** : If a provided argument did not pass validation.

**Code** : `422 Unprocessable Entity`

**Content** :
```json
{"error": "prefix cannot be over 20 chars."}
```
#### Or

**Condition** : If FHIR store is unavailable.

**Code** : `502 Bad Gateway`

**Content** :

```json
{   
    "error": "Fhir store connection failed with error..."
}
```
___

## Service Dependecies

### FHIR Store and HL7 Model
Practitioner service is backed up by a FHIR store which is accessible as an HTTP/S endpoint and configurable by the "fhir.uri" application property.
Internally, the service uses [hapi client library](https://hapifhir.io/hapi-fhir/docs/client/examples.html) to handle the model and trasactions with FHIR store.


## Validation Configuration


### practitioner-configuration.json

Sets the validation rules for practitioner's name attributes using regex.
<<<<<<< HEAD
Note: In this iteration, the field names are hard-coded and the service will validate the existance of all three attribuets (prefix, given name and family name) in the configuration file. Adding more attributes to the json configurationfile will not dynamically add them to the validation process.
Empty or null validation regex is converted to a "validate-any" expression.
The current json file allows up to 35 charachters for any field, and specifies only "given name" as mandatory (minimum charachters = 1).
=======
Note: In this iteration, the field names are hard-coded and the service will validate the existance of all three attribuets (prefix, given name and family name) in the configuration file. Adding more attributes to the json configuration file will not dynamically add them to the validation process.
Empty or null validation regex is converted to a "validate-any" expression.
The current json file allows up to 35 charachters for any field, and specifies only "family name" as mandatory (minimum charachters = 1).
>>>>>>> main
