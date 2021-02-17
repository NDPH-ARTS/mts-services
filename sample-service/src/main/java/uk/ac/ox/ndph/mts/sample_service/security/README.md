# Security

## Authentication

Authentication is the process of recognizing a user’s identity.
Every incoming request is associated with a set of identifying credentials.
The authentication process runs when the application starts, before any other code is allowed to proceed. 
Saying that once the component is integrated in the service for each incoming request it will request for a valid token
otherwise the request will be unauthorised.

## Authorisation

### Overview

The authorisation process happens after the user was authenticated to the system. Having the user credentials it can now
validate if the user is permitted to perform the specific action. 

### Authorisation Flow

To validate the user is allowed to perform the action the authorisation flow will:
1. Get the user object id from the token
2. Find the practitioner, and it's assignment roles associated with the user
3. Fetch the roles permissions
4. Validate there is at least one role where the required permission is present. 
   If no role contains the required permission then the service will respond with HTTP status code 403,
   and the request will be forbidden.
5. Validate there is at least one role with a site that is allowed on the request entity's site.
   If no role contains a site with permission then the service will respond with HTTP status code 403,
   and the request will be forbidden.

### Integrating Authorisation

Each endpoint in a controller will require an authorisation annotation:

```java
@PreAuthorize("@authorisationService.authorise(...)")
@GetMapping("/yourEndpoint")
public String yourEndpoint() {
        return ...;
        }
```

#### Examples

**Endpoint that requires no site check:**

'<required-permission>' - is a string specifying the required permission to perform the action. For example if the
required permission is create-person, then the param should be 'create-person'.

```java
@PreAuthorize("@authorisationService.authorise('<required-permission>')")
@GetMapping("/yourEndpoint")
public String yourEndpoint() {
        return ...;
        }
```


**Endpoint with single entity with site check:**

- '<required-permission>' - is a string specifying the required permission to perform the action. For example if the
required permission is create-person, then the param should be 'create-person'.


- someEntity.siteIdPropertyName - is an entity in the request with a siteId property which we want to validate.
  For example:
  ```java
  class SomeEntity {
  
    private String siteIdPropertyName;
    
    ...
  }
  ```

```java
@PreAuthorize("@authorisationService.authorise('<required-permission>', #someEntity.siteIdPropertyName)")
@PostMapping("/yourEndpoint")
public String yourEndpoint(@RequestBody SomeEntity someEntity) {
        return ...;
        }
```

**Endpoint with list of entities with site check:**

- '<required-permission>' - is a string specifying the required permission to perform the action. For example if the
  required permission is create-person, then the param should be 'create-person'.

- entitiesList - the list of entities in the request

- getSiteId - the method name in the entity to get the site id property

For example:
  ```java
  class SomeEntity {
  
    private String siteIdPropertyName;
    
    public String getSiteId() {
        return this.siteIdPropertyName;
    }
  }
  ```

```java
@PreAuthorize("@authorisationService.authorise('<required-permission>', #entitiesList, 'getSiteId')")
@PostMapping("/yourEndpoint")
public String yourEndpoint(@RequestBody List<SomeEntity> entitiesList) {
        return ...;
        }
```
