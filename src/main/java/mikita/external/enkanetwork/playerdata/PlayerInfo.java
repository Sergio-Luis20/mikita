package mikita.external.enkanetwork.playerdata;

import java.util.List;

public record PlayerInfo(
        String nickname,
        byte level,
        String signature,
        byte worldLevel,
        int nameCardId,
        short finishAchievementNum,
        byte towerFloorIndex, // Abyss floor
        byte towerLevelIndex, // Abyss floor's chamber
        List<ShowAvatarInfo> showAvatarInfoList,
        List<Integer> showNameCardIdList,
        ProfilePicture profilePicture,
        byte theaterActIndex,
        byte theaterModeIndex,
        byte theaterStarIndex,
        byte fetterCount,
        byte towerStarIndex
) {
}
