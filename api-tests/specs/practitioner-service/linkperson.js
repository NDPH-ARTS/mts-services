const requests = require('../../data/practitioner-service/linkperson')
const conf = require('../../config/conf')
const practitionerEndpointUri = ':8081/practitioner'
let linkpersonEndpointUri = ':8081/practitioner/{personId}/link'


describe('As a user with Update Person permission I want to link a Person to an existing User So that they have the Persons roles when they login to the system', function () {

    it('Given a Person exists who is not linked to a User AND a User exists that is not linked to a Person When I submit an API request to link a Person who is NOT linked to a User to a User who is not linked to a Person in this trial Then the Person and User are linked and I receive a success acknowledgement ', async () => {

        //post a request to practitioner end point to create first Practitioner
        const firstPersonResponse = await baseRequest.post(practitionerEndpointUri).send(requests.createFirstPerson)
        const captureFirstPersonResponseData = firstPersonResponse.text
        let parseFirstPersonResponseData = JSON.parse(captureFirstPersonResponseData)
        firstPersonId = parseFirstPersonResponseData.id

        //link person to a user
        let linkUserJSON = requests.linkUser
        linkUserJSON.userId = newUserId
        linkpersonEndpointUri = linkpersonEndpointUri.replace("{personId}", firstPersonId);
        const response = await baseRequest.post(linkpersonEndpointUri).send(linkUserJSON)

        //check application response confirms link success
        expect(response.status).to.equal(HttpStatus.OK)

    });

    it('Given a Person exists who is not linked to a User When I submit an API request to link the Person to a User who is already linked to a Person in this trial Then the Person and User are NOT linked and I receive an error notification ', async () => {

        //post a request to practitioner end point to create second Practitioner
        const secondPersonResponse = await baseRequest.post(practitionerEndpointUri).send(requests.createSecondPerson)
        const captureSecondPersonResponseData = secondPersonResponse.text
        let parseSecondPersonResponseData = JSON.parse(captureSecondPersonResponseData)
        secondPersonId = parseSecondPersonResponseData.id

        //link person to a user who is already linked
        let linkUserJSON = requests.linkUser
        linkUserJSON.userId = newUserId
        linkpersonEndpointUri = linkpersonEndpointUri.replace("{personId}", secondPersonId);
        const response = await baseRequest.post(linkpersonEndpointUri).send(linkUserJSON)

        //check application response confirms link failure
        expect(response.error).to.equal("Unprocessable Entity")

    });

    
});