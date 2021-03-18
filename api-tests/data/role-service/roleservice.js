/**
 * Role services data covering roles egression test scenarios
 * 
 * Author Sameera Purini
 * 
 */

const utils = require('../../common/utils')

const validRole = {
    "id": utils.getRandomString(7),
}

const emptyString = {
    "id": "",
}

const tooLongId = {
    "id": utils.getRandomString(256),
}

module.exports = {
    validRole,
    emptyString,
    tooLongId,
}