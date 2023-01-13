package org.fidelica.backend.util;

import java.io.IOException;

public interface GoogleRecaptcha {

    boolean isValid(String clientResponse) throws IOException, InterruptedException;
}
