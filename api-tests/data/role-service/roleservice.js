const validRole = {
    "id": getRandomString(7),
}

const emptyString = {
    "id": "",
}

const tooLongId = {
    "id": getRandomString(256),
}

function getRandomString(length) {
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
}