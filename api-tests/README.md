# mocha-chai-javascript api tests

In the api-tests folder,

# to install mocha-chai dependencies
npm install node_modules
# to get mochawesome html reports
npm install mochawesome

# to open the html reports, access the mochawesome-report folder and open the mochawesome.html in a browser


To run tests locally

# set the env variables and build the docker image locally as per the service under test
# export BASE_URL, example
BASE_URL=ttp://localhost:82/roles

# without the html reports
npm run test 

# with html reports
npm run test:awesome

If you want to run only a single test

# add only before the scenario
Example: 
<!-- it.only('User is able to view the created roles', async () => {
        const response = await baseRequest.get('/roles?page=0&size=2');
        expect(response.status).to.equal(HttpStatus.OK)
        expect(response.text).to.contain("id");
    }); -->

# or update the following lines in package.json file with the particular service
Example:
<!-- line 7 with "test:awesome": "mocha specs/*/roleservice.js --reporter mochawesome || true", 
line 9 with "test": "mocha specs/*/roleservice.js", -->
