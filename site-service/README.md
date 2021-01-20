# Site Service
Site service validates the input site details and adds it as a FHIR site entity to the FHIR store.

## API Endpoints
### Create Site
Creates a new site entity.

**URL** : `/site`

**Payload** :

```json
{
    "name": "CCO",
    "alias": "CCO"
}
```

| Parameter | Data Type Expected |                                                       Description                                                      |
|:---------:|:------------------:|:----------------------------------------------------------------------------------------------------------------------:|
|     name    |       string       | Name |
|     alias    |       string       | Description |


**Method** : `POST`

#### Success Response

**Condition** : If site was created succesfully in the FHIR store.

**Code** : `201 CREATED`

**Content example**

```json
{
    "id": "787485bc-7c4e-4d2d-bf42-f9db010e5fb5"
}
```

#### Error Responses

**Condition** : If is input is malformed.

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
{"error": "alias cannot be over 35 chars."}
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
Site service is backed up by a FHIR store which is accessible as an HTTP/S endpoint and configurable by the "fhir.uri" application property.
Internally, the service uses [hapi client library](https://hapifhir.io/hapi-fhir/docs/client/examples.html) to handle the model and trasactions with FHIR store.


## Validation Configuration


### site-configuration.json

Sets the validation rules for site's name attributes using regex.
Note: In this iteration, the field names are hard-coded and the service will validate the existence of all two attributes (name and alias) in the configuration file. Adding more attributes to the json configurationfile will not dynamically add them to the validation process.
Empty or null validation regex is converted to a "validate-any" expression.
The current json file allows up to 35 characters for any field, and specifies only "name" and "alias" as mandatory (minimum charachters = 1).