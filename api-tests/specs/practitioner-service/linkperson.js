/*
Link person class file that has scenarios to run link person tests
based on Sameera Purini's createperson class file

Author - Ben Deinde
*/

const requests = require('../../data/practitioner-service/linkperson')
const conf = require('../../config/conf')
const utils = require('../../common/utils')
const fetch = require("node-fetch");
const { expect } = require('chai');
const practitionerEndpointUri = '/api/practitioner'
const linkpersonEndpointUri = '/api/practitioner/{personId}/link'


describe('As a user with Update Person permission I want to link a Person to an existing User So that they have the Persons roles when they login to the system', function () {

    it('Given a Person exists who is not linked to a User AND a User exists that is not linked to a Person When I submit an API request to link a Person who is NOT linked to a User to a User who is not linked to a Person in this trial Then the Person and User are linked and I receive a success acknowledgement ', async () => {

        //post a request to practitioner end point to create first Practitioner
        const headers = await utils.getBootStrapUserHeadersWithAuth()
        let fetchResponse = await fetch(conf.baseUrl + practitionerEndpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(requests.createFirstPerson),
        })
        let response = await fetchResponse.json()
        firstPersonId = response.id

        //link person to a user
        let linkUserJSON = requests.linkUser;
        linkpersonEndpointUri1 = conf.baseUrl + linkpersonEndpointUri.replace("{personId}", firstPersonId);

        let fetchResponse1 = await fetch(linkpersonEndpointUri1, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(linkUserJSON),
        })
        expect(fetchResponse1.status).to.equal(HttpStatus.OK)

    });

    it('Given a Person exists who is not linked to a User When I submit an API request to link the Person to a User who is already linked to a Person in this trial Then the Person and User are NOT linked and I receive an error notification ', async () => {

        //post a request to practitioner end point to create second Practitioner
        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(conf.baseUrl + practitionerEndpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(requests.createSecondPerson),
        })
        let response2 = await fetchResponse.json()
        secondPersonId = response2.id;

        //link person to a user who is already linked
        let linkUserJSON = requests.linkUser;
        linkpersonEndpointUri2 = conf.baseUrl + linkpersonEndpointUri.replace("{personId}", firstPersonId);
        
        //check application response confirms link failure
        
        let fetchResponse2 = await fetch(linkpersonEndpointUri2, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(linkUserJSON),
        })
        expect(fetchResponse2.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)

    });

    it('When I submit an API request with missing Practitioner ID to link a Person who is NOT linked to a User to a User who is not linked to a Person in this trial Then the Person and User are NOT linked and I receive a failure acknowledgement ', async () => {

        //post a request to practitioner end point to create third Practitioner
        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(conf.baseUrl + practitionerEndpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(requests.createSecondPerson),
        })
        let response3 = await fetchResponse.json()
        thirdPersonId = response3.id;
                
        //link person to a user with request missing practitioner ID
        let linkUserJSON = requests.linkUser;
        linkpersonEndpointUri3 = conf.baseUrl + linkpersonEndpointUri.replace("{personId}", " ");

        //check application response confirms link failure
        
        let fetchResponse3 = await fetch(linkpersonEndpointUri3, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(linkUserJSON),
        })
        expect(fetchResponse3.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)

    });

    it('When I submit an API request with missing User ID to link a Person who is NOT linked to a User to a User who is not linked to a Person in this trial Then the Person and User are NOT linked and I receive a failure acknowledgement ', async () => {

        //post a request to practitioner end point to create fourth Practitioner
        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(conf.baseUrl + practitionerEndpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(requests.createFourthPerson),
        })
        let response4 = await fetchResponse.json()
        fourthPersonId = response4.id;
                
        //link person to a user with request missing practitioner ID
        let linkUserJSON = requests.linkEmptyUser;
        linkpersonEndpointUri4 = conf.baseUrl + linkpersonEndpointUri.replace("{personId}", fourthPersonId);

        //check application response confirms link failure
        
        let fetchResponse4 = await fetch(linkpersonEndpointUri4, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(linkUserJSON),
        })
        expect(fetchResponse4.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)

    });
    
});