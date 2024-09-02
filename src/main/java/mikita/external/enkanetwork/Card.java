package mikita.external.enkanetwork;

import lombok.Getter;
import mikita.exception.CorruptedDataException;
import mikita.exception.InvalidResourceException;
import mikita.external.enkanetwork.playerdata.PlayerData;
import mikita.miscellaneous.ImageSource;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
public class Card implements ImageSource<CorruptedDataException> {

    public static final int CARD_WIDTH = 1666;
    public static final int CARD_HEIGHT = 694;

    public static final Font GENSHIN_FONT;

    private PlayerData playerData;
    private int avatarIndex;

    public Card(PlayerData playerData, int avatarIndex) {
        this.playerData = Objects.requireNonNull(playerData, "playerData");
        int size = playerData.playerInfo().showAvatarInfoList().size();
        if (avatarIndex < 0 || avatarIndex >= size) {
            throw new IndexOutOfBoundsException("Index " + avatarIndex + " out of bounds for size " + size);
        }
        this.avatarIndex = avatarIndex;
    }

    @Override
    public BufferedImage getImage() throws CorruptedDataException {
        BufferedImage card = new BufferedImage(CARD_WIDTH, CARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = card.createGraphics();
        for (Component component : getComponents()) {
            graphics.drawImage(component.subImage(), component.x(), component.y(), null);
        }
        graphics.dispose();
        return card;
    }

    private List<Component> getComponents() throws CorruptedDataException {
        /*
         * Pegar todos os elementos do atributo avatar e montar as imagens
         * uma por uma para colocar na lista de componentes para serem montados
         * na tela principal.
         */
        List<Component> components = new LinkedList<>();

        int attributesWidth = 454;
        int relicsWidth = 501;
        int picWidth = 632;
        int picAttributesSpacing = 16;
        int attributesRelicsSpacing = 47;
        int relicsBorderSpacing = 16;
        int attributesTopBorderSpacing = 36;
        int attributesBottomBorderSpacing = 16;

        return components;
    }

    record Component(BufferedImage subImage, int x, int y) {
    }

    static {
        try (InputStream fontStream = Card.class.getResourceAsStream("/fonts/zh-cn.ttf")) {
            assert fontStream != null : "genshin font not found";
            GENSHIN_FONT = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (FontFormatException e) {
            throw new InvalidResourceException(e);
        }
    }

}
