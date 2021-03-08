const utils = require('../../common/globalUtils')

const assignPermission = {
    "id": utils.getRandomString(7),
    "permissions": [
        {
            "id": "create-person"
        }
    ]
}

const multiplePermissions = {
    "id": utils.getRandomString(7),
    "permissions": [
        {
            "id": "create-person"
        },
        {
            "id": "view-person"
        }
    ]
}

const emptyPermission = {
    "id": utils.getRandomString(7),
    "permissions": [
        {
            "id": ""
        }
    ]
}

const InvalidPermission = {
    "id": utils.getRandomString(7),
    "permissions": [
        {
            "id": "not-present"
        }
    ]
}

module.exports = {
    assignPermission,
    multiplePermissions,
    emptyPermission,
    InvalidPermission,
}