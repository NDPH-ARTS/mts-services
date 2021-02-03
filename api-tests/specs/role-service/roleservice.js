const requests = require('../../data/role-service/roleservice')
const conf = require('../../config/conf')
const endpointUri = ':82/roles';

describe('As a user I want to create roles so that they can be assigned to persons', function () {

    it('I am able to add a role to the trial site successfully', async () => {
        const response = await baseRequest.post(endpointUri).send(requests.validRole);
        expect(response.text).to.contain("id")
        expect(response.status).to.equal(HttpStatus.OK)
    });

    it('User is prevented from saving the new role if the name is already used for an existing role', async () => {
        const roleName = requests.validRole
        let response = await baseRequest.post(endpointUri).send(roleName);
        response = await baseRequest.post(endpointUri).send(roleName);
        expect(response.status).to.equal(HttpStatus.CONFLICT)
        expect(response.text).to.contain("Duplicate role ID")
    });

    it('User is shown a bad request error when the role name field is left empty', async () => {
        const response = await baseRequest.post(endpointUri).send(requests.emptyString);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST);
        expect(response.text).to.contain("Role ID cannot be empty")
    });

    it('User is shown a bad request error when the role name field exceeds the expected string length', async () => {
        const response = await baseRequest.post(endpointUri).send(requests.tooLongId);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST);
        expect(response.text).to.contain("Role ID is too long")
    });

    it('User is able to view the created roles', async () => {
        const response = await baseRequest.get(`${endpointUri}?page=0&size=2`);
        expect(response.status).to.equal(HttpStatus.OK)
        expect(response.text).to.contain("id");
    });

});