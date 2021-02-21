package uk.ac.ox.ndph.mts.security.authorisation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@ExtendWith(MockitoExtension.class)
class AuthorisationMethodSecurityMetadataSourceTests {

    private AuthorisationMethodSecurityMetadataSource authorisationServiceMethodSecurityMetadataSource;

    @BeforeEach
    void setup() {
        this.authorisationServiceMethodSecurityMetadataSource = new AuthorisationMethodSecurityMetadataSource();
    }

    @Test
    void TestFindAttributes_WithMethodAndClass_DoesNotDeny_ForNonControllerMethods() throws Exception {
        Class<Object> clasz = Object.class;
        var method = Object.class.getMethod("equals", Object.class);
        
        var attributes = authorisationServiceMethodSecurityMetadataSource.findAttributes(method, clasz);
        
        assertThat(attributes.size(), is(0));
    }

    @Test
    void TestFindAttributes_WithMethodAndClass_ReturnsDenyAll_ForControllerMethodsWithNoPreAuthorize() throws Exception {
        Class<TestController> clasz = TestController.class;
        var method = TestController.class.getMethod("getSomething");

        var attributes = authorisationServiceMethodSecurityMetadataSource.findAttributes(method, clasz);

        assertThat(attributes.size(), is(1));
    }
    
    @Test
    void TestFindAttributes_WithMethodAndClass_DoesNotDeny_ForCorrectlyAnnotatedControllerMethods() throws Exception {
        Class<TestController> clasz = TestController.class;
        var method = TestController.class.getMethod("getSomethingElse");

        var attributes = authorisationServiceMethodSecurityMetadataSource.findAttributes(method, clasz);

        assertThat(attributes.size(), is(0));
    }
    
    @Test
    void TestFindAttributes_ReturnsEmptyList() {
        assertThat(authorisationServiceMethodSecurityMetadataSource.findAttributes(Object.class).isEmpty(),
                Matchers.is(true));
    }
    
    @Test
    void TestGetAllConfigAttributes_ReturnsEmptyList() {
        assertThat(authorisationServiceMethodSecurityMetadataSource.getAllConfigAttributes().isEmpty(),
                Matchers.is(true));
    }
}

@Controller
class TestController {
    public String getSomething() { 
        return "something";
    }

    @PreAuthorize("")
    public String getSomethingElse() { 
        return "something else";
    }
}