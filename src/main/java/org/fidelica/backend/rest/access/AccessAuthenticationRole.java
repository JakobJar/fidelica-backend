package org.fidelica.backend.rest.access;

import io.javalin.security.RouteRole;

public enum AccessAuthenticationRole implements RouteRole {
    ANONYMOUS,
    AUTHENTICATED
}
