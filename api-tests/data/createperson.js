const validPerson = {
    "prefix": "Ms",
    "givenName": "Cookie",
    "familyName": "Cookie"
}
module.exports.validPerson = validPerson;

const missingPrefix = {
    "prefix": "",
    "givenName": "givenName",
    "familyName": "familyName"
}

module.exports.missingPrefix = missingPrefix;

const missingGivenName = {
    "prefix": "prefix",
    "givenName": "",
    "familyName": "familyName"
}

module.exports.missingGivenName = missingGivenName;

const missingfamilyName = {
    "prefix": "prefix",
    "givenName": "givenName",
    "familyName": ""
}

module.exports.missingfamilyName = missingfamilyName;

const invalidCharacterLength = {
    "prefix": "",
    "givenName": "",
    "familyName": "ghfghdfgdhfgdhgfdhfgdhgfhdgfhdgfhdgfdhgfhdgfhdgfhdgfhfgdhfgdhf"
}

module.exports.invalidCharacterLength = invalidCharacterLength;