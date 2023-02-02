package org.fidelica.backend.repository.article;

import org.bson.types.ObjectId;
import org.fidelica.backend.factcheck.FactCheck;
import org.fidelica.backend.factcheck.history.FactCheckEdit;

import java.util.Optional;

public interface FactCheckRepository {

    void create(FactCheck factCheck, FactCheckEdit firstEdit);

    Optional<FactCheck> findById(ObjectId id);

}
