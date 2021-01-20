const validRole = {
    "id": makeid(7),
}

const emptyString = {
    "id": "",
}

const tooLongId = {
    "id": makeid(256),
}

const trialConfigPost = {

    "id": "Sameera",
    "trialName": "Hello World Trial",

    "trialSites": [
        {
            "siteName": "(Root node)",
            "siteType": "CCO"
        },
        {
            "siteName": "(Root node dup)",
            "siteType": "LLC"
        }
    ],
    "roles": [
        {
            "id": "superuser"
        },
        {
            "id": "country-admin"
        }
    ]
}


function makeid(length) {
    let result = '';
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const charactersLength = characters.length;
    for (let i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
}

module.exports = {
    validRole,
    emptyString,
    tooLongId,
    trialConfigPost,
}