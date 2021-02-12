const requests = require('../../data/assign-roles/assignroles')
const conf = require('../../config/conf')
const practitionerEndpointUri = ':80/practitioner';
const rolesEndpointUri = ':82/roles';
const sitesEndpointUri = ':83/sites';
const assignRoleEndpointUri = ':80/practitioner/1234/roles'

describe('As a user with Assign Roles permissionI want to assign roles to a user at one or more sitesSo that I can control what functionality they have at different parts of the site hierarchy', function () {

    it.only('GIVEN a Person, a Role and more than one site exists in the Trial Instance WHEN I submit an API request to assign an existing role to an existing person at an existing trial site THEN AC1 the person is updated and I receive a success acknowledgement ', async () => {
        const personResponse = await baseRequest.post(practitionerEndpointUri).send(requests.newPerson);
        const capturePersonData = personResponse.text
        let parsePersonData = JSON.parse(capturePersonData)
        console.log('the response status for a person is' + parsePersonData.id)

        const roleResponse = await baseRequest.post(rolesEndpointUri).send(requests.newRole);
        const captureRoleData = roleResponse.text
        let parseRoleData = JSON.parse(captureRoleData)
        console.log('the response status for a role is' + parseRoleData.id)

        const siteResponse = await baseRequest.post(sitesEndpointUri).send(requests.newSite);
        const captureSiteData = siteResponse.text
        let parseSiteData = JSON.parse(captureSiteData)
        console.log('the response status for a site  is' + parseSiteData.id)

        let inputBody = {
            "siteId": captureSiteData,
            "roleId": captureRoleData
        }

        const assignRoleResponse = await baseRequest.post(assignRoleEndpointUri).send(inputBody);
        console.log('the response status for a site  is' + assignRoleResponse.text)

    });

});