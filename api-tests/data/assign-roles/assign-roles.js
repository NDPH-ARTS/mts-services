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
    assignRole
};

