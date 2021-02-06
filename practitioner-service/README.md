# Practitioner Service

Practitioner service validates the input practitioner details and adds it as a FHIR practitioner entity to the FHIR
store.

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

**Condition** : If practitioner was created successfully in the FHIR store.

**Code** : `201 CREATED`

**Content example**

```json
{
  "id": "787485bc-7c4e-4d2d-bf42-f9db010e5fb5"
}
```

#### Error Responses

**Condition** : If input is malformed.

**Code** : `400 Bad Request`

**Content** :

```json
{
  "error": "JSON parse error: Unexpected character..."
}
```

#### Or

**Condition** : If a provided argument did not pass validation.

**Code** : `422 Unprocessable Entity`

**Content** :

```json
{
  "error": "prefix cannot be over 20 chars."
}
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

### Create RoleAssignment

Assigns a role to a practitioner in a site.

**URL** : `/practitioner/{practitionerId}/roles`

**Payload** :

```json
{
  "siteId": "mySiteId",
  "roleId": "myRoleId"
}
```

| Parameter | Data Type Expected |                                                       Description                                                      |
|:---------:|:------------------:|:----------------------------------------------------------------------------------------------------------------------:|
|   practitionerId  |       string       | The id of the practitioner |
|   siteId          |       string       | The id of the site the role will apply to |
|   roleId          |       string       | The id of the role to assign |

**Method** : `POST`

#### Success Response

**Condition** : If the role was assigned successfully in the FHIR store.

**Code** : `201 CREATED`

**Content example**

```json
{
  "id": "787485bc-7c4e-4d2d-bf42-f9db010e5fb5"
}
```

#### Error Responses

**Condition** : If input is malformed.

**Code** : `400 Bad Request`

**Content** :

```json
{
  "error": "JSON parse error: Unexpected character..."
}
```

#### Or

**Condition** : If a provided argument did not pass validation or any of the parameters don't exist (error will 
include which).

**Code** : `422 Unprocessable Entity`

**Content** :

```json
{
  "error": "siteId doesn't exist."
}
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

## Service Dependencies

### FHIR Store and HL7 Model

Practitioner service is backed up by a FHIR store which is accessible as an HTTP/S endpoint and configurable by the "
fhir.uri" application property. Internally, the service
uses [hapi client library](https://hapifhir.io/hapi-fhir/docs/client/examples.html) to handle the model and transactions
with FHIR store.

### MTS role-service

Requires a URL pointing to the role service in the `role.service.uri` property.

### MTS site-service

Requires a URL pointing to the site service in the `site.service.uri` property.

## Validation Configuration

### practitioner-configuration.json

Sets the validation rules for practitioner's name attributes using regex. Note: In this iteration, the field names are
hard-coded, and the service will validate the existence of all three attributes (prefix, given name and family name) in
the configuration file. Adding more attributes to the json configuration file will not dynamically add them to the
validation process. Empty or null validation regex is converted to a "validate-any" expression. The current json file
allows up to 35 characters for any field, and specifies only "family name" as mandatory (minimum characters = 1).

## Running the service

### Local profile (for local debugging)
when running locally, the config server is not required. setup the following env vars and run the project:

```sh
SPRING_PROFILES_ACTIVE=local
FHIR_URI=<some-uri>
ROLE_SERVICE_URI=<some-uri>
SITE_SERVICE_URI=<some-uri>
```

### Other profiles (either deployed or locally)
When not using the local profile, the config server is needed.
To run, setup the following env args:

```sh
SPRING_PROFILES_ACTIVE=dev/prod/etc
SPRING_CLOUD_CONFIG_LABEL=main
SPRING_CLOUD_CONFIG_URI=<some-uri>
```

note that when running with any profile other than 'local' the micro-services URIs are not needed as it will automatically be fetched using the discovery service.
Instead the information such as the config server uri, profile and label are needed.
