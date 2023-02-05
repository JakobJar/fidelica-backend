package org.fidelica.backend.repository.article;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.fidelica.backend.factcheck.FactCheck;
import org.fidelica.backend.factcheck.history.FactCheckEdit;

import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class StandardFactCheckRepository implements FactCheckRepository {

    private final MongoCollection<FactCheck> articles;
    private final MongoCollection<FactCheckEdit> edits;

    @Inject
    public StandardFactCheckRepository(@NonNull MongoDatabase database) {
        this.articles = database.getCollection("articles", FactCheck.class);
        this.edits = database.getCollection("article_edits", FactCheckEdit.class);
    }

    @Override
    public void create(FactCheck factCheck, FactCheckEdit firstEdit) {
        articles.insertOne(factCheck);
        edits.insertOne(firstEdit);
    }

    @Override
    public Optional<FactCheck> findById(ObjectId id) {
        return Optional.ofNullable(articles.find(eq("_id", id)).first());
    }
}
