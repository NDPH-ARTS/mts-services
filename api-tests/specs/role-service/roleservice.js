/*
Role services class file that has scenarios to run role service tests
author - Sameera Purini
*/
const requests = require('../../data/role-service/roleservice')
const conf = require('../../config/conf')
const endpointUri = '/api/roles';
const utils = require('../../common/utils')
const fetch = require("node-fetch");

describe('As a user I want to create roles so that they can be assigned to persons', function () {

    it('I am able to add a role to the trial site successfully', async () => {
        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(conf.baseUrl + endpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(requests.validRole)
        })
        let response = await fetchResponse.json();
        expect(response).to.have.property('id');
        expect(fetchResponse.status).to.equal(HttpStatus.OK)
    });

    it('User is prevented from saving the new role if the name is already used for an existing role', async () => {
        const roleName = requests.validRole
        const headers1 = await utils.getHeadersWithAuth()
        let fetchResponse1 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers1,
            method: 'POST',
            body: JSON.stringify(roleName)
        })
        let fetchResponse2 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers1,
            method: 'POST',
            body: JSON.stringify(roleName)
        })
        expect(fetchResponse2.status).to.equal(HttpStatus.CONFLICT)
    });

    it('User is shown a bad request error when the role name field is left empty', async () => {
        const headers2 = await utils.getHeadersWithAuth()
        let fetchResponse2 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers2,
            method: 'POST',
            body: JSON.stringify(requests.emptyString)
        })
        let response3 = await fetchResponse2.json();
        expect(fetchResponse2.status).to.equal(HttpStatus.BAD_REQUEST);
        expect(response3.errors[0].defaultMessage).to.contain('Role ID cannot be empty')
    });

    it('User is shown a bad request error when the role name field exceeds the expected string length', async () => {
        const headers3 = await utils.getHeadersWithAuth()
        let fetchResponse3 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers3,
            method: 'POST',
            body: JSON.stringify(requests.tooLongId)
        })
        let response4 = await fetchResponse3.json();
        expect(fetchResponse3.status).to.equal(HttpStatus.BAD_REQUEST);
        expect(response4.errors[0].defaultMessage).to.contain('Role ID is too long')
    });

    it('User is able to view the created roles', async () => {
        const headers5 = await utils.getHeadersWithAuth()
        let fetchResponse5 = await fetch(conf.baseUrl + `${endpointUri}?page=0&size=2`, {
            headers: headers5,
            method: 'GET',
        })
        expect(fetchResponse5.status).to.equal(HttpStatus.OK)
    });

    it('User cannot view the roles not with no permissions', async () => {
        const headers6 = await utils.getHeadersWithAuth()
        let fetchResponse6 = await fetch(conf.baseUrl + `${endpointUri}?ids=admin`, {
            headers: headers6,
            method: 'GET',
        })
        expect(fetchResponse6.status).to.equal(HttpStatus.FORBIDDEN)
    });
});