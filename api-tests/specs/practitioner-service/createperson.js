const requests = require('../../data/practitioner-service/createperson')
const conf = require('../../config/conf')
const endpointUri = ':8081/practitioner';

describe('As a user with Create Person permission, I want to have my create person request validated by the system, So that I cannot create an invalid person', function () {

    it('When I submit an API request to create a Person with a value for all mandatory fields and no fields exceed their specified maximum length, Then a new Person record is persisted in the system with a unique identifier And I receive a success acknowledgement', async () => {
        const response = await baseRequest.post(endpointUri).send(requests.validPerson);
        expect(response.text).to.contain("id")
        expect(response.status).to.equal(HttpStatus.CREATED)
    });

    it('When I submit an API request to create a Person with missing non-mandatory prefix field, Then a new Person record is persisted in the system with a unique identifier And I receive a success acknowledgement', async () => {
        const response = await baseRequest.post(endpointUri).send(requests.missingPrefix);
        expect(response.text).to.contain("id")
        expect(response.status).to.equal(HttpStatus.CREATED)
    });

    it('When I submit an API request to create a Person with missing non-mandatory givenName field, Then a new Person record is persisted in the system with a unique identifier And I receive a success acknowledgement', async () => {
        const response = await baseRequest.post(endpointUri).send(requests.missingGivenName);
        expect(response.text).to.contain("id")
        expect(response.status).to.equal(HttpStatus.CREATED)
    });

    it('When I submit an API request to create a Person with missing manadatory familyName field, Then a new Person record is not created And I receive an error notification', async () => {
        const response = await baseRequest.post(endpointUri).send(requests.missingfamilyName);
        expect(response.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
        expect(response.text).to.contain("argument Family Name failed validation")
    });

    it('When I submit an API request to create a Person with any fields exceeding their specified maximum length, Then a new Person record is not created And I receive an error notification', async () => {
        const response = await baseRequest.post(endpointUri).send(requests.invalidCharacterLength);
        expect(response.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
        expect(response.text).to.contain("argument Family Name failed validation")
    });

    it('When I submit an API request to create a Person with any fields holding illegal characters, Then a new Person record is not created And I receive an error notification', async () => {
        const response = await baseRequest.post(endpointUri).send(requests.illegalCharacters);
        expect(response.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
        expect(response.text).to.contain("argument Prefix failed validation")
    });

    it('When I submit an API request to create a Person with a malformed JSON, Then a new Person record is not created And I receive an error notification', async () => {
        const response = await baseRequest.post(endpointUri).send(requests.malformedJson);
        expect(response.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });
});
