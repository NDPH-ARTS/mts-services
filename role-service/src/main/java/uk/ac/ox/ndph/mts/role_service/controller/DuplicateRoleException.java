package uk.ac.ox.ndph.mts.role_service.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Duplicate role ID")
public class DuplicateRoleException extends RuntimeException {
}
