# trial-config-service

The following properties are required:

    -Djdbc.driver=**db driver class name** 
    -Djdbc.url=< **arts db path** > 
    -Dserver.port=**http port**

*e.g.*
```-Djdbc.driver=org.postgresql.Driver -Djdbc.url=jdbc:postgresql://localhost:5432/arts?user=postgres&password=pass -Dserver.port=8084```

or run the service with a database and global-trial-config-mock and run API tests against it:

```docker-compose up```