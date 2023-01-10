package org.fidelica.backend.repository;

import org.bson.types.ObjectId;

import java.util.Optional;

public interface Repository<T extends Identifiable> {

    void create(T entity);

    Optional<T> findById(ObjectId id);
}
