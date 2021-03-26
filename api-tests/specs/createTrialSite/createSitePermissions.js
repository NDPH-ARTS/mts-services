/*
Create New Trial Sites class file that contain scenarios covering create site entities
author: Samee Syed
*/
const requests = require('../../data/createTrialSite/createSitePermissions')
const conf = require('../../config/conf')
const endpointUri = '/api/sites';
const utils = require('../../common/utils')
const fetch = require("node-fetch");
let ccoParentSiteId;

describe('As a Chief Investigator I want all requests to create Trial Sites authorised so that only users with the required permission can create Trial Sites in the part of the tree they have permission to update', function () {

    // sending a GET request to view CCO site ID as a superuser
    it('User should be able to view existing node site', async () => {
        const headers1 = await utils.getHeadersWithAuth()
        let fetchResponse1 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers1,
            method: 'GET',
        })
        let ParentSiteIdData = await fetchResponse1.json();
        ccoParentSiteId = ParentSiteIdData[0].siteId
        expect(fetchResponse1.status).to.equal(HttpStatus.OK)
    });

    //sending a POST request with create-site permission(superuser-bootstrap user)
    it('User can create new trial site with create-site permission', async () => {
        let ccoAsParent = requests.createSite;
        ccoAsParent.parentSiteId = ccoParentSiteId
        const headers2 = await utils.getHeadersWithAuth()
        let fetchResponse2 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers2,
            method: 'POST',
            body: JSON.stringify(ccoAsParent),
        })
        let regionResponse = await fetchResponse2.json();
        expect(fetchResponse2.status).to.equal(HttpStatus.CREATED)
        rccParentSiteId = regionResponse.id
    });

    // attempting to create a site WITHOUT create-site permission (admin-qa create)
    // Sending a POST request
    it('User cannot create new trial site without create-site permission', async () => {
        let ccoAsParent = requests.createSite;
        ccoAsParent.parentSiteId = ccoParentSiteId
        const headers3 = await utils.qaHeadersWithAuth()
        //  console.log('this is the token id' + JSON.stringify(headers3))
        let fetchResponse3 = await fetch(conf.baseUrl + endpointUri, {
            headers: headers3,
            method: 'POST',
            body: JSON.stringify(ccoAsParent),
        })
        console.log('some text'+ JSON.stringify(conf.baseUrl + endpointUri))
        expect(fetchResponse3.status).to.equal(HttpStatus.FORBIDDEN)
    });
});
