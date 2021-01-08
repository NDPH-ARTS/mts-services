
const requests = require('../data/requests')
const conf = require('../config/conf')


beforeEach(function () {
  console.log(`Base URL: ${conf.BASE_URL}`)
  baseRequest = request(conf.BASE_URL)
})

describe('Trial Config Service', function () {
  it(' Should initialise a new trial from a valid config URL', async function () {
    const response = await baseRequest.post("/trial-config/trial").send('http://global-trial-config-mock:8085/trial/test_1')
    expect(response.status).to.equal(HttpStatus.OK)
  });

});