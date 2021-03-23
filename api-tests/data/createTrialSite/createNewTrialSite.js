const utils = require('../../common/utils')
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

//parentSiteId - the Id for the CCO should be retrieved and added for parentSiteId
const validSiteRCC = {
    "name": utils.getRandomString(5),
    "alias": utils.getRandomString(5),
    "parentSiteId": "",
    "siteType": "REGION"
}

//parentSiteId - the Id for the RCC should be retrieved and added for parentSiteId
const validSiteCountry = {
    "name": utils.getRandomString(5),
    "alias": utils.getRandomString(5),
    "parentSiteId": "",
    "siteType": "COUNTRY"
}

//parentSiteId - the Id for the Country should be retrieved and added for parentSiteId
const validSiteLCC = {
    "name": utils.getRandomString(4),
    "alias": utils.getRandomString(4),
    "parentSiteId": "",
    "siteType": "LCC",
    "address": {
        "address1": "University of Oxford",
        "address2": "Richard Doll Building",
        "address3": "Old Road Campus",
        "address4": "",
        "address5": "Headington",
        "city": "Oxford",
        "country": "",
        "postcode": "OX3 7LF",
    }
}
//parentSiteId - rccParentSiteId
const missingName = {
    "name": "",
    "alias": utils.getRandomString(5),
    "parentSiteId": "",
    "siteType": "REGION"
}

//parentSiteId - rccParentSiteId
const missingAlias = {
    "name": utils.getRandomString(5),
    "alias": "",
    "parentSiteId": "",
    "siteType": "REGION"
}

//parentSiteId - rccParentSiteId
const missingBoth = {
    "name": "",
    "alias": "",
    "parentSiteId": "",
    "siteType": "REGION"
}

//parentSiteId - rccParentSiteId
const exceedingName = {
    "name": utils.getRandomString(36),
    "alias": utils.getRandomString(5),
    "parentSiteId": "",
    "siteType": "REGION"
}

//parentSiteId - rccParentSiteId
const exceedingAlias = {
    "name": utils.getRandomString(5),
    "alias": utils.getRandomString(36),
    "parentSiteId": "",
    "siteType": "REGION"
}

//parentSiteId - rccParentSiteId
const exceedingBoth = {
    "name": utils.getRandomString(36),
    "alias": utils.getRandomString(36),
    "parentSiteId": "",
    "siteType": "REGION"
}

const missingParent = {
    "name": utils.getRandomString(5),
    "alias": utils.getRandomString(5),
    "parentSiteId": "",
    "siteType": "REGION"
}

const invalidSiteType = {
    "name": utils.getRandomString(5),
    "alias": utils.getRandomString(5),
    "parentSiteId": "",
    "siteType": "&^%$^&*("
}

//parentSiteId - rccParentSiteId
const duplicateName = {
    "name": "rccuk",
    "alias": utils.getRandomString(5),
    "parentSiteId": "",
    "siteType": "REGION"
}

//parentSiteId - rccParentSiteId
const duplicateAlias = {
    "name": utils.getRandomString(5),
    "alias": "rcc",
    "parentSiteId": "",
    "siteType": "REGION"
}

//parentSiteId - rccParentSiteId
const duplicateBoth = {
    "name": "uk",
    "alias": "uk",
    "parentSiteId": "",
    "siteType": "REGION"
}

module.exports = {
    addCreateSitePermission,
    validSiteRCC,
    validSiteCountry,
    validSiteLCC,
    missingName,
    missingAlias,
    missingBoth,
    exceedingName,
    exceedingAlias,
    exceedingBoth,
    missingParent,
    invalidSiteType,
    duplicateName,
    duplicateAlias,
    duplicateBoth
}
