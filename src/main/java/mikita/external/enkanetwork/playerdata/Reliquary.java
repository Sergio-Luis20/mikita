package mikita.external.enkanetwork.playerdata;

import java.util.List;

public record Reliquary(
        byte level,
        short mainPropId,
        List<Integer> appendPropIdList
) {
}
