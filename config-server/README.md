Spring Cloud Config Server

Spring Cloud Config provides server-side and client-side support for externalized configuration, 
meaning you have a central place to manage external properties for applications across all environments.

The Config server is very lightweight with only 1 Application class and 1 property file necessary to run the server.
The application class needs to be annotated with 
`@EnableConfigServer`

The property file needing to declare the port for the server, the location of the server which holds the properties 
and the path to the application specific properties.

Currently we are using GitHub to act as the file repository, however other file repository options are available if necessary.

`server.port=8888`
`spring.cloud.config.server.git.uri=https://github.com/NDPH-ARTS/spring-config-server.git`
`spring.cloud.config.server.git.search-paths=trial-config-service, practitioner-service`

More information can be found at
https://cloud.spring.io/spring-cloud-config/reference/html

MTS services configuration


Deployment
TBC
