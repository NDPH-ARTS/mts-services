/*
create New Trial class file that has scenarios to run create-site tests with authorizations
author - Samee Syed
*/
const requests = require('../../data/createTrialSite/createNewTrialSite')
const conf = require('../../config/conf')
const endpointUri = '/api/sites';
const utils = require('../../common/utils')
const fetch = require("node-fetch");
let ccoParentSiteId;
let rccParentSiteId;
let countryParentSiteId;

describe('As a user with Create Trial Sites permission I want to have the system constrain the type of trial site I can add as a child of an existing trial site and enforce validation rules defined for the site type so that I can ensure the integrity of the trial site structure meets the needs of my trial', function () {

    //sending a  GET request to view CCO site ID
    it.only('GIVEN I have Trial Site Type fields(Viewing CCO) and site structure rules defined AND I DO NOT have an existing trial site to act as the ‘parent’ (e.g. top node in the tree) WHEN I submit an API request to create a Trial site with a value for all mandatory fields, a unique name and a permitted parent trial site ‘type’ with no fields exceeding their specified maximum length THEN a new Trial site record is created in the system, a unique identifier for itself and an acknowledgement is returned', async () => {
        const headers1 = await utils.getHeadersWithAuth()
        let fetchResponse1 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers1,
            method: 'GET',
        })
        let ParentSiteIdData = await fetchResponse1.json();
        ccoParentSiteId = ParentSiteIdData[0].siteId
        expect(fetchResponse1.status).to.equal(HttpStatus.OK)
    });

    //POST a request to create RCC
    it.only('GIVEN I have Trial Site Type (Region creatin) fields and site structure rules defined AND I have an existing trial site to act as the ‘parent’ (e.g. top node in the tree) WHEN I submit an API request to create a Trial site with a value for all mandatory fields, a unique name and a permitted parent trial site ‘type’ with no fields exceeding their specified maximum length THEN AC1 a new Trial site record is created in the system (added to the Site structure as a child of its parent) with its parent identifier, a unique identifier for itself and an acknowledgement is returned', async () => {
        let ccoAsParent = requests.validSiteRCC;
        ccoAsParent.parentSiteId = ccoParentSiteId
        const headers2 = await utils.getHeadersWithAuth()
        let fetchResponse2 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers2,
            method: 'POST',
            body: JSON.stringify(ccoAsParent),
        })
        let regionResponse = await fetchResponse2.json();
        expect(fetchResponse2.status).to.equal(HttpStatus.CREATED)
        rccParentSiteId = regionResponse.id
    });

    //POST a request to create COUNTRY
    it.only('GIVEN I have Trial Site Type (Country creation) fields and site structure rules defined AND I have an existing trial site to act as the ‘parent’ (e.g. top node in the tree) WHEN I submit an API request to create a Trial site with a value for all mandatory fields, a unique name and a permitted parent trial site ‘type’ with no fields exceeding their specified maximum length THEN AC1 a new Trial site record is created in the system (added to the Site structure as a child of its parent) with its parent identifier, a unique identifier for itself and an acknowledgement is returned', async () => {
        let rccAsParent = requests.validSiteCountry;
        rccAsParent.parentSiteId = rccParentSiteId
        const headers3 = await utils.getHeadersWithAuth()
        let fetchResponse3 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers3,
            method: 'POST',
            body: JSON.stringify(rccAsParent),
        })
        let countryResponse = await fetchResponse3.json();
        expect(fetchResponse3.status).to.equal(HttpStatus.CREATED)
        countryParentSiteId = countryResponse.id
    });

    //POST a request to create LCC
    it.only('GIVEN I have Trial Site Type(LCC creation) fields and site structure rules defined AND I have an existing trial site to act as the ‘parent’ (e.g. top node in the tree) WHEN I submit an API request to create a Trial site with a value for all mandatory fields, a unique name and a permitted parent trial site ‘type’ with no fields exceeding their specified maximum length THEN AC1 a new Trial site record is created in the system (added to the Site structure as a child of its parent) with its parent identifier, a unique identifier for itself and an acknowledgement is returned', async () => {
        let countryAsParent = requests.validSiteLCC
        countryAsParent.parentSiteId = countryParentSiteId
        const headers4 = await utils.getHeadersWithAuth()
        let fetchResponse4 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers4,
            method: 'POST',
            body: JSON.stringify(countryAsParent),
        })
        expect(fetchResponse4.status).to.equal(HttpStatus.CREATED)
    });

    //attempting to create an RCC with missing Name using POST
    it.only('WHEN I submit an API request (Name missing)to create a new trial site with one or more missing mandatory fields, THEN AC2 a new record is not created and I receive an error notification', async () => {
        let missingNameRccAsParent = requests.missingName;
        missingNameRccAsParent.parentSiteId = ccoParentSiteId
        const headers5 = await utils.getHeadersWithAuth()
        let fetchResponse5 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers5,
            method: 'POST',
            body: JSON.stringify(missingNameRccAsParent),
        })
        expect(fetchResponse5.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    //attempting to create an RCC with missing Alias using POST
    it.only('WHEN I submit an API request (Alias missing) to create a new trial site with one or more missing mandatory fields, THEN AC2 a new record is not created and I receive an error notification', async () => {
        let missingAliasRccAsParent = requests.missingAlias;
        missingAliasRccAsParent.parentSiteId = ccoParentSiteId
        const headers6 = await utils.getHeadersWithAuth()
        let fetchResponse6 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers6,
            method: 'POST',
            body: JSON.stringify(missingAliasRccAsParent),
        })
        expect(fetchResponse6.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
        //+correctly displayed as 422:UNPROCESSABLE_ENTITY if manually run in POSTMAN
    });

    //attempting to create an RCC with missing Name and Alias using POST
    it.only('WHEN I submit an API request(Name & Alias missing) to create a new trial site with one or more missing mandatory fields, THEN AC2 a new record is not created and I receive an error notification', async () => {
        let missingBothRccAsParent = requests.missingBoth;
        missingBothRccAsParent.parentSiteId = ccoParentSiteId
        const headers7 = await utils.getHeadersWithAuth()
        let fetchResponse7 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers7,
            method: 'POST',
            body: JSON.stringify(missingBothRccAsParent),
        })
        expect(fetchResponse7.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    //attempting to create an RCC with Name exceeding permitted character count using POST
    it.only('WHEN I submit an API request (Name exceeding) to create a Trial site with any fields exceeding their specified maximum length, THEN AC3 a new record is not created and I receive an error notification', async () => {
        let exceedingNameRccAsParent = requests.exceedingName;
        exceedingNameRccAsParent.parentSiteId = ccoParentSiteId
        const headers8 = await utils.getHeadersWithAuth()
        let fetchResponse8 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers8,
            method: 'POST',
            body: JSON.stringify(exceedingNameRccAsParent),
        })
        expect(fetchResponse8.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    //attempting to create an RCC with Alias exceeding permitted character count using POST
    it.only('WHEN I submit an API request (Alias exceeding) to create a Trial site with any fields exceeding their specified maximum length, THEN AC3 a new record is not created and I receive an error notification', async () => {
        let exceedingAliasRccAsParent = requests.exceedingAlias;
        exceedingAliasRccAsParent.parentSiteId = ccoParentSiteId
        const headers9 = await utils.getHeadersWithAuth()
        let fetchResponse9 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers9,
            method: 'POST',
            body: JSON.stringify(exceedingAliasRccAsParent),
        })
        expect(fetchResponse9.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    //attempting to create an RCC with Name and Alias exceeding permitted character count using POST
    it.only('WHEN I submit an API (Name & Alias exceeding) request to create a Trial site with any fields exceeding their specified maximum length, THEN AC3 a new record is not created and I receive an error notification', async () => {
        let exceedingBothRccAsParent = requests.exceedingBoth;
        exceedingBothRccAsParent.parentSiteId = ccoParentSiteId
        const headers10 = await utils.getHeadersWithAuth()
        let fetchResponse10 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers10,
            method: 'POST',
            body: JSON.stringify(exceedingBothRccAsParent),
        })
        expect(fetchResponse10.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    //attempting to create an RCC with missing parentSiteId using POST
    it.only('WHEN I submit an API request to create a Trial site without specifying the parent trial site THEN AC4 a new record is not created and I receive an error notification', async () => {
        const headers11 = await utils.getHeadersWithAuth()
        let fetchResponse11 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers11,
            method: 'POST',
            body: JSON.stringify(requests.missingParent),
        })
        expect(fetchResponse11.status).to.equal(HttpStatus.FORBIDDEN)
    });

    //attempting to create an RCC with missing invalidSiteType using POST
    it.only('WHEN I submit an API request to create a Trial site with an invalid parent ‘type’ trial site THEN AC5 a new record is not created and I receive an error notification', async () => {
        let invalidSiteTypeRccAsParent = requests.invalidSiteType;
        invalidSiteTypeRccAsParent.parentSiteId = ccoParentSiteId
        const headers12 = await utils.getHeadersWithAuth()
        let fetchResponse12 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers12,
            method: 'POST',
            body: JSON.stringify(invalidSiteTypeRccAsParent),
        })
        expect(fetchResponse12.status).to.equal(HttpStatus.INTERNAL_SERVER_ERROR)
    });

    //attempting to create an RCC with duplicate Name using POST
    it.only('WHEN I submit an API request to create a Trial site and do not provide a unique name THEN AC6 a new record is not created and I receive an error notification', async () => {
        const headers13 = await utils.getHeadersWithAuth()
        let fetchResponse13 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers13,
            method: 'POST',
            body: JSON.stringify(requests.duplicateName),
        })
        expect(fetchResponse13.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    //attempting to view the created trial site using Get
    it.only('WHEN a entry to create new trial site is successfully recorded THEN the user can examine its existence by sending a Get request', async () => {
        const headers14 = await utils.getHeadersWithAuth()
        let fetchResponse14 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers14,
            method: 'GET',
        })
        let sitesResponse = await fetchResponse14.json();
        ccoParentSiteId = sitesResponse[0].siteId
        expect(fetchResponse14.status).to.equal(HttpStatus.OK)
    })
});
