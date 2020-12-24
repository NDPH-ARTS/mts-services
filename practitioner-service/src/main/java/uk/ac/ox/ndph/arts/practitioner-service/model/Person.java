package uk.ac.ox.ndph.arts.practitioner_service.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;

@RequiredArgsConstructor
@Setter @Getter
public class Person {

    private final @NonNull String prefix;
    private final @NonNull String givenName;
    private final @NonNull String familyName;
}