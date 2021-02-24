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

    getTokenId() {
        let form = new formData();
        form.append('grant_type', token_request['grant_type'])
        form.append('client_id', token_request.client_id)
        form.append('client_secret', token_request.client_secret)
        form.append('scope', token_request.scope)
        form.append('username', token_request.username)
        form.append('password', token_request.password)

        fetch(authUri, {
            method: 'POST',
            body: form
        })
            .then(response => response.json())
            .catch(error => console.error('Error:', error))
            .then(response => console.log('Success:', JSON.stringify(response.id_token)))
    };
}

module.exports = new utils;