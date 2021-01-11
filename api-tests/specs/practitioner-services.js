const inputData = require('../data/createPerson')
const conf = require('../config/conf')
const request = require("supertest")

beforeEach(function () {
    baseRequest = request("http://localhost:8080")
})

describe('Given the fields have been configured for Persons in the Trial Instance', function () {

    it('When I submit an API request to create a Person with a value for all mandatory fields and no fields exceed their specified maximum length, Then a new Person record is persisted in the system with a unique identifier And I receive a success acknowledgement', async () => {
        const response = await baseRequest.post('/Practitioner').send(inputData.validPerson);
        expect(response.status).to.equal(HttpStatus.OK)
    });

    it('When I submit an API request to create a Person with one or more missing mandatory fields, Then a new Person record is not created And I receive an error notification', async () => {
        const response = await baseRequest.post("/Practitioner").send(inputData.invalidPerson);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    it('When I submit an API request to create a Person with any fields exceeding their specified maximum length, Then a new Person record is not created And I receive an error notification', async () => {
        const response = await baseRequest.post("/Practitioner").send(inputData.invalidCharacterLength);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

});