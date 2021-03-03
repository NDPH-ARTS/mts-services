let formData = require('form-data');
const authUri = 'https://login.microsoftonline.com/5d23383f-2acb-448e-8353-4b4573b82276/oauth2/v2.0/token'
const fetch = require("node-fetch");

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

    async getTokenId() {
        let form = new formData();
        form.append('grant_type', 'password')
        form.append('client_id', (process.env.MTS_AZURE_APP_CLIENT_ID))
        form.append('scope', 'openid profile')
        form.append('username', process.env.AUTOMATION_USER_NAME)
        form.append('password', process.env.AUTOMATION_USER_PASSWORD)
        let response = await fetch(authUri, {
            method: 'POST',
            body: form
        })

        let jsonResponse = await response.json();
        return jsonResponse.id_token;
    };

    async getHeadersWithAuth() {
        const tokenId = await this.getTokenId();
        let headers = {
            'Authorization': 'Bearer ' + tokenId,
            'Content-Type': 'application/json',
            'Accept-Encoding': 'gzip, deflate, br',
            'Connection': 'keep-alive'
        };
        return headers;
    }

}

module.exports = new utils;