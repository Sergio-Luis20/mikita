package mikita.external.enkanetwork.playerdata;

import java.util.List;

public record ReliquaryFlat(
        String nameTextMapHash,
        byte rankLevel,
        ItemType itemType,
        String icon,
        EquipType equipType,
        String setNameTextMapHash,
        List<ReliquarySubstat> reliquarySubstats,
        ReliquaryMainstat reliquaryMainstat
) {
}
