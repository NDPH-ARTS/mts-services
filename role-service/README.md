# role-service

## How to run - test env

    docker-compose up role-service

Env vars:

    set GITHUB_SHA=test
    set SAPASSWORD=SomePasswordGoesHere123abc

Visit http://localhost:82/roles?page=0&size=10 or http://localhost:82/swagger-ui/

## How to run - dev env

### 1. Database in a docker container

    docker pull mcr.microsoft.com/mssql/server:2019-latest
    docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=SomePasswordGoesHere123$%abc" -p 1466:1433 --name mssql1 -h mssql1 -d mcr.microsoft.com/mssql/server:2019-latest

### 2. Run spring boot application with VM opts

    -Djdbc.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver -Djdbc.url=jdbc:sqlserver://localhost:1466;databaseName=master;user=sa;password=SomePasswordGoesHere123abc -Dserver.port=8083

## Endpoints:


- `GET /roles?page=x&size=y` returns all roles, paged, including any permission mappings
- `GET /roles/{X}` returns role X including any permission mappings
- `GET /permissions?page=x&size=y` returns all available permissions - this is a static allowlist see below.
- `POST /roles` with body :
  ```
  {
    "id": "bar",
    "permissions": [
        {
            "id": "create-person"
        }
    ]
  }
  ```
  creates a new role with ID and with permission mappings (if given).
  Returns a 409 (conflict) if the role ID already exists.
  Returns a 400 (bad request) if the role ID is blank or longer than 255 characters.
  Returns a 400 (bad request) if any of the permissions are not on the allowlist.

-`POST /roles/{X}/permissions` with body :
  ```
  [
     {
        "id": "create-person"
     },
      {
         "id": "view-person"
      }
   ]

  ```
updates permissions for role X.
Returns a 404 (not found) if role X is not found.
Returns a 400 if any of the permissions are not on the allowlist.

Permissions themselves are (intentionally) static allowlist, loaded by liquibase.
Initial list suggested by @adriancull is _view-person_ and _create-person_  see https://github.com/NDPH-ARTS/mts-services/blob/feature/ARTS-101-permissions/role-service/src/main/resources/db/changelog/changelog-permission-data.xml


