const requests = require('../../data/role-service/roleservice')
const conf = require('../../config/conf')

beforeEach(function () {
    console.log(`Base URL2: ${conf.BASE_URL}`)
    baseRequest = request(conf.BASE_URL)
})

describe('As a user I want to create roles so that they can be assigned to persons', function () {
    it.only('I am able to enter a list of names for the role', async () => {
        const response = await baseRequest.post('/roles').send(requests.validRole);
        expect(response.text).to.contain("id")
        expect(response.status).to.equal(HttpStatus.OK)
    });
});