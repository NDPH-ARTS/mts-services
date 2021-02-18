const requests = require('../../data/createTrialSite/createNewTrialSite')
const conf = require('../../config/conf')
const endpointUri = '/api/sites';
let ccoParentSiteId;
let rccParentSiteId;
let countryParentSiteId;

describe('As a user with Create Trial Sites permission I want to have the system constrain the type of trial site I can add as a child of an existing trial site and enforce validation rules defined for the site type so that I can ensure the integrity of the trial site structure meets the needs of my trial', function () {

    it('GIVEN I have Trial Site Type fields and site structure rules defined AND I DO NOT have an existing trial site to act as the ‘parent’ (e.g. top node in the tree) WHEN I submit an API request to create a Trial site with a value for all mandatory fields, a unique name and a permitted parent trial site ‘type’ with no fields exceeding their specified maximum length THEN a new Trial site record is created in the system, a unique identifier for itself and an acknowledgement is returned', async () => {

        const getSitesResponse = await baseRequest.get(endpointUri);
        const captureParentSiteId = getSitesResponse.text
        let parseParentSiteIdData = JSON.parse(captureParentSiteId)
        ccoParentSiteId = parseParentSiteIdData[0].siteId
    });

    it('GIVEN I have Trial Site Type fields and site structure rules defined AND I have an existing trial site to act as the ‘parent’ (e.g. top node in the tree) WHEN I submit an API request to create a Trial site with a value for all mandatory fields, a unique name and a permitted parent trial site ‘type’ with no fields exceeding their specified maximum length THEN AC1 a new Trial site record is created in the system (added to the Site structure as a child of its parent) with its parent identifier, a unique identifier for itself and an acknowledgement is returned', async () => {
        let ccoAsParent = requests.validSiteRCC;
        ccoAsParent.parentSiteId = ccoParentSiteId
        const rccResponseId = await baseRequest.post(endpointUri).send(ccoAsParent);
        expect(rccResponseId.status).to.equal(HttpStatus.CREATED)
        const captureRCCParentSiteResponseData = rccResponseId.text
        let parseRCCParentSiteResponseData = JSON.parse(captureRCCParentSiteResponseData)
        rccParentSiteId = parseRCCParentSiteResponseData.id
        console.log('rcc site id' + parseRCCParentSiteResponseData.id)
    });

    it('GIVEN I have Trial Site Type fields and site structure rules defined AND I have an existing trial site to act as the ‘parent’ (e.g. top node in the tree) WHEN I submit an API request to create a Trial site with a value for all mandatory fields, a unique name and a permitted parent trial site ‘type’ with no fields exceeding their specified maximum length THEN AC1 a new Trial site record is created in the system (added to the Site structure as a child of its parent) with its parent identifier, a unique identifier for itself and an acknowledgement is returned', async () => {
        let rccAsParent = requests.validSiteCountry;
        rccAsParent.parentSiteId = rccParentSiteId
        const countryResponseId = await baseRequest.post(endpointUri).send(rccAsParent);
        expect(countryResponseId.status).to.equal(HttpStatus.CREATED)
        const captureCountryParentSiteResponseData = countryResponseId.text
        let parseCountryParentSiteResponseData = JSON.parse(captureCountryParentSiteResponseData)
        countryParentSiteId = parseCountryParentSiteResponseData.id
    });

    it('GIVEN I have Trial Site Type fields and site structure rules defined AND I have an existing trial site to act as the ‘parent’ (e.g. top node in the tree) WHEN I submit an API request to create a Trial site with a value for all mandatory fields, a unique name and a permitted parent trial site ‘type’ with no fields exceeding their specified maximum length THEN AC1 a new Trial site record is created in the system (added to the Site structure as a child of its parent) with its parent identifier, a unique identifier for itself and an acknowledgement is returned', async () => {
        let countryAsParent = requests.validSiteLCC
        countryAsParent.parentSiteId = countryParentSiteId
        const lccResponseId = await baseRequest.post(endpointUri).send(countryAsParent);
        expect(lccResponseId.status).to.equal(HttpStatus.CREATED)
    });

    it('WHEN I submit an API request to create a new trial site with one or more missing mandatory fields, THEN AC2 a new record is not created and I receive an error notification', async () => {
        let missingNameRccAsParent = requests.missingName;
        missingNameRccAsParent.parentSiteId = ccoParentSiteId
        const response = await baseRequest.post(endpointUri).send(missingNameRccAsParent);
        expect(response.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    it('WHEN I submit an API request to create a new trial site with one or more missing mandatory fields, THEN AC2 a new record is not created and I receive an error notification', async () => {
        let missingAliasRccAsParent = requests.missingAlias;
        missingAliasRccAsParent.parentSiteId = ccoParentSiteId
        const response = await baseRequest.post(endpointUri).send(missingAliasRccAsParent);
        expect(response.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    it('WHEN I submit an API request to create a new trial site with one or more missing mandatory fields, THEN AC2 a new record is not created and I receive an error notification', async () => {
        let missingBothRccAsParent = requests.missingBoth;
        missingBothRccAsParent.parentSiteId = ccoParentSiteId
        const response = await baseRequest.post(endpointUri).send(missingBothRccAsParent);
        expect(response.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    it('WHEN I submit an API request to create a Trial site with any fields exceeding their specified maximum length, THEN AC3 a new record is not created and I receive an error notification', async () => {
        let exceedingNameRccAsParent = requests.exceedingName;
        exceedingNameRccAsParent.parentSiteId = ccoParentSiteId
        const response = await baseRequest.post(endpointUri).send(requests.exceedingNameRccAsParent);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    it('WHEN I submit an API request to create a Trial site with any fields exceeding their specified maximum length, THEN AC3 a new record is not created and I receive an error notification', async () => {
        let exceedingAliasRccAsParent = requests.exceedingAlias;
        exceedingAliasRccAsParent.parentSiteId = ccoParentSiteId
        const response = await baseRequest.post(endpointUri).send(requests.exceedingAliasRccAsParent);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    it('WHEN I submit an API request to create a Trial site with any fields exceeding their specified maximum length, THEN AC3 a new record is not created and I receive an error notification', async () => {
        let exceedingBothRccAsParent = requests.exceedingBoth;
        exceedingBothRccAsParent.parentSiteId = ccoParentSiteId
        const response = await baseRequest.post(endpointUri).send(requests.exceedingBothRccAsParent);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    it('WHEN I submit an API request to create a Trial site without specifying the parent trial site THEN AC4 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post(endpointUri).send(requests.missingParent);
        expect(response.status).to.equal(HttpStatus.INTERNAL_SERVER_ERROR)
    });

    it('WHEN I submit an API request to create a Trial site with an invalid parent ‘type’ trial site THEN AC5 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post(endpointUri).send(requests.invalidParentType);
        expect(response.status).to.equal(HttpStatus.INTERNAL_SERVER_ERROR)
    });

    it('WHEN I submit an API request to create a Trial site and do not provide a unique name THEN AC6 a new record is not created and I receive an error notification', async () => {
        let duplicateNameRccAsParent = requests.duplicateName;
        duplicateNameRccAsParent.parentSiteId = ccoParentSiteId
        const response = await baseRequest.post(endpointUri).send(requests.duplicateNameRccAsParent);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    it('WHEN I submit an API request to create a Trial site and do not provide a unique alias THEN AC6 a new record is not created and I receive an error notification', async () => {
        let duplicateAliasRccAsParent = requests.duplicateAlias;
        duplicateAliasRccAsParent.parentSiteId = ccoParentSiteId
        const response = await baseRequest.post(endpointUri).send(requests.duplicateAliasRccAsParent);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    it('WHEN I submit an API request to create a Trial site and do not provide a unique alias THEN AC6 a new record is not created and I receive an error notification', async () => {
        let duplicateBothRccAsParent = requests.duplicateBoth;
        duplicateBothRccAsParent.parentSiteId = ccoParentSiteId
        const response = await baseRequest.post(endpointUri).send(requests.duplicateBothRccAsParent);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    it('WHEN a entry to create new trial site is successfully recorded THEN the user can examine its existence by sending a Get request', async () => {
        const response = await baseRequest.get(endpointUri)
        expect(response.status).to.equal(HttpStatus.OK)
    });
});
