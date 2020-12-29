# site-type-service
Provides CRUD operations for the Site Type entity

The following properties are required:

    -Djdbc.driver=**db driver class name** 
    -Djdbc.url=< **arts db path** > 
    -Dserver.port=**http port**

*e.g.*
```-Djdbc.driver=org.postgresql.Driver -Djdbc.url=jdbc:postgresql://localhost:5432/arts?user=postgres&password=pass -Dserver.port=8086```

