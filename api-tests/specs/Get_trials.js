
const requests = require('../data/requests')
const conf = require('../config/conf')


beforeEach(function () {
  baseRequest = request(conf.BASE_URL)
})

describe('Check the end point response of a simple microservice', function () {

  it(' Check GET of translation/fr-fr', async function () {
    const response = await baseRequest.get("/translation/fr-fr")
    expect(response.status).to.equal(HttpStatus.OK)
    console.log('response message' + response.status)
    expect(response.body.title).to.equal('Arts Admin')
    expect(response.body.id).to.equal('Id')
  });


  it(' Check GET of  translation/ja-jp', async function () {
    const response = await baseRequest.get("/translation/ja-jp")
    expect(response.status).to.equal(HttpStatus.OK)
    expect(response.body.title).to.equal('Arts Admin')
    expect(response.body.id).to.equal('Id')
  });

  it(' Check GET of  translation/en-gb', async function () {
    const response = await baseRequest.get("/translation/en-gb")
    expect(response.status).to.equal(HttpStatus.OK)
    expect(response.body.title).to.equal('Arts Admin')
    expect(response.body.id).to.equal('Id')
  });

}); 