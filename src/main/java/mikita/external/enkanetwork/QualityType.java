package mikita.external.enkanetwork;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QualityType {

    QUALITY_GRAY((byte) 1),
    QUALITY_GREEN((byte) 2),
    QUALITY_BLUE((byte) 3),
    QUALITY_PURPLE((byte) 4),
    QUALITY_ORANGE((byte) 5),
    QUALITY_ORANGE_SP((byte) 5);

    private final byte stars;

}
