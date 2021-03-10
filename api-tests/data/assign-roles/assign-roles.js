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

const assignRole = {
    "practionerId": "",
    "siteId": "",
    "roleId": ""
}

module.exports = {
    roleWithPermissions,
    assignRole
};