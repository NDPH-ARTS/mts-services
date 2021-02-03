const requests = require('../../data/role-service/permissions')
const conf = require('../../config/conf')
const endpointUri = ':82/roles';

describe('As a user I want to set permissions for a role so that I can decide what functionality users assigned this role will have in the system', function () {

    it('User is able to assign a permission to a role', async () => {
        const response = await baseRequest.post(endpointUri).send(requests.assignPermission);
        expect(response.text).to.contain("id")
        expect(response.status).to.equal(HttpStatus.OK)
    });


    it('User is able to assign multiple permissions to a role', async () => {
        const response = await baseRequest.post(endpointUri).send(requests.multiplePermissions);
        expect(response.text).to.contain("id")
        expect(response.status).to.equal(HttpStatus.OK)
    });

    it('User gets a bad request error when no permission is assigned to a role', async () => {
        const response = await baseRequest.post(endpointUri).send(requests.emptyPermission);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST);
        expect(response.text).to.contain("Permission  not found")
    });

    it('User gets a bad request error when a non-existing permission is assigned to a role', async () => {
        const response = await baseRequest.post(endpointUri).send(requests.InvalidPermission);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST);
        expect(response.text).to.contain("Permission not-present not found")
    });

    it('User is able to view the created roles', async () => {
        const response = await baseRequest.get(`${endpointUri}?page=0&size=2`);
        expect(response.status).to.equal(HttpStatus.OK)
        expect(response.text).to.contain("id");
    });

});