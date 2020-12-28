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

**Condition** : If a provided argument was missing or empty.

**Code** : `400 Bad Request`

**Content** :
```json
{"error": "values cannot be null or empty."}
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
