const conf = require('../../config/conf')
const utils = require('../../common/utils')
const fetch = require("node-fetch");
const { expect } = require('chai');
const helloEndpointUri = '/api/hello'


describe('Test new endpoint', function () {

    it('Is authenticated ', async () => {

        //post a request to practitioner end point to create first Practitioner
        const headers = await utils.getBootStrapUserHeadersWithAuth()
        let fetchResponse = await fetch(conf.baseUrl + helloEndpointUri, {
            headers: headers,
            method: 'GET'
        })
        let response = await fetchResponse.json()
        expect(response.status).to.equal(HttpStatus.OK)

    });
});
