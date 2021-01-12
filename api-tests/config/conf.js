global.expect = require('chai').expect
global.request = require('supertest')
global.HttpStatus = require('http-status-codes')

module.exports = {
    BASE_URL: process.env.BASE_URL,
    reporter: 'node_modules/mochawesome',
    'reporter-option': [
        'overwrite=true',
        'reportTitle=My\ Custom\ Title',
        'showPassed=false'
    ],
};