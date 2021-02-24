let formData = require('form-data');
const authUri = 'https://login.microsoftonline.com/5d23383f-2acb-448e-8353-4b4573b82276/oauth2/v2.0/token'
const fetch = require("node-fetch");
let token_request = require('../config/token-request.json');

class utils {

    getRandomString(length) {
        let result = '';
        const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
        const charactersLength = characters.length;
        for (let i = 0; i < length; i++) {
            result += characters.charAt(Math.floor(Math.random() * charactersLength));
        }
        return result;
    }

    async  getTokenId() {
        let form = new formData();
        form.append('grant_type', Buffer.from(token_request.grant_type, 'base64').toString('ascii'));
        form.append('client_id', Buffer.from(token_request.client_id, 'base64').toString('ascii'));
        form.append('client_secret', Buffer.from(token_request.client_secret, 'base64').toString('ascii'));
        form.append('scope', token_request.scope)
        form.append('username', Buffer.from(token_request.username, 'base64').toString('ascii'));
        form.append('password', Buffer.from(token_request.password, 'base64').toString('ascii'));
        let response = await fetch(authUri, {
            method: 'POST',
            body: form
        })

        let jsonResponse = await response.json();
        return jsonResponse.id_token;
    };

}

module.exports = new utils;