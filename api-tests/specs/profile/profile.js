const conf = require('../../config/conf')
const utils = require('../../common/globalUtils')
const fetch = require("node-fetch");
const { expect } = require('chai');
const endpointUri = '/api/practitioner/profile';


describe('A Trial is being configured with a bootstrap user assigned to a site and a role with permissions', function () {

    it('User when pointed to profile endpoint uri gets a successful response with authorisation checks', async () => {
        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(conf.baseUrl + endpointUri, {
            headers: headers,
            method: 'GET',
        })
        let response = await fetchResponse.json();
        expect(fetchResponse.status).to.equal(HttpStatus.OK);
        expect(response[0]).to.have.property('id');
    });
});