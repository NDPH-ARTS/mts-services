const { validPerson, invalidCharacterLength, missingGivenName, missingfamilyName, missingPrefix } = require('../../data/createPerson')
const conf = require('../../config/conf')

//const request = require("supertest")

beforeEach(function () {
    baseRequest = request(conf.BASE_URL)
})

describe('Given the fields have been configured for Persons in the Trial Instance', function () {

    it('When I submit an API request to create a Person with a value for all mandatory fields and no fields exceed their specified maximum length, Then a new Person record is persisted in the system with a unique identifier And I receive a success acknowledgement', async () => {
        const response = await baseRequest.post('/practitioner').send(validPerson);
        expect(response.status).to.equal(201)
    });

    it('When I submit an API request to create a Person with missing non-mandatory prefix field, Then a new Person record is persisted in the system with a unique identifier And I receive a success acknowledgement', async () => {
        const response = await baseRequest.post("/practitioner").send(missingPrefix);
        expect(response)
        expect(response.status).to.equal(201)
    });

    it('When I submit an API request to create a Person with missing non-mandatory givenName field, Then a new Person record is persisted in the system with a unique identifier And I receive a success acknowledgement', async () => {
        const response = await baseRequest.post("/practitioner").send(missingGivenName);
        expect(response.status).to.equal(201)
    });

    it('When I submit an API request to create a Person with missing manadatory familyName field, Then a new Person record is not created And I receive an error notification', async () => {
        const response = await baseRequest.post("/practitioner").send(missingfamilyName);
        expect(response.text).to.contain("argument Family Name failed validation")
    });

    it('When I submit an API request to create a Person with missing manadatory familyName field, Then a new Person record is not created And I receive an error notification', async () => {
        const response = await baseRequest.post("/practitioner").send(missingfamilyName);
        expect(response.text).to.contain("argument Family Name failed validation")
    });

    it('When I submit an API request to create a Person with any fields exceeding their specified maximum length, Then a new Person record is not created And I receive an error notification', async () => {
        const response = await baseRequest.post("/practitioner").send(invalidCharacterLength);
        expect(response.status).to.equal(422)
        expect(response.text).to.contain("argument Family Name failed validation")
    });

});