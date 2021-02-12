const utils = require('../../data/common/utils')

const newRole = {
    "id": utils.getRandomString(7),
}

const newPerson = {
    "prefix": "Ms",
    "givenName": "Cookie",
    "familyName": "Cookie"
}

const newSite = {
    "name": "CCO",
    "alias": "CCO"
}
module.exports = {
    newPerson,
    newRole,
    newSite
};

