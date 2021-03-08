const utils = require('../../common/globalUtils')
//creating a parent as CCO with no existing node
const validSiteCCO = {
    "name": "CCOUK",
    "alias": "CCO",
    "parentSiteId": ""
}

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
    "name": utils.getRandomString(5),
    "alias": utils.getRandomString(5),
    "parentSiteId": "",
    "siteType": "LCC"
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

const invalidParentType = {
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
    validSiteCCO,
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
    invalidParentType,
    duplicateName,
    duplicateAlias,
    duplicateBoth
}
