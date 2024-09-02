package mikita.external.enkanetwork.playerdata;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = EquipListDeserializer.class)
public record EquipList(List<EquipReliquary> reliquaries, EquipWeapon weapon) {

    @JsonValue
    public List<Object> toJson() {
        List<Object> equipList = new ArrayList<>(reliquaries.size() + 1);
        equipList.addAll(reliquaries);
        equipList.add(weapon);
        return equipList;
    }

}

class EquipListDeserializer extends JsonDeserializer<EquipList> {

    @Override
    public EquipList deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);
        int size = node.size();

        List<EquipReliquary> reliquaries = new ArrayList<>(size - 1);
        EquipWeapon weapon = null;

        for (JsonNode element : node) {
            if (element.has("weapon")) {
                weapon = codec.treeToValue(element, EquipWeapon.class);
            } else {
                reliquaries.add(codec.treeToValue(element, EquipReliquary.class));
            }
        }

        return new EquipList(reliquaries, weapon);
    }

}