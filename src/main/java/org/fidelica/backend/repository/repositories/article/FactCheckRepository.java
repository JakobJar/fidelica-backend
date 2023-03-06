package org.fidelica.backend.repository.repositories.article;

import org.bson.types.ObjectId;
import org.fidelica.backend.factcheck.FactCheck;
import org.fidelica.backend.factcheck.history.FactCheckEdit;

import java.util.List;
import java.util.Optional;

public interface FactCheckRepository {

    void create(FactCheck factCheck, FactCheckEdit firstEdit);

    Optional<FactCheck> findById(ObjectId id);

    Optional<FactCheck> findPreviewById(ObjectId id);

    Optional<FactCheckEdit> findEditById(ObjectId id);

    List<FactCheckEdit> getEditPreviews(ObjectId factcheckId, int limit, int offset);

    List<FactCheckEdit> getEditDifferencesAfter(ObjectId factcheckId, ObjectId editId);
}
