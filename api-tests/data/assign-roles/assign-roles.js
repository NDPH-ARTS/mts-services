/*
assign roles class file to run assign role tests
author - Sameera Purini
*/
const utils = require('../../common/utils')

const roleWithPermissions = {
    "id": utils.getRandomString(7),
    "permissions": [
        {
            "id": "create-person"
        },
        {
            "id": "view-person"
        },
        {
            "id": "assign-role"
        }
    ]
}

const roleWithSuperUserPermissions = {
    "id": utils.getRandomString(7),
    "permissions": [
        {
            "id": "create-person"
        },
        {
            "id": "view-person"
        },
        {
            "id": "assign-role"
        }
    ]
}

const roleWithOutSuperUserPermissions = {
    "id": utils.getRandomString(7),
    "permissions": [
        {
            "id": "create-person"
        }
    ]
}

const regionA = {
    "name": utils.getRandomString(5),
    "alias": utils.getRandomString(5),
    "parentSiteId": "",
    "siteType": "REGION"
}

const countryA = {
    "name": utils.getRandomString(5),
    "alias": utils.getRandomString(5),
    "parentSiteId": "",
    "siteType": "COUNTRY"
}

const regionB = {
    "name": utils.getRandomString(5),
    "alias": utils.getRandomString(5),
    "parentSiteId": "",
    "siteType": "REGION"
}

const countryB = {
    "name": utils.getRandomString(5),
    "alias": utils.getRandomString(5),
    "parentSiteId": "",
    "siteType": "COUNTRY"
}

const adminUser1 = {
    "prefix": "Ms",
    "givenName": utils.getRandomString(5),
    "familyName": utils.getRandomString(5),
    "userAccountId": "d4b73ba8-9d76-408b-b6e1-e3b8953b39e7"
}

const staff = {
    "prefix": "Ms",
    "givenName": utils.getRandomString(5),
    "familyName": utils.getRandomString(5),
    "userAccountId": "f4c3120a-1ca4-4b59-ab6d-e362016f68fb"
}

const linkPersonRoleAtSite = {
    "practitionerId": "",
    "siteId": "",
    "roleId": ""
}

const createPerson = {
    "prefix": "Ms",
    "givenName": "Cookie",
    "familyName": "Cookie"
}

const createSite = {
    "name": utils.getRandomString(5),
    "alias": utils.getRandomString(3),
    "parentSiteId": "",
    "siteType": "REGION"
}

const assignRole = {
    "siteId": "",
    "roleId": ""
}

module.exports = {
    roleWithPermissions,
    createPerson,
    createSite,
    assignRole,
    regionA,
    regionB,
    countryA,
    countryB,
    roleWithSuperUserPermissions,
    roleWithOutSuperUserPermissions,
    adminUser1,
    linkPersonRoleAtSite,
    staff
};

