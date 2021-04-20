/*
Profile class file that has scenarios to run profile tests
author - Sameera Purini
*/

const conf = require('../../config/conf')
const utils = require('../../common/utils')
const fetch = require("node-fetch");
const { expect } = require('chai');
const endpointUri = '/api/handoff/hello';


describe('Hello test', function () {

    it('Hello test', async () => {
        let fetchResponse = await fetch(conf.baseUrl + endpointUri, {
            method: 'GET',
        })
        expect(fetchResponse.status).to.equal(HttpStatus.OK);
    });
});
