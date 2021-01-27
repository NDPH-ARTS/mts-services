global.expect = require('chai').expect
global.request = require('supertest')
global.HttpStatus = require('http-status-codes')

module.exports = {
    baseUrl: process.env.BASE_URL,
    reporter: 'node_modules/mochawesome',
    'reporter-option': [
        'overwrite=true',
        'reportTitle=My\ Custom\ Title',
        'showPassed=false'
    ],
};
beforeEach(function () {
    baseRequest = request(module.exports.baseUrl)
})