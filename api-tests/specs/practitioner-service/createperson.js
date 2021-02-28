const requests = require('../../data/practitioner-service/createperson')
const conf = require('../../config/conf')
const utils = require('../../common/utils')
const fetch = require("node-fetch");
const endpointUri = 'https://as-tauth1-sc-gateway-dev.azurewebsites.net/api/practitioner';


describe('As a user with Create Person permission, I want to have my create person request validated by the system, So that I cannot create an invalid person', function () {

    it('When I submit an API request to create a Person with a value for all mandatory fields and no fields exceed their specified maximum length, Then a new Person record is persisted in the system with a unique identifier And I receive a success acknowledgement', async () => {
        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(endpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(requests.validPerson),
        })
        const response = await fetchResponse.json();
        expect(response).to.have.property('id');
        expect(fetchResponse.status).to.equal(HttpStatus.CREATED);
    });

    it('When I submit an API request to create a Person with missing non-mandatory prefix field, Then a new Person record is persisted in the system with a unique identifier And I receive a success acknowledgement', async () => {
        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(endpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(requests.missingPrefix),
        })
        const response = await fetchResponse.json();
        expect(response).to.have.property('id');
        expect(fetchResponse.status).to.equal(HttpStatus.CREATED);
    });

    it('When I submit an API request to create a Person with missing non-mandatory givenName field, Then a new Person record is persisted in the system with a unique identifier And I receive a success acknowledgement', async () => {
        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(endpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(requests.missingGivenName),
        })
        const response = await fetchResponse.json();
        expect(response).to.have.property('id');
        expect(fetchResponse.status).to.equal(HttpStatus.CREATED);
    });

    it('When I submit an API request to create a Person with missing manadatory familyName field, Then a new Person record is not created And I receive an error notification', async () => {
        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(endpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(requests.missingfamilyName),
        })
        const response = await fetchResponse.json();
        expect(response.message).to.contain('argument Family Name failed validation')
        expect(fetchResponse.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY);
    });

    it('When I submit an API request to create a Person with any fields exceeding their specified maximum length, Then a new Person record is not created And I receive an error notification', async () => {
        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(endpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(requests.invalidCharacterLength),
        })
        const response = await fetchResponse.json();
        expect(response.message).to.contain('argument Family Name failed validation')
        expect(fetchResponse.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY);
    });

    it('When I submit an API request to create a Person with any fields holding illegal characters, Then a new Person record is not created And I receive an error notification', async () => {
        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(endpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(requests.illegalCharacters),
        })
        const response = await fetchResponse.json();
        expect(response.message).to.contain('argument Prefix failed validation')
        expect(fetchResponse.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY);
    });

    it('When I submit an API request to create a Person with a malformed JSON, Then a new Person record is not created And I receive an error notification', async () => {
        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(endpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(requests.malformedJson),
        })
        const response = await fetchResponse.json();
        expect(fetchResponse.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY);
    });
});
