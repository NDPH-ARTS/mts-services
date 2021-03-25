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
let regionASiteId;
let countryASiteId;
let regionBSiteId;
let normalRoleId;
let superUserRoleId;
let adminUserId;
let staffUserId;
let countryBSiteId;



describe('As a user with Assign Roles permission and authorisation I want to assign roles to a user at one or more sites So that I can control what functionality they have at different parts of the site hierarchy', function () {

    it('A Person, a Role and more than one site exists in the Trial Instance when I submit an API request to assign an existing role to an existing person at an existing trial site then the person is updated and I receive a success acknowledgement', async () => {

        // get parent site Id from sites endpoint
        const headers = await utils.getBootStrapUserHeadersWithAuth()
        let fetchResponse = await fetch(conf.baseUrl + sitesEndpointUri, {
            headers: headers,
            method: 'GET',
        })
        let ParentSiteIdData = await fetchResponse.json();
        ccoParentSiteId = ParentSiteIdData[0].siteId

        // get the profile Id from profile endpoint
        let fetchResponse1 = await fetch(conf.baseUrl + practitionerEndpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(createPersonrequests.validPerson),
        })
        let response = await fetchResponse1.json();
        profileId = response.id

        // request posted to role service endpoint
        let fetchResponse2 = await fetch(conf.baseUrl + rolesEndpointUri, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(requests.roleWithPermissions),
        })
        const roleResponse = await fetchResponse2.json();
        roleId = roleResponse.id
        //assign roles to a person
        let assignRoleJSON = requests.assignRole
        assignRoleJSON.siteId = ccoParentSiteId
        assignRoleJSON.practionerId = profileId
        assignRoleJSON.roleId = roleId
        let assignRoleEndpointUri1 = conf.baseUrl + assignRoleEndpointUri.replace("{profileId}", profileId);
        let fetchResponse3 = await fetch(assignRoleEndpointUri1, {
            headers: headers,
            method: 'POST',
            body: JSON.stringify(assignRoleJSON),
        })
        expect(fetchResponse3.status).to.equal(HttpStatus.CREATED)
    });

    // arts621 - create sites
    it('I create data in order to carry out the tests on assigning roles with authorisation', async () => {
        // create region A
        let regionAId = requests.regionA;
        regionAId.parentSiteId = ccoParentSiteId
        const headers1 = await utils.getBootStrapUserHeadersWithAuth()
        let fetchResponse3 = await fetch(conf.baseUrl + sitesEndpointUri, {
            headers: headers1,
            method: 'POST',
            body: JSON.stringify(regionAId),
        })
        let regionASiteIdData = await fetchResponse3.json();
        regionASiteId = regionASiteIdData.id

        // create country A under region A
        let countryAId = requests.countryA;
        countryAId.parentSiteId = regionASiteId
        let fetchResponse4 = await fetch(conf.baseUrl + sitesEndpointUri, {
            headers: headers1,
            method: 'POST',
            body: JSON.stringify(countryAId),
        })
        let countryASiteIdData = await fetchResponse4.json();
        countryASiteId = countryASiteIdData.id

        // create region B
        let regionBId = requests.regionB;
        regionBId.parentSiteId = ccoParentSiteId
        let fetchResponse5 = await fetch(conf.baseUrl + sitesEndpointUri, {
            headers: headers1,
            method: 'POST',
            body: JSON.stringify(regionBId),
        })
        let RegionBSiteIdData = await fetchResponse5.json();
        regionBSiteId = RegionBSiteIdData.id

        // create country B under region B
        let countryBId = requests.countryB;
        countryBId.parentSiteId = regionBSiteId
        let fetchResponse6 = await fetch(conf.baseUrl + sitesEndpointUri, {
            headers: headers1,
            method: 'POST',
            body: JSON.stringify(countryBId),
        })
        let countryBSiteIdData = await fetchResponse6.json();
        countryBSiteId = countryBSiteIdData.id

        // create a role with create person & assign role & view person permissions
        let fetchResponse7 = await fetch(conf.baseUrl + rolesEndpointUri, {
            headers: headers1,
            method: 'POST',
            body: JSON.stringify(requests.roleWithSuperUserPermissions),
        })
        let superUserRoleData = await fetchResponse7.json();
        superUserRoleId = superUserRoleData.id

        // create a role with only create person permissions
        let fetchResponse8 = await fetch(conf.baseUrl + rolesEndpointUri, {
            headers: headers1,
            method: 'POST',
            body: JSON.stringify(requests.roleWithOutSuperUserPermissions),
        })
        let normalRoleData = await fetchResponse8.json();
        normalRoleId = normalRoleData.id


        // Create ADMIN user #1 to link to test-automation azure user account
        let fetchResponse9 = await fetch(conf.baseUrl + practitionerEndpointUri, {
            headers: headers1,
            method: 'POST',
            body: JSON.stringify(requests.adminUser1),
        })
        let adminUserData = await fetchResponse9.json();
        adminUserId = adminUserData.id

        // Assign a super role to ADMIN user #1 on REGION A
        let assignRoleAtRegionA = requests.linkPersonRoleAtSite
        assignRoleAtRegionA.practionerId = adminUserId
        assignRoleAtRegionA.siteId = regionASiteId
        assignRoleAtRegionA.roleId = superUserRoleId
        let assignRoleEndpointUri2 = conf.baseUrl + assignRoleEndpointUri.replace("{profileId}", adminUserId);
        let fetchResponse10 = await fetch(assignRoleEndpointUri2, {
            headers: headers1,
            method: 'POST',
            body: JSON.stringify(assignRoleAtRegionA),
        })
        expect(fetchResponse10.status).to.equal(HttpStatus.CREATED)


        // Assign a NORMAL role to ADMIN user #1 on REGION B
        let assignRoleAtRegionB = requests.linkPersonRoleAtSite
        assignRoleAtRegionB.practionerId = adminUserId
        assignRoleAtRegionB.siteId = regionBSiteId
        assignRoleAtRegionB.roleId = normalRoleId
        let assignRoleEndpointUri3 = conf.baseUrl + assignRoleEndpointUri.replace("{profileId}", adminUserId);

        let fetchResponse11 = await fetch(assignRoleEndpointUri3, {
            headers: headers1,
            method: 'POST',
            body: JSON.stringify(assignRoleAtRegionB),
        })
        expect(fetchResponse11.status).to.equal(HttpStatus.CREATED)

    });

    it('AC001 - A valid API request is made to assign roles to a Staff When the request is made by a User with the Assign Roles permission at the site that the Staff is being assigned a role to Then the Staff is assigned the role(s) at the site(s)', async () => {
        // ADMIN USER # creates another USER #2 assigning ADMIN USER #2 to qa-with-create azure useraccount Id"
        const headers2 = await utils.qaHeadersWithAuth()
        let fetchResponse12 = await fetch(conf.baseUrl + practitionerEndpointUri, {
            headers: headers2,
            method: 'POST',
            body: JSON.stringify(requests.staff),
        })

        let staffUserData = await fetchResponse12.json();
        staffUserId = staffUserData.id

        // AC001
        let assignRoleAtValidRegion = requests.linkPersonRoleAtSite
        assignRoleAtValidRegion.practionerId = staffUserId
        assignRoleAtValidRegion.siteId = regionASiteId
        assignRoleAtValidRegion.roleId = normalRoleId
        let assignRoleEndpointUri4 = conf.baseUrl + assignRoleEndpointUri.replace("{profileId}", staffUserId);

        let fetchResponse13 = await fetch(assignRoleEndpointUri4, {
            headers: headers2,
            method: 'POST',
            body: JSON.stringify(assignRoleAtValidRegion),
        })
        expect(fetchResponse13.status).to.equal(HttpStatus.CREATED)

    });

    it('AC002 - When the request is made by a User without the Assign Roles permission at the site that the Staff is being assigned a role to then  the Staff is NOT assigned the role(s) at the site(s) and the user is notified of the failure', async () => {

        // AC002
        let assignRoleAtInValidRegion = requests.linkPersonRoleAtSite
        assignRoleAtInValidRegion.practionerId = staffUserId
        assignRoleAtInValidRegion.siteId = regionBSiteId
        assignRoleAtInValidRegion.roleId = normalRoleId
        let assignRoleEndpointUri5 = conf.baseUrl + assignRoleEndpointUri.replace("{profileId}", staffUserId);
        const headers3 = await utils.qaHeadersWithAuth()
        let fetchResponse14 = await fetch(assignRoleEndpointUri5, {
            headers: headers3,
            method: 'POST',
            body: JSON.stringify(assignRoleAtInValidRegion),
        })
        expect(fetchResponse14.status).to.equal(HttpStatus.FORBIDDEN)
    });

    it('AC003 - When the request is made by a User with a role that contains the Assign Roles permission at a site above that where the Staff is being assigned a role to then the Staff is assigned the role(s) at the site(s)', async () => {

        // AC003
        let assignRoleAtValidCountry = requests.linkPersonRoleAtSite
        assignRoleAtValidCountry.practionerId = staffUserId
        assignRoleAtValidCountry.siteId = countryASiteId
        assignRoleAtValidCountry.roleId = normalRoleId
        let assignRoleEndpointUri6 = conf.baseUrl + assignRoleEndpointUri.replace("{profileId}", staffUserId);
        const headers3 = await utils.qaHeadersWithAuth()
        let fetchResponse15 = await fetch(assignRoleEndpointUri6, {
            headers: headers3,
            method: 'POST',
            body: JSON.stringify(assignRoleAtValidCountry),
        })
        expect(fetchResponse15.status).to.equal(HttpStatus.CREATED)
    });

    it('AC004 - When the request is made by a User without the Assign Roles permission at the site that the Staff is being assigned a role to, but the User does have the Assign Roles permission at a site in a different branch of the site tree then AC4 the Staff is NOT assigned the role(s) at the site(s) and the user is notified of the failure', async () => {

        // AC004
        let assignRoleAtInvalidCountry = requests.linkPersonRoleAtSite
        assignRoleAtInvalidCountry.practionerId = staffUserId
        assignRoleAtInvalidCountry.siteId = countryBSiteId
        assignRoleAtInvalidCountry.roleId = normalRoleId
        let assignRoleEndpointUri7 = conf.baseUrl + assignRoleEndpointUri.replace("{profileId}", staffUserId);
        const headers4 = await utils.qaHeadersWithAuth()
        let fetchResponse16 = await fetch(assignRoleEndpointUri7, {
            headers: headers4,
            method: 'POST',
            body: JSON.stringify(assignRoleAtInvalidCountry),
        })
        expect(fetchResponse16.status).to.equal(HttpStatus.FORBIDDEN)
    });


});