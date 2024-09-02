package mikita.external.enkanetwork.store;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = NamecardDeserializer.class)
public record Namecard(String namecardId, @JsonValue String icon) {
}

class NamecardDeserializer extends JsonDeserializer<Namecard> {

    @Override
    public Namecard deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String namecardId = p.currentName();
        JsonNode node = p.getCodec().readTree(p);
        String icon = node.asText();
        return new Namecard(namecardId, icon);
    }

}
