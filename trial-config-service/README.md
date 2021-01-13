# trial-config-service

The following properties are required:

    -Djdbc.driver=**db driver class name** 
    -Djdbc.url=< **arts db path** > 
    -Dserver.port=**http port**
    -Drole.service=**role service uri**

*e.g.*
```-Djdbc.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver -Djdbc.url=jdbc:sqlserver://localhost:1466;databaseName=master;user=sa;password=SomePasswordGoesHere123$%abc -Dserver.port=8084 -Drole.service=http://localhost:8086```


Microsoft SQL Server docker:

```docker pull mcr.microsoft.com/mssql/server:2019-latest```

```docker run -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=Technocrat123!£^' -p 1433:1433 --name sql1 -h sql1 -d mcr.microsoft.com/mssql/server:2019-latest```

For testing the service with a database, global-trial-config-mock and role service:

```docker-compose up trial-config-service```
