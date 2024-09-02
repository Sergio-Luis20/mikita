package mikita.external.enkanetwork.playerdata;

import java.util.List;

public record WeaponFlat(
        String nameTextMapHash,
        byte rankLevel,
        ItemType itemType,
        String icon,
        List<WeaponStat> weaponStats
) {
}
