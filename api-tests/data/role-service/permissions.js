const utils = require('../../data/common/utils')

const assignPermission = {
    "id": utils.getRandomString(7),
    "permissions": [
        {
            "id": "create-person"
        }
    ]
}

module.exports = {
    assignPermission,
}