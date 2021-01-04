global.expect = require('chai').expect
global.request = require('supertest')
global.HttpStatus = require('http-status-codes')

module.exports = {
    BASE_URL: 'https://mts-translation-service.azurewebsites.net',
    reporter: 'node_modules/mochawesome',
    'reporter-option': [
        'overwrite=true',
        'reportTitle=My\ Custom\ Title',
        'showPassed=false'
    ],
};

