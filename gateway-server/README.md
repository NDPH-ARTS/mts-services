# Gateway Server

The gateway server is a [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) and acts as a single entry point which routes requests to the different micro-services (practitioner, roles, sites, etc...).

### usage example
to reach the underlying resources, simply use the following url format:

http://baseurl:port/service-name/endpoint.

for example:

POST http://localhost:8072/practitioner-service/practitioner
