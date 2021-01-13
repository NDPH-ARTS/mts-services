# trial-config-service

The following properties are required:

    -Djdbc.driver=**db driver class name** 
    -Djdbc.url=< **arts db path** > 
    -Dserver.port=**http port**

*e.g.*
```-Djdbc.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver -Djdbc.url=jdbc:sqlserver://localhost:1433;databaseName=master;user=sa;password=XaVhCoCraB123!£^ -Dserver.port=8084```

or run the service with a database and global-trial-config-mock and run API tests against it:

```docker-compose up```

Microsoft SQL Server docker:

```docker pull mcr.microsoft.com/mssql/server:2019-latest```

```docker run -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=Technocrat123!£^' -p 1433:1433 --name sql1 -h sql1 -d mcr.microsoft.com/mssql/server:2019-latest```
