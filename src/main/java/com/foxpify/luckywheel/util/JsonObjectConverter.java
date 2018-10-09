package com.foxpify.luckywheel.util;

import com.dslplatform.json.JsonConverter;
import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.ObjectConverter;
import io.vertx.core.json.JsonObject;

@JsonConverter(target = JsonObject.class)
public class JsonObjectConverter {
    public static final JsonReader.ReadObject<JsonObject> JSON_READER = reader -> {
        if (reader.wasNull()) return null;
        return new JsonObject(ObjectConverter.deserializeMap(reader));
    };

    public static final JsonWriter.WriteObject<JsonObject> JSON_WRITER = (writer, value) -> {
        if (value == null) {
            writer.writeNull();
        } else {
            ObjectConverter.serializeMap(value.getMap(), writer);
        }
    };
}
