/*
data class file with/out create-site permission
author: Samee Syed
 */
const utils = require('../../common/utils')

const createSite = {
    "name": utils.getRandomString(5),
    "alias": utils.getRandomString(5),
    "parentSiteId": "",
    "siteType": "REGION"
}

module.exports = {
    createSite
}
