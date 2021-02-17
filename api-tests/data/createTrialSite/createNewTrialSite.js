//creating a parent as CCO with no existing node
const validSiteCCO = {
    "name": "CCOUK",
    "alias": "CCO",
    "parentSiteId": ""
}

//parentSiteId - the Id for the CCO should be retrieved and added for parentSiteId
const validSiteRCC = {
    "name": "UK",
    "alias": "UK",
    "parentSiteId": "",
    "siteType":"REGION"
}

//parentSiteId - the Id for the RCC should be retrieved and added for parentSiteId
const validSiteCountry = {
    "name": "ENGLAND",
    "alias": "ENGLAND",
    "parentSiteId": "",
    "siteType":"COUNTRY"
}

//parentSiteId - the Id for the Country should be retrieved and added for parentSiteId
const validSiteLCC = {
    "name": "Queen Elizabeth Hospitals",
    "alias": "QE Newcastle",
    "parentSiteId": "",
    "siteType":"LCC"
}

//parentSiteId - rccParentSiteId
const missingName = {
    "name": "UK",
    "alias": "someName",
    "parentSiteId": "",
    "siteType":"REGION"
}

//parentSiteId - rccParentSiteId
const missingAlias = {
    "name": "someName",
    "alias": "UK",
    "parentSiteId": "",
    "siteType":"REGION"
}

//parentSiteId - rccParentSiteId
const missingBoth = {
    "name": "",
    "alias": "",
    "parentSiteId": "",
    "siteType":"REGION"
}

//parentSiteId - rccParentSiteId
const exceedingName = {
    "name": "Abcdefghijklmnopqrstuvwxyzabcdefghij",
    "alias": "SomeAlias",
    "parentSiteId": "",
    "siteType":"REGION"
}

//parentSiteId - rccParentSiteId
const exceedingAlias = {
    "name": "SomeName",
    "alias": "Abcdefghijklmnopqrstuvwxyzabcdefghij",
    "parentSiteId": "",
    "siteType":"REGION"
}

//parentSiteId - rccParentSiteId
const exceedingBoth = {
    "name": "Abcdefghijklmnopqrstuvwxyzabcdefghij",
    "alias": "Abcdefghijklmnopqrstuvwxyzabcdefghij",
    "parentSiteId": "",
    "siteType":"REGION"
}

const missingParent = {
    "name": "SomeName",
    "alias": "SomeAlias",
    "parentSiteId": "",
    "siteType":"REGION"
}

const invalidParentType = {
    "name": "SomeName",
    "alias": "SomeAlias",
    "parentSiteId": "",
    "siteType":"&^%$^&*("
}

//parentSiteId - rccParentSiteId
const duplicateName = {
    "name": "rccuk",
    "alias": "someText",
    "parentSiteId": "",
    "siteType":"REGION"
}

//parentSiteId - rccParentSiteId
const duplicateAlias = {
    "name": "someText",
    "alias": "rcc",
    "parentSiteId": "",
    "siteType":"REGION"
}

//parentSiteId - rccParentSiteId
const duplicateBoth = {
    "name": "uk",
    "alias": "uk",
    "parentSiteId": "",
    "siteType":"REGION"
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
