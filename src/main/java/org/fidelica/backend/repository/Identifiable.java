package org.fidelica.backend.repository;

import org.bson.types.ObjectId;

public interface Identifiable {

    ObjectId getId();
}
