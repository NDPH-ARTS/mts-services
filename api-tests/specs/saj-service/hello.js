const conf = require('../../config/conf')
const utils = require('../../common/utils')
const fetch = require("node-fetch");
const { expect } = require('chai');
const helloEndpointUri = '/api/hello'


describe('Test hello endpoint', function () {

    it('Get Hello ', async () => {

        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(conf.baseUrl + endpointUri, {
            headers: headers,
            method: 'GET'
        })
        expect(fetchResponse.status).to.equal(HttpStatus.OK);

    });
});
