/*
sites extensions data class file to run sites extensions tests
author - Sameera Purini
*/
const utils = require('../../common/utils')

const regionWithExt = {
    "name": utils.getRandomString(4),
    "alias": utils.getRandomString(4),
    "parentSiteId": "",
    "siteType": "REGION",
    "address": null,
    "extensions": null
}

const countryWithExt = {
    "name": utils.getRandomString(4),
    "alias": utils.getRandomString(4),
    "parentSiteId": "",
    "siteType": "COUNTRY",
    "address": null,
    "extensions": {
        "countryCode": "UK"
    }
}

const lccWithExt = {
    "name": utils.getRandomString(4),
    "alias": utils.getRandomString(4),
    "parentSiteId": "",
    "siteType": "LCC",
    "address": {
        "address1": "University of Oxford",
        "address2": "Richard Doll Building",
        "address3": "Old Road Campus",
        "address4": "",
        "address5": "Headington",
        "city": "Oxford",
        "country": "",
        "postcode": "OX3 7LF",
    }

}

module.exports = {
    regionWithExt,
    countryWithExt,
    lccWithExt
}