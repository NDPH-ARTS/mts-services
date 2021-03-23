/*
Create New Trial Sites class file that contain scenarios covering create site entities
author: Samee Syed
*/
const requests = require('../../data/createTrialSite/createSitePermissions')
const conf = require('../../config/conf')
const endpointUri = '/api/sites';
const addPermitUri = '/api/roles/superuser/permissions';
const utils = require('../../common/utils')
const fetch = require("node-fetch");
let ccoParentSiteId;

describe('As a Chief Investigator I want all requests to create Trial Sites authorised so that only users with the required permission can create Trial Sites in the part of the tree they have permission to update', function () {

    // sending a GET request to view CCO site ID
    it('An environment configured without create-site permission, user can view existing node site', async () => {
        const headers1 = await utils.getHeadersWithAuth()
        let fetchResponse1 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers1,
            method: 'GET',
        })
        let ParentSiteIdData = await fetchResponse1.json();
        ccoParentSiteId = ParentSiteIdData[0].siteId
    });

    // attempting to create a site WITHOUT create-site permission as a superuser
    // Sending a POST request (Env does not have create-site permission)
    it('User cannot create new trial site without create-site permission', async () => {
        let ccoAsParent = requests.createSite;
        ccoAsParent.parentSiteId = ccoParentSiteId
        const headers2 = await utils.testAuth()
        let fetchResponse2 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers2,
            method: 'POST',
            body: JSON.stringify(ccoAsParent),
        })
        let regionResponse = await fetchResponse2.json();
        expect(fetchResponse2.status).to.equal(HttpStatus.FORBIDDEN)
    });

    //adding create-site permission to create a site
    it('Assigning create-site permission to a superuser', async () => {
        const headers3 = await utils.getHeadersWithAuth()
        let fetchResponse3 = await fetch(conf.baseUrl + addPermitUri, {
            headers: headers3,
            method: 'POST',
            body: JSON.stringify(requests.addCreateSitePermission),
        })
        const validRegionSiteResponse = await fetchResponse3.json();
        validRegionSite = validRegionSiteResponse.id
        expect(fetchResponse3.status).to.equal(HttpStatus.OK)
    });

    //sending a POST request after adding create-site permission
    // An environment configured without create-site permission
    it('User can create new trial site with create-site permission', async () => {
        let ccoAsParent = requests.createSite;
        ccoAsParent.parentSiteId = ccoParentSiteId
        const headers4 = await utils.getHeadersWithAuth()
        let fetchResponse4 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers4,
            method: 'POST',
            body: JSON.stringify(ccoAsParent),
        })
        let regionResponse = await fetchResponse4.json();
        expect(fetchResponse4.status).to.equal(HttpStatus.CREATED)
    });
});
