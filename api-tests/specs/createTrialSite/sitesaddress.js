/*
Sites address class file that contain scenarios covering site address entities
author - Sameera Purini
*/
const requests = require('../../data/createTrialSite/sitesaddress')
const conf = require('../../config/conf')
const endpointUri = '/api/sites';
const utils = require('../../common/utils')
const fetch = require("node-fetch");

let ccoParentSiteId;
let validRegionSite;
let countrySiteId;

describe('As a user with Create Trial Sites permission I want to create a trial site with an address where that trial site type requires an address so that I can add a physical address to organisational trial sites when required in my site structure', function () {

    it('User with Create Trial Sites permission is logged in to a Trial Instance with at least one Trial Site Type that requires an address to check if the required address fields have been defined', async () => {
        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(conf.baseUrl + endpointUri, {
            headers: headers,
            method: 'GET',
        })

        let response = await fetchResponse.json();
        ccoParentSiteId = response[0].siteId
        CCOData = response[0].address
        expect(CCOData).to.contain({ "address1": "address1", "address2": "address2", "address3": "address3", "address4": "address4", "address5": "address5", "city": "city", "country": "country", "postcode": "postcode" })
    });

    it('User submits an API request to create a trial site with address fields in a region where address is not configured then the user is shown an error message', async () => {
        let regionParentSiteId = requests.InvalidRegion;
        regionParentSiteId.parentSiteId = ccoParentSiteId
        const headers1 = await utils.getHeadersWithAuth()
        let fetchResponse1 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers1,
            method: 'POST',
            body: JSON.stringify(regionParentSiteId),
        })
        let regionResponse = await fetchResponse1.json();
        expect(regionResponse.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
        expect(regionResponse.message).to.eql('argument Cannot have Address failed validation')
    });

    it('User submits an API request to create a region in a trial site with address fields configured as null then the region is created successfully with the generation of an id', async () => {
        let validRegionSiteId = requests.ValidRegion;
        validRegionSiteId.parentSiteId = ccoParentSiteId
        const headers2 = await utils.getHeadersWithAuth()
        let fetchResponse2 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers2,
            method: 'POST',
            body: JSON.stringify(validRegionSiteId),
        })
        const validRegionSiteResponse = await fetchResponse2.json();
        validRegionSite = validRegionSiteResponse.id
        expect(fetchResponse2.status).to.equal(HttpStatus.CREATED)
    });

    it('User submits an API request to create a country in a trial site with address fields configured as null then the country is created successfully with the generation of an id', async () => {
        let validCountrySiteId = requests.ValidCountry
        validCountrySiteId.parentSiteId = validRegionSite
        const headers3 = await utils.getHeadersWithAuth()
        let fetchResponse3 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers3,
            method: 'POST',
            body: JSON.stringify(validCountrySiteId),
        })
        const countrySiteResponse = await fetchResponse3.json();
        countrySiteId = countrySiteResponse.id
        expect(fetchResponse3.status).to.equal(HttpStatus.CREATED)
    });

    it('User submits an API request to create a trial site with an address in LCC then an address is associated with the LCC site in the system and an acknowledgement is returned', async () => {
        let LCCParentSiteId = requests.LCCWithAddress
        LCCParentSiteId.parentSiteId = countrySiteId
        const headers4 = await utils.getHeadersWithAuth()
        let fetchResponse4 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers4,
            method: 'POST',
            body: JSON.stringify(LCCParentSiteId),
        })
        let LCCResponse = await fetchResponse4.json();
        expect(fetchResponse4.status).to.equal(HttpStatus.CREATED)
    });

    it('User submits an API request to create a trial site without an address in LCC then an error message is returned', async () => {
        let LCCIdWithoutAddress = requests.LCCWithOutAddress
        LCCIdWithoutAddress.parentSiteId = countrySiteId
        const headers5 = await utils.getHeadersWithAuth()
        let fetchResponse5 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers5,
            method: 'POST',
            body: JSON.stringify(LCCIdWithoutAddress),
        })
        let LCCWithoutAddressResponse = await fetchResponse5.json();
        expect(fetchResponse5.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
        expect(LCCWithoutAddressResponse.message).to.eql('argument No Address in payload failed validation')
    });

    it('User submits an API request to create a trial site with an address in LCC with no values populated, then an address is not associated with the trial site record and I receive an error notification', async () => {
        let EmptyAddressId = requests.emptyAddressFields
        EmptyAddressId.parentSiteId = countrySiteId
        const headers6 = await utils.getHeadersWithAuth()
        let fetchResponse6 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers6,
            method: 'POST',
            body: JSON.stringify(EmptyAddressId),
        })
        let EmptyAddressResponse = await fetchResponse6.json();
        expect(fetchResponse6.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
        expect(EmptyAddressResponse.message).to.eql('argument No Address in payload failed validation')
    });
});
