/**
 * Site Address data covering assign roles regression test scenarios
 * 
 * Author Sameera Purini
 * 
 */
const utils = require('../../common/utils')

const InvalidRegion = {
    "name": utils.getRandomString(4),
    "alias": utils.getRandomString(4),
    "parentSiteId": "",
    "siteType": "REGION",
    "address": {
        "address1": "address1",
        "address2": "address2",
        "address3": "address3",
        "address4": "address4",
        "address5": "address5",
        "city": "city",
        "country": "country",
        "postcode": "postcode",
    }
}

const ValidRegion = {
    "name": utils.getRandomString(4),
    "alias": utils.getRandomString(4),
    "parentSiteId": "",
    "siteType": "REGION",
    "address": null
}

const ValidCountry = {
    "name": utils.getRandomString(4),
    "alias": utils.getRandomString(4),
    "parentSiteId": "",
    "siteType": "COUNTRY",
    "address": null
}

const LCCWithAddress = {
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

const LCCWithOutAddress = {
    "name": utils.getRandomString(4),
    "alias": utils.getRandomString(4),
    "parentSiteId": "",
    "siteType": "LCC",
    "address": null
}

const emptyAddressFields = {
    "name": utils.getRandomString(4),
    "alias": utils.getRandomString(4),
    "parentSiteId": "",
    "siteType": "LCC",
    "address": {
        "address1": "",
        "address2": "",
        "address3": "",
        "address4": "",
        "address5": "",
        "city": "",
        "country": "",
        "postcode": "",
    }
}

module.exports = {
    InvalidRegion,
    ValidRegion,
    ValidCountry,
    LCCWithAddress,
    LCCWithOutAddress,
    emptyAddressFields
}