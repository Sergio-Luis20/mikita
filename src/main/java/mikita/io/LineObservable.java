package mikita.io;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LineObservable implements AutoCloseable {

    private final BufferedReader reader;
    private final ExecutorService executor;
    private final List<LineObserver> observers;

    @Getter
    private volatile boolean running, closed, closing;

    @Getter
    private final boolean block;

    public LineObservable(InputStream stream) {
        this(stream, true);
    }

    public LineObservable(InputStream stream, boolean block) {
        this.block = block;

        reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream, "stream")));
        executor = Executors.newSingleThreadExecutor(Thread.ofPlatform().daemon().factory());
        observers = Collections.synchronizedList(new ArrayList<>());
    }

    public synchronized void start() {
        if (running || closed) {
            return;
        }
        running = true;
        executor.execute(() -> {
            try {
                if (block) {
                    blockingRead();
                } else {
                    nonBlockingRead();
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Exception in " + getClass().getSimpleName() + ": " + e);
            }
        });
    }

    private void blockingRead() throws IOException {
        while (running) {
            if (!doRead()) {
                break;
            }
        }
    }

    private void nonBlockingRead() throws IOException, InterruptedException {
        while (running) {
            if (reader.ready()) {
                if (closing) {
                    break;
                }
                if (doRead()) {
                    continue;
                }
                break;
            }
            Thread.sleep(100);
        }
    }

    private boolean doRead() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return false;
        }
        observers.forEach(observer -> observer.newLine(line));
        return true;
    }

    public void addObserver(LineObserver observer) {
        observers.add(Objects.requireNonNull(observer, "observer"));
    }

    public boolean containsObserver(LineObserver observer) {
        return observers.contains(Objects.requireNonNull(observer, "observer"));
    }

    public void removeObserver(LineObserver observer) {
        observers.remove(Objects.requireNonNull(observer, "observer"));
    }

    @Override
    public synchronized void close() throws Exception {
        if (closed) {
            return;
        }
        running = false;
        try {
            executor.shutdown();
            reader.close();
            observers.forEach(LineObserver::onClose);
        } finally {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            closed = true;
        }
    }

    private void setClosing() {
        closing = true;
    }

}
