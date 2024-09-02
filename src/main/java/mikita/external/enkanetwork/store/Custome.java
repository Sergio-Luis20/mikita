package mikita.external.enkanetwork.store;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

@JsonSerialize(using = CustomeSerializer.class)
@JsonDeserialize(using = CustomeDeserializer.class)
public record Custome(String customeId, String sideIconName, String icon, String art, int avatarId) {
}

class CustomeSerializer extends JsonSerializer<Custome> {

    @Override
    public void serialize(Custome value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("sideIconName", value.sideIconName());
        gen.writeStringField("icon", value.icon());
        gen.writeStringField("art", value.art());
        gen.writeNumberField("avatarId", value.avatarId());

        gen.writeEndArray();
    }

}

class CustomeDeserializer extends JsonDeserializer<Custome> {

    @Override
    public Custome deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);

        String customeId = p.currentName();
        String sideIconName = node.get("sideIconName").asText();
        String icon = node.get("icon").asText();
        String art = node.get("art").asText();
        int avatarId = node.get("avatarId").intValue();

        return new Custome(customeId, sideIconName, icon, art, avatarId);
    }

}

