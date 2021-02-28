const requests = require('../../data/assign-roles/assign-roles')
const conf = require('../../config/conf')
const sitesEndpointUri = ':8080/api/sites';
const rolesEndpointUri = ':8080/api/roles';
const practitionerEndpointUri = ':8080/api/practitioner'
let assignRoleEndpointUri = ':8080/api/practitioner/{personId}/roles'


describe('As a user with Assign Roles permission I want to assign roles to a user at one or more sites So that I can control what functionality they have at different parts of the site hierarchy', function () {

    it('GIVEN a Person, a Role and more than one site exists in the Trial Instance WHEN I submit an API request to assign an existing role to an existing person at an existing trial site THEN the person is updated and I receive a success acknowledgement ', async () => {

        //get parent site Id from sites endpoint
        const getSitesResponse = await baseRequest.get(sitesEndpointUri);
        const captureParentSiteId = getSitesResponse.text
        let parseParentSiteIdData = JSON.parse(captureParentSiteId)
        parentSiteId = parseParentSiteIdData[0].siteId

        //request posted to practitioner end point
        const personResponse = await baseRequest.post(practitionerEndpointUri).send(requests.createPerson)
        const capturePersonResponseData = personResponse.text
        let parsePersonResponseData = JSON.parse(capturePersonResponseData)
        personId = parsePersonResponseData.id

        //request posted to role service endpoint
        const roleResponse = await baseRequest.post(rolesEndpointUri).send(requests.roleWithPermissions)
        const captureRoleResponseData = roleResponse.text
        let parseRoleResponseData = JSON.parse(captureRoleResponseData)
        roleId = parseRoleResponseData.id

        //assign roles to a person
        let assignRoleJSON = requests.assignRole
        assignRoleJSON.siteId = parentSiteId
        assignRoleJSON.roleId = roleId
        assignRoleEndpointUri = assignRoleEndpointUri.replace("{personId}", personId);
        const response = await baseRequest.post(assignRoleEndpointUri).send(assignRoleJSON)
        expect(response.status).to.equal(HttpStatus.CREATED)
    });
});