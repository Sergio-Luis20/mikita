package mikita.external.enkanetwork.store;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = ProfilePicturePathDeserializer.class)
public record ProfilePicturePath(String iconId, @JsonValue String iconPath) {
}

class ProfilePicturePathDeserializer extends JsonDeserializer<ProfilePicturePath> {

    @Override
    public ProfilePicturePath deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String iconId = p.currentName();
        JsonNode node = p.getCodec().readTree(p);
        String iconPath = node.asText();
        return new ProfilePicturePath(iconId, iconPath);
    }

}
