package mikita.external;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import mikita.exception.URLException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

@Getter
@RequiredArgsConstructor
public class RawFile {

    @NonNull
    private URL url;

    public byte[] getAsByteArray() throws URLException {
        try (BufferedInputStream buff = getInputStream()) {
            return buff.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAsString() throws URLException {
        return new String(getAsByteArray());
    }

    public BufferedInputStream getInputStream() throws URLException {
        try {
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            return new BufferedInputStream(connection.getInputStream());
        } catch (IOException e) {
            throw new URLException(e);
        }
    }

}
