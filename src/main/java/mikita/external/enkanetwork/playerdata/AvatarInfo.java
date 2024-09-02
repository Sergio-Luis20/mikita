package mikita.external.enkanetwork.playerdata;

import java.util.List;
import java.util.Map;

public record AvatarInfo(
        String avatarId,
        Map<String, Prop> propMap,
        Map<String, Double> fightPropMap,
        short skillDepotId,
        List<Integer> inherentProudSkillList,
        Map<String, Byte> skillLevelMap,
        EquipList equipList,
        FetterInfo fetterInfo
) {
}
