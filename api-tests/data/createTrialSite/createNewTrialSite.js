/* data class file with/out create-site permission
author: Samee Syed
 */
const utils = require('../../common/utils')

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
        "address4": "RDrive",
        "address5": "Headington",
        "city": "Oxford",
        "country": "UK",
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
    "name": "duplicateName",
    "alias": "duplicateName",
    "parentSiteId": "",
    "siteType": "REGION"
}

module.exports = {
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
    duplicateName
}
