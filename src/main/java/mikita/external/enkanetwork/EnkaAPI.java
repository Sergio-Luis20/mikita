package mikita.external.enkanetwork;

import mikita.exception.EnkaNetworkException;
import mikita.external.enkanetwork.playerdata.PlayerData;
import mikita.external.enkanetwork.playerdata.PlayerInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiFunction;

@Service
public class EnkaAPI {

    public final String API_PREFIX = "https://enka.network/api/";
    public final String UI_PREFIX = "https://enka.network/ui/";

    private RestTemplate restTemplate;

    public EnkaAPI(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PlayerInfo getPlayerInfo(String uid) throws EnkaNetworkException {
        return getObject(API_PREFIX + "uid/" + uid + "?info", PlayerInfoHolder.class,
                "Could not get player info from uid " + uid, this::getFullUIDExceptionMessage)
                .playerInfo();
    }

    public PlayerData getPlayerData(String uid) throws EnkaNetworkException {
        return getObject(API_PREFIX + "uid/" + uid, PlayerData.class,
                "Could not get player data from uid " + uid, this::getFullUIDExceptionMessage);
    }

    public BufferedImage getIcon(String characterName, boolean side) throws EnkaNetworkException {
        return getImage("UI_AvatarIcon_" + (side ? "Side_" : "") + characterName + ".png");
    }

    public BufferedImage getSplashArt(String characterName) throws EnkaNetworkException {
        return getImage("UI_Gacha_AvatarImg_" + characterName + ".webp");
    }

    public BufferedImage getImage(String iconFile) throws EnkaNetworkException {
        byte[] bytes = getObject(UI_PREFIX + iconFile, byte[].class,
                "Could not get icon data from icon id " + iconFile, this::getFullIconExceptionMessage);
        try (InputStream inputStream = new ByteArrayInputStream(bytes);
             BufferedInputStream buff = new BufferedInputStream(inputStream)) {
            BufferedImage original = ImageIO.read(buff);
            if (original.getType() == BufferedImage.TYPE_INT_ARGB) {
                return original;
            }
            BufferedImage argb = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = argb.createGraphics();
            graphics.drawImage(original, 0, 0, null);
            graphics.dispose();
            return argb;
        } catch (IOException e) {
            throw new EnkaNetworkException("Could not do parsing from response body to BufferedImage", e);
        }
    }

    private <T> T getObject(String url, Class<T> componentType, String exceptionMessage, BiFunction<String, HttpStatusCode, String> fullMessageFunction) throws EnkaNetworkException {
        try {
            ResponseEntity<T> response = restTemplate.getForEntity(url, componentType);
            HttpStatusCode code = response.getStatusCode();
            if (code.value() == 200) {
                return response.getBody();
            }
            throw new EnkaNetworkException(fullMessageFunction.apply(exceptionMessage, code));
        } catch (RestClientException e) {
            throw new EnkaNetworkException("RestClientException", e);
        }
    }

    private String getFullUIDExceptionMessage(String exceptionMessage, HttpStatusCode code) {
        return getExceptionMessage(exceptionMessage, code, "Wrong UID format", "Player does not exist (MHY server said that)");
    }

    private String getFullIconExceptionMessage(String exceptionMessage, HttpStatusCode code) {
        return getExceptionMessage(exceptionMessage, code, "Wrong icon id format", "Icon does not exist");
    }

    private String getExceptionMessage(String exceptionMessage, HttpStatusCode code, String badRequestMessge, String notFoundMessage) {
        int statusCode = code.value();
        String reasonPhrase = code instanceof HttpStatus status ? status.getReasonPhrase()
                : HttpStatus.valueOf(code.value()).getReasonPhrase();
        String exceptionDetails = switch (statusCode) {
            case 400 -> badRequestMessge;
            case 404 -> notFoundMessage;
            case 424 -> "Game maintenance / everything is broken after the game update";
            case 429 -> "Rate-limited (either by Enka server or by MHY server)";
            case 500 -> "General Enka server error";
            case 503 -> "Enka server screwed up massively";
            default -> reasonPhrase;
        };
        return exceptionMessage + ": " + exceptionDetails + "(" + statusCode + " - " + reasonPhrase + ")";
    }

    public record PlayerInfoHolder(PlayerInfo playerInfo) {
    }

}
