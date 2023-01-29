package org.fidelica.backend.repository.serialization;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.Locale;

public class LocaleCodec implements Codec<Locale> {

    @Override
    public Locale decode(BsonReader bsonReader, DecoderContext decoderContext) {
        return Locale.forLanguageTag(bsonReader.readString());
    }

    @Override
    public void encode(BsonWriter bsonWriter, Locale locale, EncoderContext encoderContext) {
        if (locale == null) {
            bsonWriter.writeNull();
            return;
        }
        bsonWriter.writeString(locale.toLanguageTag());
    }

    @Override
    public Class<Locale> getEncoderClass() {
        return Locale.class;
    }
}
