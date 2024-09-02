package mikita.miscellaneous;

import lombok.Getter;
import mikita.exception.InvalidResourceException;
import mikita.exception.ResourceNotFoundException;
import mikita.exception.TextTooLongException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
public class MessageOfTheDay implements ImageSource<TextTooLongException> {

    public static final String FRAME_RESOURCE_PATH = "/images/frame.png";
    public static final String FONT_RESOURCE_PATH = "/fonts/Relationship of mélodrame.ttf";
    public static final float FONT_SIZE = 48f;

    private String message;
    private Color color;
    private float fontSize;

    public MessageOfTheDay(String message) {
        setMessage(message);
        setColor(Color.BLACK);
        setFontSize(FONT_SIZE);
    }

    @Override
    public BufferedImage getImage() throws TextTooLongException {
        BufferedImage image;
        Font font;
        try (InputStream imageStream = MessageOfTheDay.class.getResourceAsStream(FRAME_RESOURCE_PATH);
             InputStream fontStream = MessageOfTheDay.class.getResourceAsStream(FONT_RESOURCE_PATH)) {
            if (imageStream == null) {
                throw new ResourceNotFoundException("Could not find " + FRAME_RESOURCE_PATH);
            }
            if (fontStream == null) {
                throw new ResourceNotFoundException("Couldl not find" + FONT_RESOURCE_PATH);
            }
            image = ImageIO.read(imageStream);
            font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.BOLD, fontSize);
        } catch (IOException e) {
            throw new ResourceNotFoundException("Could not read " + FRAME_RESOURCE_PATH, e);
        } catch (FontFormatException e) {
            throw new InvalidResourceException("Could not parse " + FONT_RESOURCE_PATH, e);
        }

        int width = image.getWidth();
        int height = image.getHeight();

        int colorRgb = color.getRGB();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = image.getRGB(i, j);
                if (rgb != 0) {
                    image.setRGB(i, j, colorRgb);
                }
            }
        }

        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);

        int textMargin = 100;
        int breakWidth = width - textMargin - 2 * 40; // 40 to ensure not touching border (jit will optimize this line)
        int maxTextHeight = height - 2 * (90 + 10); // 90 from minimum border + 10 to ensure not touching border (jit will optimize this line)

        AttributedString attributedString = new AttributedString(message);
        attributedString.addAttribute(TextAttribute.FONT, font);
        AttributedCharacterIterator iterator = attributedString.getIterator();

        int start = iterator.getBeginIndex();
        int end = iterator.getEndIndex();

        FontRenderContext context = g2d.getFontRenderContext();
        LineBreakMeasurer measurer = new LineBreakMeasurer(iterator, context);

        record LinePosition(TextLayout layout, float width, float height) {
        }

        List<LinePosition> lines = new LinkedList<>();

        measurer.setPosition(start);

        float totalHeight = 0;
        float lastHeight = 0;

        while (measurer.getPosition() < end) {
            TextLayout layout = measurer.nextLayout(breakWidth);
            lastHeight = layout.getAscent() + layout.getDescent() + layout.getLeading();
            lines.add(new LinePosition(layout, layout.getAdvance(), lastHeight));
            totalHeight += lastHeight;
        }

        if (totalHeight > maxTextHeight) {
            TextTooLongException ex = new TextTooLongException("MOTD too long");
            ex.setText(message);
            throw ex;
        }

        totalHeight -= lastHeight;

        float drawY = (height - totalHeight) / 2;

        for (LinePosition line : lines) {
            float drawX = (width - line.width()) / 2;
            line.layout().draw(g2d, drawX, drawY);
            drawY += line.height();
        }

        g2d.dispose();

        return image;
    }

    public void setMessage(String message) {
        this.message = Objects.requireNonNull(message, "message");
    }

    public void setColor(Color color) {
        this.color = Objects.requireNonNull(color, "color");
    }

    public void setFontSize(float fontSize) {
        if (fontSize <= 0) {
            throw new IllegalArgumentException("fontSize must be positive");
        }
        this.fontSize = fontSize;
    }

}
