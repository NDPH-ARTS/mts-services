const validSiteTypes = {
    "id": "test_1",
    "trialName": "Define Trial Site Types",

    "trialSites": [
        {
            "siteName": "(Root node)",
            "siteType": "CCO"
        }
    ],
    "roles": [
        {
            "roleName": "Superuser"
        },
        {
            "roleName": "CCO superuser"
        }
    ],
    "siteTypes": [
        {
            "siteName": "CCO",
            "siteDescription": "CCO"
        },
        {
            "siteName": "Region",
            "siteDescription": "Region"
        }
    ]
}
module.exports.validSiteTypes = validSiteTypes;

const missingsiteName = {
    "id": "test_1",
    "trialName": "Hello World Trial",

    "trialSites": [
        {
            "siteName": "(Root node)",
            "siteType": "CCO"
        }
    ],
    "roles": [
        {
            "roleName": "Superuser"
        },
        {
            "roleName": "CCO superuser"
        }
    ],
    "siteTypes": [
        {
            "siteName": "",
            "siteDescription": "siteDescription"
        }
    ]
}
module.exports.missingsiteName = missingsiteName;

const missingsiteDescription = {
    "id": "test_1",
    "trialName": "Hello World Trial",

    "trialSites": [
        {
            "siteName": "(Root node)",
            "siteType": "CCO"
        }
    ],
    "roles": [
        {
            "roleName": "Superuser"
        },
        {
            "roleName": "CCO superuser"
        }
    ],
    "siteTypes": [
        {
            "siteName": "siteName",
            "siteDescription": ""
        }
    ]
}
module.exports.missingsiteDescription = missingsiteDescription;

// case insensitive/duplicate validation for siteName
const duplicatesiteName = {
    "id": "test_1",
    "trialName": "Hello World Trial",

    "trialSites": [
        {
            "siteName": "(Root node)",
            "siteType": "CCO"
        }
    ],
    "roles": [
        {
            "roleName": "Superuser"
        },
        {
            "roleName": "CCO superuser"
        }
    ],
    "siteTypes": [
        {
            "siteName": "cco",
            "siteDescription": "SomeSiteDescription"
        }
    ]
}
module.exports.duplicatesiteName = duplicatesiteName;

// case insensitive/duplicate validation for siteDescription
const duplicatesiteDescription = {
    "id": "test_1",
    "trialName": "Hello World Trial",

    "trialSites": [
        {
            "siteName": "(Root node)",
            "siteType": "CCO"
        }
    ],
    "roles": [
        {
            "roleName": "Superuser"
        },
        {
            "roleName": "CCO superuser"
        }
    ],
    "siteTypes": [
        {
            "siteName": "SomeSiteName",
            "siteDescription": "cco"
        }
    ]
}
module.exports.duplicatesiteDescription = duplicatesiteDescription;