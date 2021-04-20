/*
Extensions attribute addition to the site tree
author - Sameera Purini
*/
const requests = require('../../data/createTrialSite/siteExtensions')
const conf = require('../../config/conf')
const endpointUri = '/api/sites';
const utils = require('../../common/utils')
const fetch = require("node-fetch");

let ccoSiteId;
let regionParentSiteId;
let countrySiteId;

describe('As a user I want to configure a trial site ‘type’ to have a custom string field So that when my trial users create trial sites using this ‘type’ they are able to enter the information they require to support their trial', function () {

    it.only('As a user I can add a region to the root of CCO without extensions configured', async () => {
        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(conf.baseUrl + endpointUri, {
            headers: headers,
            method: 'GET',
        })
        let response = await fetchResponse.json();
        ccoSiteId = response[0].siteId
        let regionId = requests.regionWithExt;
        regionId.parentSiteId = ccoSiteId
        let fetchResponse1 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(regionId),
        })
        let regionResponse = await fetchResponse1.json();
        expect(fetchResponse1.status).to.equal(HttpStatus.CREATED)
        regionParentSiteId = regionResponse.id
    });

    it.only('As a user I can configure a custom string field calling it Extensions to a country by giving the field a name, display name', async () => {
        let regionParentId = requests.countryWithExt;
        regionParentId.parentSiteId = regionParentSiteId
        const headers2 = await utils.getHeadersWithAuth()
        let fetchResponse2 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers2,
            method: 'POST',
            body: JSON.stringify(regionParentId),
        })
        let countryResponse = await fetchResponse2.json();
        expect(fetchResponse2.status).to.equal(HttpStatus.CREATED)
        countrySiteId = countryResponse.id
    });

    it.only('As a user I can configure a custom string field calling it Extensions to a LCC by giving the field a name, display name', async () => {
        let countryParentId = requests.lccWithExt;
        countryParentId.parentSiteId = countrySiteId
        const headers3 = await utils.getHeadersWithAuth()
        let fetchResponse3 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers3,
            method: 'POST',
            body: JSON.stringify(countryParentId),
        })
        expect(fetchResponse3.status).to.equal(HttpStatus.CREATED)
    });
});
