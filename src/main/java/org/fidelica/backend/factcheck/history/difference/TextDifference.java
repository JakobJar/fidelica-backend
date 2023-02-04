package org.fidelica.backend.factcheck.history.difference;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator
public interface TextDifference {

    int getStartIndex();

    int getEndIndex();

    String getReplacement();
}
