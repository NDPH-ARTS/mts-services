const { validSiteTypes } = require('../../data/defineSiteTypes_data')
const conf = require('../../config/conf')
beforeEach(function () {
    console.log(`Base URL2: ${conf.BASE_URL}`)
    baseRequest = request(conf.BASE_URL)
})

describe('As a development user, I want to define a set of Trial Site Types, so that I can define a set of fields for each type of site for my users to create trial sites from in the trial site structure', function () {
    it('GIVEN a Trial Instance exists, WHEN I define a trial site ‘type’, THEN AC1 I can set the following properties:, ID(automatically generated), Name, Display Name(both required)', async () => {
        const response = await baseRequest.post('/trial-config/trial').send(validSiteTypes);
        //expect(response.text).to.contain("id")
        expect(response.status).to.equal(200)
    });
});