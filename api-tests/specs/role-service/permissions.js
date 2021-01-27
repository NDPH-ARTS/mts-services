const requests = require('../../data/role-service/permissions')
const conf = require('../../config/conf')

beforeEach(function () {
    baseRequest = request(conf.BASE_URL)
})

describe('As a user I want to set permissions for a role so that I can decide what functionality users assigned this role will have in the system', function () {
    it.only('I am able to define the permissions linked to a role', async () => {
        const response = await baseRequest.post('/roles').send(requests.assignPermission);
        expect(response.text).to.contain("id")
        expect(response.status).to.equal(HttpStatus.OK)
    });

});
