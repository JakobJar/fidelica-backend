package org.fidelica.backend.user;

import org.bson.types.ObjectId;
import org.fidelica.backend.repository.Identifiable;

import java.util.Collection;
import java.util.Optional;

public interface Manager<T extends Identifiable> {

    void register(T entity);

    void unregister(T entity);

    Optional<T> getById(ObjectId id);

    Collection<T> getEntities();
}
