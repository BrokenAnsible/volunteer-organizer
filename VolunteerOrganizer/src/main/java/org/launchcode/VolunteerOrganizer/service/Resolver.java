package org.launchcode.VolunteerOrganizer.service;

import java.util.Optional;

public interface Resolver<E, T> {
    Optional<T> resolve(E input);
}
