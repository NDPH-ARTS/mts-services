const requests = require('../../data/createTrialSite/createNewTrialSite')
const conf = require('../../config/conf')

beforeEach(function () {
    baseRequest = request(conf.BASE_URL)
})

describe('As a user with Create Trial Sites permission, I want to create a new trial site with a uniquename as a child of an existing site so that I can build the trial site structure to meet the needs of my trial', function () {

    it('GIVEN I’m logged in to my Trial Instance with fields defined for at least one Trial Site Type AND I have an existing trial site to act as the ‘parent’ (e.g. top node in the tree), WHEN I submit an API request to create a Trial site with a value for all mandatory fields, a uniquename and parent trial site with no fields exceeding their specified maximum length THEN AC1 a new Trial site record is created in the system (added to the Site structure as a child of its parent) with a unique identifier and an acknowledgement is returned', async () => {
        const response = await baseRequest.post('/site').send(newTrialSite1);
        expect(response.status).to.equal(HttpStatus.CREATED)
    });

    it('GIVEN I’m logged in to my Trial Instance with fields defined for at least one Trial Site Type AND I have an existing trial site to act as the ‘parent’ (e.g. top node in the tree), WHEN I submit an API request to create a Trial site with a value for all mandatory fields, a uniquename and parent trial site with no fields exceeding their specified maximum length THEN AC1 a new Trial site record is created in the system (added to the Site structure as a child of its parent) with a unique identifier and an acknowledgement is returned', async () => {
        const response = await baseRequest.post('/site').send(newTrialSite2);
        expect(response.status).to.equal(HttpStatus.CREATED)
    });

    it('GIVEN I’m logged in to my Trial Instance with fields defined for at least one Trial Site Type AND I have an existing trial site to act as the ‘parent’ (e.g. top node in the tree), WHEN I submit an API request to create a Trial site with a value for all mandatory fields, a uniquename and parent trial site with no fields exceeding their specified maximum length THEN AC1 a new Trial site record is created in the system (added to the Site structure as a child of its parent) with a unique identifier and an acknowledgement is returned', async () => {
        const response = await baseRequest.post('/site').send(newTrialSite3);
        expect(response.status).to.equal(HttpStatus.CREATED)
    });

    it('WHEN I submit an API request to create a new trial site with one or more missing mandatory fields, THEN AC2 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(missingFields1);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    it('WHEN I submit an API request to create a new trial site with one or more missing mandatory fields, THEN AC2 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(missingFields2);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    it('WHEN I submit an API request to create a new trial site with one or more missing mandatory fields, THEN AC2 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(missingFields3);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    it('WHEN I submit an API request to create a Trial site with any fields exceeding their specified maximum length, THEN AC3 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(exceeding1);
        expect(response.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    it('WHEN I submit an API request to create a Trial site with any fields exceeding their specified maximum length, THEN AC3 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(exceeding2);
        expect(response.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    it('WHEN I submit an API request to create a Trial site with any fields exceeding their specified maximum length, THEN AC3 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(exceeding3);
        expect(response.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    it('WHEN I submit an API request to create a Trial site without specifying the parent trial site THEN AC4 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(missingParentSiteID);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    it('WHEN I submit an API request to create a Trial site without specifying the parent trial site THEN AC4 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(duplicateName);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    it('WHEN I submit an API request to create a Trial site without specifying the parent trial site THEN AC4 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(duplicateAlias);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    it('some text here', async () => {
        const response = await baseRequest.get("/Organisation");
        expect(response.status).to.equal(HttpStatus.OK)
        expect(response.text).to.contain("id")
    });
});