package mikita.external.enkanetwork.playerdata;

import java.util.List;

public record PlayerData(PlayerInfo playerInfo, List<AvatarInfo> avatarInfoList, byte ttl, String uid) {
}
