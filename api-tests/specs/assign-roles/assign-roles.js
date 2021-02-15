const requests = require('../../data/assign-roles/assign-roles')
const conf = require('../../config/conf')
const sitesEndpointUri = ':8083/sites';
const permissionsEndpointUri = ':8082/roles';
const practitionerEndpointUri = ':8081/practitioner'
const assignRoleEndpointUri = ':8081/practitioner/{personId}/roles'


describe('As a user with Assign Roles permission I want to assign roles to a user at one or more sites So that I can control what functionality they have at different parts of the site hierarchy', function () {

    it.only('GIVEN a Person, a Role and more than one site exists in the Trial Instance WHEN I submit an API request to assign an existing role to an existing person at an existing trial site THEN the person is updated and I receive a success acknowledgement ', async () => {

        // GET parent site Id from sites endpoint
        const getSitesResponse = await baseRequest.get(sitesEndpointUri);
        const captureParentSiteId = getSitesResponse.text
        let parseParentSiteIdData = JSON.parse(captureParentSiteId)
        parentId = parseParentSiteIdData[1].parentSiteId
        // request posted to sites end point
        let createSiteJSON = requests.createSite
        createSiteJSON.parentSiteId = parentId
        const siteResponse = await baseRequest.post(sitesEndpointUri).send(createSiteJSON)
        const capturesiteResponse = siteResponse.text
        let parseSiteResponseData = JSON.parse(capturesiteResponse)
        siteId = parseSiteResponseData.id
        //request posted to practitioner end point 
        const personResponse = await baseRequest.post(practitionerEndpointUri).send(requests.createPerson)
        const capturePersonResponseData = personResponse.text
        let parsePersonResponseData = JSON.parse(capturePersonResponseData)
        personId = parsePersonResponseData.id
        //request posted to role service endpoint
        const roleResponse = await baseRequest.post(permissionsEndpointUri).send(requests.roleWithPermissions)
        const captureRoleResponseData = roleResponse.text
        let parseRoleResponseData = JSON.parse(captureRoleResponseData)
        roleId = parseRoleResponseData.id

        //assign roles to a person
        let assignRoleJSON = requests.assignRole
        assignRoleJSON.siteId = siteId
        assignRoleJSON.roleId = roleId
        const response = await baseRequest.post(assignRoleEndpointUri).send(assignRoleJSON)
        expect(response.status).to.equal(HttpStatus.CREATED)
    });
});