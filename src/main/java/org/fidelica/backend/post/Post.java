package org.fidelica.backend.post;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.fidelica.backend.repository.Identifiable;

@BsonDiscriminator
public interface Post extends Identifiable {

}
