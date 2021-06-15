# HOW TO RUN THE LOCAL ENV

## ON WINDOWS
From a command prompt:
```sh
docker build -t sibling-creator . -f localenv/Dockerfile
docker run -it -v "/var/run/docker.sock:/var/run/docker.sock" -e "GITHUB_SHA=friday" --env-file c:\Users\katesan\secrets.list sibling-creator
```

You need to create a file called secrets.list on your local system with these environment variables (available on confluence):
```sh
INIT_SERVICE_SECRET=**
SAPASSWORD=**
AUTOMATION_USER_PASSWORD=**
QA_WITH_CREATE_USER_PASSWORD=**
BOOTSTRAP_USER_PASSWORD=**
```
And change the path in the run command to point to it.


You can pick any value for GITHUB_SHA and make it unique for a fresh build.
