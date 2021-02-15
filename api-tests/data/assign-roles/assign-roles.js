const utils = require('../../common/utils')

const roleWithPermissions = {
    "id": utils.getRandomString(7),
    "permissions": [
        {
            "id": "create-person"
        },
        {
            "id": "view-person"
        }
    ]
}

const createPerson = {
    "prefix": "Ms",
    "givenName": "Cookie",
    "familyName": "Cookie"
}

const createSite = {
    "name": utils.getRandomString(5),
    "alias": utils.getRandomString(3),
    "parentSiteId": "5d01f252-d41c-40f8-b3c1-4bd7282fb28e",
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
    assignRole
};

