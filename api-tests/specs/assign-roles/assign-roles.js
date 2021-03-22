/*
Assign role class file that has scenarios to run assign role tests
author - Sameera Purini
*/
const requests = require('../../data/assign-roles/assign-roles')
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
        const headers = await utils.getHeadersWithAuthBootStrapUser()
        let fetchResponse = await fetch(conf.baseUrl + sitesEndpointUri, {
            headers: headers,
            method: 'GET',
        })
        let ParentSiteIdData = await fetchResponse.json();
        ccoParentSiteId = ParentSiteIdData[0].siteId

        // get the profile Id from profile endpoint
        let fetchResponse1 = await fetch(conf.baseUrl + profileEndpointUri, {
            headers: headers,
            method: 'GET',
        })
        let response = await fetchResponse1.json();
        profileId = response[0].id

        // request posted to role service endpoint
        const roleResponse = await baseRequest.post(rolesEndpointUri).send(requests.roleWithPermissions)
        const captureRoleResponseData = roleResponse.text
        let parseRoleResponseData = JSON.parse(captureRoleResponseData)
        roleId = parseRoleResponseData.id

        //assign roles to a person
        let assignRoleJSON = requests.assignRole
        assignRoleJSON.siteId = ccoParentSiteId
        assignRoleJSON.practionerId = profileId
        assignRoleJSON.roleId = roleId
        let assignRoleEndpointUri1 = conf.baseUrl + assignRoleEndpointUri.replace("{profileId}", profileId);

        let fetchResponse2 = await fetch(assignRoleEndpointUri1, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(assignRoleJSON),
        })
        expect(fetchResponse2.status).to.equal(HttpStatus.CREATED)
    });
});