package mikita.external.enkanetwork.playerdata;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemType {

    ITEM_WEAPON("Weapon"),
    ITEM_RELIQUARY("Artifact");

    private final String description;

}
