package uk.ac.ox.ndph.mts.sample_service.security.authorisation;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.AbstractFallbackMethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.security.access.annotation.Jsr250SecurityConfig.DENY_ALL_ATTRIBUTE;

/**
 * This class enforces authorisation annotations on every endpoint in controllers.
 */
public class AuthorisationMethodSecurityMetadataSource extends AbstractFallbackMethodSecurityMetadataSource {

    /**
     * Class methods security
     * @param clazz - class
     * @return attributes
     */
    @Override
    protected Collection<ConfigAttribute> findAttributes(Class<?> clazz) {
        return new ArrayList<>();
    }

    /**
     * Methods security
     * @param method - method
     * @param targetClass - target class
     * @return attributes
     */
    @Override
    protected Collection<ConfigAttribute> findAttributes(Method method, Class<?> targetClass) {
        Annotation[] annotations = method.getAnnotations();
        ArrayList<ConfigAttribute> attributes = new ArrayList<>();

        // if the class is annotated as @Controller we should by default deny access to all methods
        if (AnnotationUtils.findAnnotation(targetClass, Controller.class) != null) {
            attributes.add(DENY_ALL_ATTRIBUTE);
        }

        if (annotations != null) {
            for (Annotation a : annotations) {
                // but not if the method has at least a PreAuthorize or PostAuthorize annotation
                if (a instanceof PreAuthorize) {
                    return new ArrayList<>();
                }
            }
        }
        return attributes;
    }

    /**
     * Get all configuration attributes
     * @return configuration attributes
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return new ArrayList<>();
    }
}
