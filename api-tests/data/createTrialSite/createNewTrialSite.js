const newTrialSite1 = {
    "name": "Abcdefghijklmnopqrstuvwxyzabcdefghi",
    "alias": "CCO"
}
module.exports.newTrialSite1 = newTrialSite1;

const newTrialSite2 = {
    "name": "Abcdefghijklmnopqrstuvwxyzabcdefghi",
    "alias": "Abcdefghijklmnopqrstuvwxyzabcdefghi"
}
module.exports.newTrialSite2 = newTrialSite2;

const newTrialSite3 = {
    "name": "CCO",
    "alias": "Abcdefghijklmnopqrstuvwxyzabcdefghi"
}
module.exports.newTrialSite3 = newTrialSite3;

const missingFields1 = {
    "name": "",
    "alias": "CCO"
}
module.exports.missingFields1 = missingFields1;

const missingFields2 = {
    "name": "",
    "alias": ""
}
module.exports.missingFields2 = missingFields2;

const missingFields3 = {
    "name": "CCO",
    "alias": ""
}
module.exports.missingFields3 = missingFields3;

const exceeding1 = {
    "name": "Abcdefghijklmnopqrstuvwxyzabcdefghij",
    "alias": "CCO"
}
module.exports.exceeding1 = exceeding1;

const exceeding2 = {
    "name": "Abcdefghijklmnopqrstuvwxyzabcdefghij",
    "alias": "Abcdefghijklmnopqrstuvwxyzabcdefghij"
}
module.exports.exceeding2 = exceeding2;

const exceeding3 = {
    "name": "CCO",
    "alias": "Abcdefghijklmnopqrstuvwxyzabcdefghij"
}
module.exports.exceeding3 = exceeding3;

const missingParentSiteID = {
    "name": "Region",
    "alias": "Region",
    "parentSiteId": ""
}
module.exports.missingParentSiteID = missingParentSiteID;

const duplicateName = {
    "name": "cco",
    "alias": "someText"
}
module.exports.duplicateName = duplicateName;

const duplicateAlias = {
    "name": "someText",
    "alias": "cco"
}
module.exports.duplicateAlias = duplicateAlias;

