package uk.ac.ox.ctsu.arts.sitetypeservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.ac.ox.ctsu.arts.sitetypeservice.exception.NotFoundException;
import uk.ac.ox.ctsu.arts.sitetypeservice.model.SiteType;
import uk.ac.ox.ctsu.arts.sitetypeservice.model.SiteTypeRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SiteTypeControllerTest {
    private SiteTypeController siteTypeController;
    @Mock
    SiteTypeRepository siteTypeRepository;

    @BeforeEach
    void setUp() {
        siteTypeController = new SiteTypeController(siteTypeRepository);
    }

    @Test
    void getCallsRepository() {
        long id = 1;
        when(siteTypeRepository.findById(id)).thenReturn(Optional.of(new SiteType()));
        siteTypeController.get(id);
    }

    @Test
    void getPagedCallsRepository() {
        int pageNumber = 1;
        int pageSize = 40;
        @SuppressWarnings("unchecked")
        Page<SiteType> page = (Page<SiteType>) mock(Page.class);

        when(siteTypeRepository.findAll(argThat((PageRequest pr) -> pr.getPageSize() == pageSize && pr.getPageNumber() == 1)))
            .thenReturn(page);
        siteTypeController.getPaged(pageNumber, pageSize);
    }

    @Test
    void getThrowsNotFoundException() {
        long id = 42;
        assertThrows(NotFoundException.class, () -> siteTypeController.get(id));
    }

    @Test
    void createCallsRepository() {
        SiteType siteType = new SiteType();
        when(siteTypeRepository.save(siteType)).thenReturn(siteType);
        siteTypeController.create(siteType, mock(Jwt.class));
    }

    @Test
    void updateCallsRepository() {
        SiteType siteType = new SiteType();
        when(siteTypeRepository.save(siteType)).thenReturn(siteType);
        siteTypeController.update(siteType);
    }
}
