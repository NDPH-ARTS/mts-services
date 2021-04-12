/*
Permissions class file that has scenarios to run permissions tests
author - Sameera Purini
*/
const requests = require('../../data/role-service/permissions')
const conf = require('../../config/conf')
const endpointUri = '/api/roles';
const utils = require('../../common/utils')
const fetch = require("node-fetch");

describe('As a user I want to set permissions for a role so that I can decide what functionality users assigned this role will have in the system', function () {

    it('User is able to assign a permission to a role', async () => {
        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(conf.baseUrl + endpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(requests.assignPermission)
        })
        let response = await fetchResponse.json();
        expect(response).to.have.property('id');
        expect(fetchResponse.status).to.equal(HttpStatus.OK)
    });


    it('User is able to assign multiple permissions to a role', async () => {
        const headers1 = await utils.getHeadersWithAuth()
        let fetchResponse1 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers1,
            method: 'POST',
            body: JSON.stringify(requests.multiplePermissions)
        })
        let response1 = await fetchResponse1.json();
        expect(response1).to.have.property('id');
        expect(fetchResponse1.status).to.equal(HttpStatus.OK)
    });

    it('User gets a bad request error when no permission is assigned to a role', async () => {
        const headers2 = await utils.getHeadersWithAuth()
        let fetchResponse2 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers2,
            method: 'POST',
            body: JSON.stringify(requests.emptyPermission)
        })
        let response2 = await fetchResponse2.json();
        expect(fetchResponse2.status).to.equal(HttpStatus.BAD_REQUEST)
        expect(response2.message).to.eql('Permission  not found')
    });

    it('User gets a bad request error when a non-existing permission is assigned to a role', async () => {
        const headers3 = await utils.getHeadersWithAuth()
        let fetchResponse3 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers3,
            method: 'POST',
            body: JSON.stringify(requests.InvalidPermission)
        })
        let response3 = await fetchResponse3.json();
        expect(fetchResponse3.status).to.equal(HttpStatus.BAD_REQUEST)
        expect(response3.message).to.eql('Permission not-present not found')
    });

    it('When the user identity request is identical to the user by token, a Get Roles permissions request is performed, then request is completed successfully', async () => {
        const headers4 = await utils.getHeadersWithAuth()
        let fetchResponse4 = await fetch(conf.baseUrl + `${endpointUri}?ids=superuser`, {
            headers: headers4,
            method: 'GET',
        })
        expect(fetchResponse4.status).to.equal(HttpStatus.OK)
    });

    it('When the user identity request is not identical to the user by token, a Get Roles permissions request is performed, the request is failed due to not identical user.', async () => {
        const headers5 = await utils.getHeadersWithAuth()
        let fetchResponse5 = await fetch(conf.baseUrl + `${endpointUri}?ids=admin`, {
            headers: headers5,
            method: 'GET',
        })
        expect(fetchResponse5.status).to.equal(HttpStatus.FORBIDDEN)
    });

});