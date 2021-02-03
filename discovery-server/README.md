#Discovery Service - Eureka

This is a Netflix Eureka service registry.The Discovery server is very lightweight with only 1 Application class and 1 property file necessary to run the server. \
The application class needs to be annotated with:`@EnableEurekaServer`

The property file needing to declare the port for the server, and two other properties to disable the eureka server from trying to register (every eureka server is also an eureka client):
<pre>
server.port = 8761 
eureka.client.register-with-eureka = false
eureka.client.fetch-registry = false
</pre>

More information can be found at\
https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-eureka-server.html
