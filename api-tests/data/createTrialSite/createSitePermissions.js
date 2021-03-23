/*
data class file with/out create-site permission
author: Samee Syed
 */
const utils = require('../../common/utils')

const createSite = {
    "name": utils.getRandomString(5),
    "alias": utils.getRandomString(5),
    "parentSiteId": "",
    "siteType": "REGION"
}

//POST: <https://as-<new branch2>-sc-gateway-dev.azurewebsites.net//api/roles/superuser/permissions
const addCreateSitePermission =
    [
        {
            "id": "create-site"
        },
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

module.exports = {
    createSite,
    addCreateSitePermission
}
