package org.fidelica.backend.util;

import java.io.IOException;

public class DummyGoogleRecaptcha implements GoogleRecaptcha {

    @Override
    public boolean isValid(String clientResponse) throws IOException, InterruptedException {
        return true;
    }
}
