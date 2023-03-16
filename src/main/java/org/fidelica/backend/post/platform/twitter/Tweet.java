package org.fidelica.backend.post.platform.twitter;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.fidelica.backend.post.Post;

@BsonDiscriminator
public interface Tweet extends Post {

    long getId();
}
