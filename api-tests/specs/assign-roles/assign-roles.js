/*
Assign role class file that has scenarios to run assign role tests
author - Sameera Purini
*/
const requests = require('../../data/assign-roles/assign-roles')
const createPersonrequests = require('../../data/practitioner-service/createperson')
const conf = require('../../config/conf')
const sitesEndpointUri = '/api/sites';
const rolesEndpointUri = '/api/roles';
const profileEndpointUri = '/api/practitioner/profile'
const practitionerEndpointUri = '/api/practitioner'
let assignRoleEndpointUri = '/api/practitioner/{profileId}/roles'
const utils = require('../../common/utils')
const fetch = require("node-fetch");
let ccoParentSiteId;



describe('As a user with Assign Roles permission and authorisation I want to assign roles to a user at one or more sites So that I can control what functionality they have at different parts of the site hierarchy', function () {

    it.only('A Person, a Role and more than one site exists in the Trial Instance when I submit an API request to assign an existing role to an existing person at an existing trial site then the person is updated and I receive a success acknowledgement', async () => {

        // get parent site Id from sites endpoint
        const headers = await utils.getHeadersWithAuth()
        let fetchResponse = await fetch(conf.baseUrl + sitesEndpointUri, {
            headers: headers,
            method: 'GET',
        })
        console.log('the headers1 are ' + JSON.stringify(headers))
        let ParentSiteIdData = await fetchResponse.json();
        ccoParentSiteId = ParentSiteIdData[0].siteId
        console.log('the cco parensite id is' + ccoParentSiteId)

        // get the profile Id from profile endpoint
        let fetchResponse1 = await fetch(conf.baseUrl + practitionerEndpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(createPersonrequests.validPerson),
        })
        console.log('the headers2 are ' + JSON.stringify(headers))
        let response = await fetchResponse1.json();
        profileId = response.id
        console.log('the profile id is' + profileId)

        // request posted to role service endpoint
        let fetchResponse2 = await fetch(conf.baseUrl + rolesEndpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(requests.roleWithPermissions),
        })
        console.log('the headers3 are ' + JSON.stringify(headers))
        const roleResponse = await fetchResponse2.json();
        roleId = roleResponse.id
        console.log('the role id is' + roleId)
        //assign roles to a person
        let assignRoleJSON = requests.assignRole
        assignRoleJSON.siteId = ccoParentSiteId
        assignRoleJSON.practionerId = profileId
        assignRoleJSON.roleId = roleId
        let assignRoleEndpointUri1 = conf.baseUrl + assignRoleEndpointUri.replace("{profileId}", profileId);
        console.log('the endpoint uri is ' + assignRoleEndpointUri1)
        let fetchResponse3 = await fetch(assignRoleEndpointUri1, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(assignRoleJSON),
        })
        console.log('the headers4 are ' + JSON.stringify(headers))
        console.log('the request posted  is' + JSON.stringify(assignRoleJSON))
        expect(fetchResponse3.status).to.equal(HttpStatus.CREATED)
    });
});