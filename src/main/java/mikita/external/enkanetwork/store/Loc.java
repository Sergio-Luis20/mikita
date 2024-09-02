package mikita.external.enkanetwork.store;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.Map;

@JsonDeserialize(using = LocDeserializer.class)
public record Loc(String loc, @JsonValue Map<String, String> tanslations) {
}

class LocDeserializer extends JsonDeserializer<Loc> {

    @Override
    public Loc deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String loc = p.currentName();
        TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
        Map<String, String> translations = p.getCodec().readValue(p, typeRef);
        return new Loc(loc, translations);
    }

}
