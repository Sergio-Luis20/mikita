package mikita.external.enkanetwork.playerdata;

import java.util.Map;

public record Weapon(byte level, byte promoteLevel, Map<Integer, Byte> affixMap) {
}
