/*
create person data class file to run create person tests
author - Sameera Purini
*/
const validPerson = {
    "prefix": "Ms",
    "givenName": "Cookie",
    "familyName": "Cookie"
}

const missingPrefix = {
    "prefix": "",
    "givenName": "givenName",
    "familyName": "familyName"
}

const missingGivenName = {
    "prefix": "prefix",
    "givenName": "",
    "familyName": "familyName"
}

const missingfamilyName = {
    "prefix": "prefix",
    "givenName": "givenName",
    "familyName": ""
}

const invalidCharacterLength = {
    "prefix": "qwertyuioplkjhgfdsazxcvbnmqwertyuiw",
    "givenName": "qwertyuioplkjhgfdsazxcvbnmqwertyuiw",
    "familyName": "ghfghdfgdhfgdhgfdhfgdhgfhdgfhdgfhdgfdhgfhdgfhdgfhdgfhfgdhfgdhf"
}

const illegalCharacters = {
    "prefix": "M@£$S",
    "givenName": "S£$%T@@$E",
    "familyName": "P*(&)(*!£~D"
}

const malformedJson = {
    "\prefix": "\M@£$S",
    "\givenName": "\S£$%T@@$E",
    "\familyName": "\P*(&)(*!£~D"
}


module.exports = {
    validPerson,
    missingPrefix,
    missingGivenName,
    missingfamilyName,
    invalidCharacterLength,
    illegalCharacters,
    malformedJson
};   