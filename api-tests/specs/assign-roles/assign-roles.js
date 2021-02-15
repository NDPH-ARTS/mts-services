const requests = require('../../data/assign-roles/assign-roles')
const conf = require('../../config/conf')
const sitesEndpointUri = ':8083/sites';
const permissionsEndpointUri = ':8082/roles';
const practitionerEndpointUri = ':8081/practitioner'
const assignRoleEndpointUri = ':8081/practitioner/{personId}/roles'


describe('As a user with Assign Roles permission I want to assign roles to a user at one or more sites So that I can control what functionality they have at different parts of the site hierarchy', function () {

    it('GIVEN a Person, a Role and more than one site exists in the Trial Instance WHEN I submit an API request to assign an existing role to an existing person at an existing trial site THEN the person is updated and I receive a success acknowledgement ', async () => {
        const siteResponse = await baseRequest.post(sitesEndpointUri).send(requests.createSite)
        const capturesiteResponse = siteResponse.text
        let parseSiteResponseData = JSON.parse(capturesiteResponse)
        siteId = parseSiteResponseData.id
        const personResponse = await baseRequest.post(practitionerEndpointUri).send(requests.createPerson)
        const capturePersonResponseData = personResponse.text
        let parsePersonResponseData = JSON.parse(capturePersonResponseData)
        personId = parsePersonResponseData.id
        const roleResponse = await baseRequest.post(permissionsEndpointUri).send(requests.roleWithPermissions)
        const captureRoleResponseData = roleResponse.text
        let parseRoleResponseData = JSON.parse(captureRoleResponseData)
        roleId = parseRoleResponseData.id

        let assignRoleJSON = requests.assignRole
        assignRoleJSON.siteId = siteId
        assignRoleJSON.roleId = roleId
        const response = await baseRequest.post(assignRoleEndpointUri).send(assignRoleJSON)
        expect(response.status).to.equal(HttpStatus.CREATED)
    });
});