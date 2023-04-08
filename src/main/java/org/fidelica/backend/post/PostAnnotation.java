package org.fidelica.backend.post;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.types.ObjectId;

import java.util.Collection;

@BsonDiscriminator
public interface PostAnnotation {

    PostRating getRating();

    String getNote();

    Collection<ObjectId> getRelatedArticles();

    Collection<ObjectId> getUpvoters();

    Collection<ObjectId> getDownvoters();
}
