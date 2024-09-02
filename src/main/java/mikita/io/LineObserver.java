package mikita.io;

@FunctionalInterface
public interface LineObserver {

    void newLine(String line);

    default void onClose() {
    }

}
