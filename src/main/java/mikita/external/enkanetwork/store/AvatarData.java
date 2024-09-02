package mikita.external.enkanetwork.store;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import mikita.external.enkanetwork.QualityType;
import mikita.external.enkanetwork.WeaponType;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

@JsonSerialize(using = AvatarDataSerializer.class)
@JsonDeserialize(using = AvatarDataDeserializer.class)
public record AvatarData(
        String avatarId,
        String element,
        List<String> consts,
        List<Integer> skillOrder,
        Map<Integer, String> skills,
        Map<Integer, Short> proudMap,
        String nameTextMapHash,
        String sideIconName,
        QualityType qualityType,
        WeaponType weaponType,
        Map<String, Custome> customes
) {
}

class AvatarDataSerializer extends JsonSerializer<AvatarData> {

    @Override
    public void serialize(AvatarData value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("Element", value.element());

        gen.writeArrayFieldStart("Consts");
        for (String constant : value.consts()) {
            gen.writeString(constant);
        }
        gen.writeEndArray();

        gen.writeArrayFieldStart("SkillOrder");
        for (int order : value.skillOrder()) {
            gen.writeNumber(order);
        }
        gen.writeEndArray();

        gen.writeObjectFieldStart("Skills");
        for (Entry<Integer, String> skillEntry : value.skills().entrySet()) {
            gen.writeStringField(skillEntry.getKey().toString(), skillEntry.getValue());
        }
        gen.writeEndObject();

        gen.writeObjectFieldStart("ProudMap");
        for (Entry<Integer, Short> proudEntry : value.proudMap().entrySet()) {
            gen.writeNumberField(proudEntry.getKey().toString(), proudEntry.getValue());
        }
        gen.writeEndObject();

        gen.writeStringField("NameTextMapHash", value.nameTextMapHash());
        gen.writeStringField("SideIconName", value.sideIconName());
        gen.writeStringField("QualityType", value.qualityType().name());

        WeaponType weaponType = value.weaponType();
        if (weaponType != null) {
            gen.writeStringField("WeaponType", weaponType.name());
        }

        Map<String, Custome> customes = value.customes();
        if (customes != null) {
            gen.writeObjectFieldStart("Customes");
            for (Entry<String, Custome> customeEntry : customes.entrySet()) {
                gen.writeObjectField(customeEntry.getKey(), customeEntry.getValue());
            }
            gen.writeEndObject();
        }

        gen.writeEndObject();
    }

}

class AvatarDataDeserializer extends JsonDeserializer<AvatarData> {

    @Override
    public AvatarData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);

        String avatarId = p.currentName();
        String element = node.get("Element").asText();

        JsonNode constsList = node.get("Consts");
        List<String> consts = new ArrayList<>(constsList.size());
        for (JsonNode constNode : constsList) {
            consts.add(constNode.asText());
        }

        JsonNode skillOrderList = node.get("SkillOrder");
        List<Integer> skillOrder = new ArrayList<>(skillOrderList.size());
        for (JsonNode skillOrderNode : skillOrderList) {
            skillOrder.add(skillOrderNode.intValue());
        }

        JsonNode skillsNode = node.get("Skills");
        Map<Integer, String> skills = new HashMap<>(skillsNode.size());
        skillsNode.fields().forEachRemaining(entry -> skills.put(Integer.parseInt(entry.getKey()), entry.getValue().asText()));

        JsonNode proudMapNode = node.get("ProudMap");
        Map<Integer, Short> proudMap = new HashMap<>(proudMapNode.size());
        proudMapNode.fields().forEachRemaining(entry -> proudMap.put(Integer.parseInt(entry.getKey()), entry.getValue().shortValue()));

        String nameTextMapHash = node.get("NameTextMapHash").asText();
        String sideIconName = node.get("SideIconName").asText();
        QualityType qualityType = QualityType.valueOf(node.get("QualityType").asText());
        WeaponType weaponType = node.has("WeaponType") ? WeaponType.valueOf(node.get("WeaponType").asText()) : null;

        Map<String, Custome> customes = null;
        if (node.has("Customes")) {
            JsonNode customesNode = node.get("Customes");
            customes = new HashMap<>(customesNode.size());
            Iterator<Entry<String, JsonNode>> customesIterator = customesNode.fields();
            while (customesIterator.hasNext()) {
                Entry<String, JsonNode> entry = customesIterator.next();
                String customeId = entry.getKey();
                Custome custome = ctxt.readTreeAsValue(entry.getValue(), Custome.class);
                customes.put(customeId, custome);
            }
        }

        return new AvatarData(avatarId, element, consts, skillOrder, skills, proudMap,
                nameTextMapHash, sideIconName, qualityType, weaponType, customes);
    }

}
