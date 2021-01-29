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
The services that consume the config-server now only need to have a bootstrap property file in the 
/resources directory. The other property files will be served up by the config server.

The bootstrap property file will now contain properties to locate and instruct the config-server.
The following properties can be used to get started

`spring.application.name=trial-config-service` - name of service, this is required to help the
config server locate the property file in the file repo. Typically the repo will contain many property files 
which need to be named in a distinct manner to allow the config server to locate individual service property
files successfully. If this is ommitted the default application.properties file will be used at the base of the file repo.

`spring.cloud.config.uri=http://localhost:8888` - config server location
`spring.cloud.config.profile=default` - profile to help separate properties for envs - e.g dev, qa, int, prod etc
`spring.cloud.config.label=main` - the version of the property files - in our case teh GIT branch.


Deployment
TBC
