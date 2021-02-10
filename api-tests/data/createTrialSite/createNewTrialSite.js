//parentSiteId - the Id for the CCO should be retrieved and added for parentSiteId
const newTrialSite1 = {
        "name": "UK Regional Coordinating Centre",
        "alias": "UK RCC",
        "parentSiteId": ""
    }
module.exports.newTrialSite1 = newTrialSite1;

//parentSiteId - the Id for the RCC should be retrieved and added for parentSiteId
const newTrialSite2 = {
        "name": "UK Country",
        "alias": "UK Country",
        "parentSiteId": ""
    }
module.exports.newTrialSite2 = newTrialSite2;

//parentSiteId - the Id for the Country should be retrieved and added for parentSiteId
const newTrialSite3 = {
        "name": "Oxford Local Clinical Centre",
        "alias": "UK LCC",
        "parentSiteId": ""
    }
module.exports.newTrialSite3 = newTrialSite3;

//parentSiteId - the Id for the CCO should be retrieved and added for parentSiteId
const missingFields1 = {
    "name": "",
    "alias": "RCC",
    "parentSiteId": ""
}
module.exports.missingFields1 = missingFields1;

//parentSiteId - the Id for the CCO should be retrieved and added for parentSiteId
const missingFields2 = {
    "name": "RCC",
    "alias": "",
    "parentSiteId": ""
}
module.exports.missingFields2 = missingFields2;

//parentSiteId - the Id for the CCO should be retrieved and added for parentSiteId
const missingFields3 = {
    "name": "",
    "alias": "",
    "parentSiteId": ""
}
module.exports.missingFields3 = missingFields3;

//parentSiteId - the Id for the CCO should be retrieved and added for parentSiteId
const exceeding1 = {
    "name": "Abcdefghijklmnopqrstuvwxyzabcdefghij",
    "alias": "RCC",
    "parentSiteId": ""
}
module.exports.exceeding1 = exceeding1;

//parentSiteId - the Id for the CCO should be retrieved and added for parentSiteId
const exceeding2 = {
    "name": "Abcdefghijklmnopqrstuvwxyzabcdefghij",
    "alias": "Abcdefghijklmnopqrstuvwxyzabcdefghij",
    "parentSiteId": ""
}
module.exports.exceeding2 = exceeding2;

//parentSiteId - the Id for the CCO should be retrieved and added for parentSiteId
const exceeding3 = {
    "name": "RCC",
    "alias": "Abcdefghijklmnopqrstuvwxyzabcdefghij",
    "parentSiteId": ""
}
module.exports.exceeding3 = exceeding3;

const missingParent = {
    "name": "RCC",
    "alias": "RCC",
    "parentSiteId": ""
}
module.exports.missingParent = missingParent;

const invalidParentType = {
    "name": "RCC",
    "alias": "RCC",
    "parentSiteId": "&^%$^&*("
}
module.exports.invalidParentType = invalidParentType;

//parentSiteId - the Id for the CCO should be retrieved and added for parentSiteId
const duplicateName = {
    "name": "UK Regional Coordinating Centre",
    "alias": "someText",
    "parentSiteId": ""
}
module.exports.duplicateName = duplicateName;

//parentSiteId - the Id for the CCO should be retrieved and added for parentSiteId
const duplicateAlias = {
    "name": "someText",
    "alias": "UK RCC",
    "parentSiteId": ""
}
module.exports.duplicateAlias = duplicateAlias;

//parentSiteId - the Id for the CCO should be retrieved and added for parentSiteId
const duplicateBoth = {
    "name": "UK Regional Coordinating Centre",
    "alias": "UK rcc",
    "parentSiteId": ""
}
module.exports.duplicateBoth = duplicateBoth;
