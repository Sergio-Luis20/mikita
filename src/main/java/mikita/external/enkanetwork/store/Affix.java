package mikita.external.enkanetwork.store;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import mikita.external.enkanetwork.playerdata.AppendProp;

import java.io.IOException;

@JsonSerialize(using = AffixSerializer.class)
@JsonDeserialize(using = AffixDeserializer.class)
public record Affix(String affixId, AppendProp propType, double efficiency, byte position) {
}

class AffixSerializer extends JsonSerializer<Affix> {

    @Override
    public void serialize(Affix value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("propType", value.propType().name());
        gen.writeNumberField("efficiency", value.efficiency());
        gen.writeNumberField("position", value.position());

        gen.writeEndObject();
    }

}

class AffixDeserializer extends JsonDeserializer<Affix> {

    @Override
    public Affix deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);

        String affixId = p.currentName();
        AppendProp propType = AppendProp.valueOf(node.get("propType").asText());
        double efficienty = node.get("efficiency").doubleValue();
        byte position = Byte.parseByte(node.get("position").asText());

        return new Affix(affixId, propType, efficienty, position);
    }

}