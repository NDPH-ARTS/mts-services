# role-service

## How to run - test env

    docker-compose up role-service

Env vars:

    set GITHUB_SHA=test
    set JDBC_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
    set JDBC_URL=jdbc:sqlserver://mts-services_sqlserver_1:1433;databaseName=master;user=sa;password=SomePasswordGoesHere123abc
    set SAPASSWORD=SomePasswordGoesHere123abc

Visit http://localhost:82/roles?page=0&size=10 or http://localhost:82/swagger-ui/

## How to run - dev env

### 1. Database in a docker container

    docker pull mcr.microsoft.com/mssql/server:2019-latest
    docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=SomePasswordGoesHere123$%abc" -p 1466:1433 --name mssql1 -h mssql1 -d mcr.microsoft.com/mssql/server:2019-latest

### 2. Run spring boot application with VM opts

    -Djdbc.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver -Djdbc.url=jdbc:sqlserver://localhost:1466;databaseName=master;user=sa;password=SomePasswordGoesHere123abc -Dserver.port=8083



