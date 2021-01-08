
const requests = require('../data/requests')
const conf = require('../config/conf')


beforeEach(function () {
  console.log(`Base URL: ${conf.BASE_URL}`)
  baseRequest = request(conf.BASE_URL)
})

describe('Practitioner Service', function () {
  let person = {
    prefix: 'XXXXX',
    givenName: 'John',
    familyName: 'Gamily'
  };

  it('Should create a new FHIR practitioner', async function () {
    const response = await baseRequest.post("/practitioner").send(person);
    expect(response.status).to.equal(201);
  });

});