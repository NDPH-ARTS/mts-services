const requests = require('../../data/createTrialSite/createNewTrialSite')
const conf = require('../../config/conf')

beforeEach(function () {
    baseRequest = request(conf.BASE_URL)
})

describe('As a user with Create Trial Sites permission I want to have the system constrain the type of trial site I can add as a child of an existing trial site and enforce validation rules defined for the site type so that I can ensure the integrity of the trial site structure meets the needs of my trial', function () {

    it('GIVEN I have Trial Site Type fields and site structure rules defined AND I have an existing trial site to act as the ‘parent’ (e.g. top node in the tree) WHEN I submit an API request to create a Trial site with a value for all mandatory fields, a unique name and a permitted parent trial site ‘type’ with no fields exceeding their specified maximum length THEN AC1 a new Trial site record is created in the system (added to the Site structure as a child of its parent) with its parent identifier, a unique identifier for itself and an acknowledgement is returned', async () => {
        const response = await baseRequest.post('/site').send(newTrialSite1);
        expect(response.status).to.equal(HttpStatus.CREATED)
    });

    it('GIVEN I have Trial Site Type fields and site structure rules defined AND I have an existing trial site to act as the ‘parent’ (e.g. top node in the tree) WHEN I submit an API request to create a Trial site with a value for all mandatory fields, a unique name and a permitted parent trial site ‘type’ with no fields exceeding their specified maximum length THEN AC1 a new Trial site record is created in the system (added to the Site structure as a child of its parent) with its parent identifier, a unique identifier for itself and an acknowledgement is returned', async () => {
        const response = await baseRequest.post('/site').send(newTrialSite2);
        expect(response.status).to.equal(HttpStatus.CREATED)
    });

    it('GIVEN I have Trial Site Type fields and site structure rules defined AND I have an existing trial site to act as the ‘parent’ (e.g. top node in the tree) WHEN I submit an API request to create a Trial site with a value for all mandatory fields, a unique name and a permitted parent trial site ‘type’ with no fields exceeding their specified maximum length THEN AC1 a new Trial site record is created in the system (added to the Site structure as a child of its parent) with its parent identifier, a unique identifier for itself and an acknowledgement is returned', async () => {
        const response = await baseRequest.post('/site').send(newTrialSite3);
        expect(response.status).to.equal(HttpStatus.CREATED)
    });

    //Kedar to confirm HttpStatus (please see my comments in ARTS-182)
    it('WHEN I submit an API request to create a new trial site with one or more missing mandatory fields, THEN AC2 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(missingFields1);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    //Kedar to confirm HttpStatus (please see my comments in ARTS-182)
    it('WHEN I submit an API request to create a new trial site with one or more missing mandatory fields, THEN AC2 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(missingFields2);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    //Kedar to confirm HttpStatus (please see my comments in ARTS-182)
    it('WHEN I submit an API request to create a new trial site with one or more missing mandatory fields, THEN AC2 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(missingFields3);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    //Kedar to confirm HttpStatus (please see my comments in ARTS-182)
    it('WHEN I submit an API request to create a Trial site with any fields exceeding their specified maximum length, THEN AC3 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(exceeding1);
        expect(response.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    //Kedar to confirm HttpStatus (please see my comments in ARTS-182)
    it('WHEN I submit an API request to create a Trial site with any fields exceeding their specified maximum length, THEN AC3 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(exceeding2);
        expect(response.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    //Kedar to confirm HttpStatus (please see my comments in ARTS-182)
    it('WHEN I submit an API request to create a Trial site with any fields exceeding their specified maximum length, THEN AC3 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(exceeding3);
        expect(response.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    //Kedar to confirm HttpStatus (please see my comments in ARTS-182)
    it('WHEN I submit an API request to create a Trial site without specifying the parent trial site THEN AC4 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(missingParent);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    //Kedar to confirm HttpStatus (please see my comments in ARTS-182)
    it('WHEN I submit an API request to create a Trial site with an invalid parent ‘type’ trial site THEN AC5 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(invalidParentType);
        expect(response.status).to.equal(HttpStatus.BAD_REQUEST)
    });

    //Kedar to confirm HttpStatus (please see my comments in ARTS-182)
    it('WHEN I submit an API request to create a Trial site and do not provide a unique name THEN AC6 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(duplicateName);
        expect(response.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    //Kedar to confirm HttpStatus (please see my comments in ARTS-182)
    it('WHEN I submit an API request to create a Trial site and do not provide a unique alias THEN AC6 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(duplicateAlias);
        expect(response.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    //Kedar to confirm HttpStatus (please see my comments in ARTS-182)
    it('WHEN I submit an API request to create a Trial site and do not provide a unique alias THEN AC6 a new record is not created and I receive an error notification', async () => {
        const response = await baseRequest.post("/site").send(duplicateBoth);
        expect(response.status).to.equal(HttpStatus.UNPROCESSABLE_ENTITY)
    });

    //Kedar to confirm HttpStatus (please see my comments in ARTS-182)
    it('WHEN a entry to create new trial site is successfully recorded THEN the user can examine its existence by sending a 	Get request at http://localhost:8080/Organisation', async () => {
        const response = await baseRequest.get("/Organisation");
        expect(response.status).to.equal(HttpStatus.OK)
        expect(response.text).to.contain("id")
    });

    //Kedar to confirm HttpStatus (please see my comments in ARTS-182)
    it('WHEN a entry to create new trial site is successfully recorded THEN the user can examine its existence by sending 	a Get request at http://localhost:8080/ResearchStudy', async () => {
        const response = await baseRequest.get("/ResearchStudy");
        expect(response.status).to.equal(HttpStatus.OK)
        expect(response.text).to.contain("id")
    });
});
