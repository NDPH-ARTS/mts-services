const utils = require('../../common/utils')

const createFirstPerson = {
    "prefix": "Mr",
    "givenName": "Adam",
    "familyName": "Mayweather"
}

const createSecondPerson = {
    "prefix": "Ms",
    "givenName": "Louise",
    "familyName": "Gardener"
}

const createThirdPerson = {
    "prefix": "Ms",
    "givenName": "Chelsea",
    "familyName": "Edwards"
}

const createFourthPerson = {
    "prefix": "Dr",
    "givenName": "Graham",
    "familyName": "Bell"
}

const linkUser = {
    "userAccountId": utils.getRandomString(15),
}

const linkEmptyUser = {
    
}

module.exports = {
    createFirstPerson,
    createSecondPerson,
    createThirdPerson,
    createFourthPerson,
    linkUser,
    linkEmptyUser
};   