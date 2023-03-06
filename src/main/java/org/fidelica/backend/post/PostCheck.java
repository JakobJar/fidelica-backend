package org.fidelica.backend.post;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.types.ObjectId;

import java.util.Collection;

@BsonDiscriminator
public interface PostCheck {

    PostCheckRating getRating();

    String getComment();

    Collection<ObjectId> getRelatedArticles();
}
