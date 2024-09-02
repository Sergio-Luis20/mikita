package mikita.external.enkanetwork.playerdata;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EquipType {

    EQUIP_BRACER("Flower"),
    EQUIP_NECKLACE("Feather"),
    EQUIP_SHOES("Sands"),
    EQUIP_RING("Goblet"),
    EQUIP_DRESS("Circlet");

    private final String description;

}
