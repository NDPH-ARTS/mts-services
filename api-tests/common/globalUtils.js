let formData = require('form-data')
const authURL = 'https://login.microsoftonline.com/5d23383f-2acb-448e-8353-4b4573b82276/oauth2/v2.0/token'
const fetch = require('node-fetch')

class globalUtils {

    getRandomString(length) {
        let result = '';
        const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
        const charactersLength = characters.length;
        for (let i = 0; i < length; i++) {
            result += characters.charAt(Math.floor(Math.random() * charactersLength));
        }
        return result;
    }


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

    async getTokenId() {
        let form = new formData();
        form.append('grant_type', 'password')
        form.append('client_id', 'f352ce15-0142-4dfa-8e18-801ee6391557')
        form.append('scope', 'openid profile')
        form.append('username', 'test-automation@mtsdevndph.onmicrosoft.com')
        form.append('password', 'kjrUB5$_S.19dTTR')
        let response = await fetch(authURL, {
            method: 'POST',
            body: form
        })
        let jsonResponse = await response.json()
        return jsonResponse.id_token;
    };

}
module.exports = new globalUtils