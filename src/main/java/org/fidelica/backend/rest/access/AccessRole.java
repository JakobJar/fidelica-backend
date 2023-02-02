package org.fidelica.backend.rest.access;

import io.javalin.security.RouteRole;

public enum AccessRole implements RouteRole {
    ANONYMOUS,
    AUTHENTICATED
}
