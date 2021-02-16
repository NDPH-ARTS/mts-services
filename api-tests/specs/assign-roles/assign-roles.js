const requests = require('../../data/assign-roles/assign-roles')
const conf = require('../../config/conf')
const sitesEndpointUri = ':8083/sites';
const rolesEndpointUri = ':8082/roles';
const practitionerEndpointUri = ':8081/practitioner'
let assignRoleEndpointUri = ':8081/practitioner/{personId}/roles'


describe('As a user with Assign Roles permission I want to assign roles to a user at one or more sites So that I can control what functionality they have at different parts of the site hierarchy', function () {

    it('GIVEN a Person, a Role and more than one site exists in the Trial Instance WHEN I submit an API request to assign an existing role to an existing person at an existing trial site THEN the person is updated and I receive a success acknowledgement ', async () => {

        //get parent site Id from sites endpoint
        const getSitesResponse = await baseRequest.get(sitesEndpointUri);
        const captureParentSiteId = getSitesResponse.text
        console.log('Captured Parent Site Id is' + captureParentSiteId)
        let parseParentSiteIdData = JSON.parse(captureParentSiteId)
        parentSiteId = parseParentSiteIdData[0].siteId
        console.log('Parsed Parent Site Id is' + parentSiteId)

        //request posted to practitioner end point 
        const personResponse = await baseRequest.post(practitionerEndpointUri).send(requests.createPerson)
        const capturePersonResponseData = personResponse.text
        let parsePersonResponseData = JSON.parse(capturePersonResponseData)
        personId = parsePersonResponseData.id
        console.log('Parsed Person Id is' + personId)

        //request posted to role service endpoint
        const roleResponse = await baseRequest.post(rolesEndpointUri).send(requests.roleWithPermissions)
        const captureRoleResponseData = roleResponse.text
        let parseRoleResponseData = JSON.parse(captureRoleResponseData)
        roleId = parseRoleResponseData.id
        console.log('Parsed Role Id is' + roleId)

        //assign roles to a person
        let assignRoleJSON = requests.assignRole
        assignRoleJSON.siteId = parentSiteId
        console.log('parentsiteId taken in the request is' + parentSiteId)
        assignRoleJSON.roleId = roleId
        console.log('roleId taken in the request is' + roleId)
        assignRoleEndpointUri = assignRoleEndpointUri.replace("{personId}", personId);
        console.log('the post uri is' + assignRoleEndpointUri)
        const response = await baseRequest.post(assignRoleEndpointUri).send(assignRoleJSON)
        console.log('the final response status is' + response.status)
        expect(response.status).to.equal(HttpStatus.CREATED)
    });
});