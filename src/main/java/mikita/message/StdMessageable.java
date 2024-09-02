package mikita.message;

import org.springframework.stereotype.Component;

@Component
public class StdMessageable implements Messageable {

    private static final StdMessageable INSTANCE = new StdMessageable();

    private StdMessageable() {
    }

    @Override
    public void sendMessage(String message) {
        System.out.println("[Std-Info] " + message);
    }

    @Override
    public void sendWarnMessage(String warnMessage) {
        System.out.println("[Std-Warn] " + warnMessage);
    }

    @Override
    public void sendErrorMessage(String errorMessage) {
        System.err.println("[Std-Error] " + errorMessage);
    }

    @Override
    public void sendErrorMessage(String errorMessage, Throwable throwable) {
        System.err.println("[Std-Error] " + errorMessage + " (" + throwable + ")");
    }

    public static StdMessageable getInstance() {
        return INSTANCE;
    }

}
