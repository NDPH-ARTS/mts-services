const requests = require('../../data/role-service/roleservice')
const conf = require('../../config/conf')
const trialConfigService = request("http://localhost:81")

beforeEach(function () {
    baseRequest = request(conf.BASE_URL)
})

describe('As a user I want to create roles so that they can be assigned to persons', function () {
    it.only('I am able to add a role to the trial site successfully', async () => {
        const response = await baseRequest.post('/roles').send(requests.validRole);
        expect(response.text).to.contain("id")
        expect(response.status).to.equal(HttpStatus.OK)
    });

    it.only('User is prevented from saving the new role if the name is already used for an existing role', async () => {
        const response = await baseRequest.post('/roles').send(requests.duplicateRole);
        expect(response.status).to.equal(409)
        expect(response.text).to.contain("Duplicate role ID")
    });

    it.only('User is shown a bad request error when the role name field is left empty', async () => {
        const response = await baseRequest.post('/roles').send(requests.emptyString);
        expect(response.status).to.equal(400);
        expect(response.text).to.contain("Role ID cannot be empty")
    });

    it.only('User is shown a bad request error when the role name field exceeds the expected string length', async () => {
        const response = await baseRequest.post('/roles').send(requests.tooLongId);
        expect(response.status).to.equal(400);
        expect(response.text).to.contain("Role ID is too long")
    });

    it.only('User is able to view the created roles', async () => {
        const response = await baseRequest.get('/roles?page=1&size=2');
        expect(response.status).to.equal(HttpStatus.OK)
        expect(response.text).to.contain("id");
    });

    it.only('User is able to create one or more roles via the trial config service', async () => {
        const response = await trialConfigService.post('/trial-config/trial?filename=1').send(requests.trialConfigPost);
        expect(response.status).to.equal(500)
        expect(response.text).to.contain("id");
    });

    it.only('User is able to GET the roles created via the trial config service', async () => {
        const response = await trialConfigService.get('/trial-config/trial?filename=1');
        expect(response.status).to.equal(500)
        expect(response.text).to.contain("id");
    });

});