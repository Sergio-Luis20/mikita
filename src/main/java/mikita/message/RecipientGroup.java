package mikita.message;

import java.util.*;

public class RecipientGroup implements Messageable {

    private final List<Messageable> group;

    public RecipientGroup() {
        group = Collections.synchronizedList(new ArrayList<>());
    }

    public RecipientGroup(Messageable... recipients) {
        this();
        addAll(recipients);
    }

    public void add(Messageable recipient) {
        group.add(Objects.requireNonNull(recipient, "recipient"));
    }

    public void addAll(Messageable... recipients) {
        group.addAll(Arrays.stream(recipients).filter(Objects::nonNull).toList());
    }

    public boolean contains(Messageable recipient) {
        return group.contains(Objects.requireNonNull(recipient, "recipient"));
    }

    public void remove(Messageable recipient) {
        group.remove(Objects.requireNonNull(recipient, "recipient"));
    }

    public void clear() {
        group.clear();
    }

    public int size() {
        return group.size();
    }

    public Messageable get(int index) {
        return group.get(index);
    }

    public List<Messageable> getCopy() {
        return new ArrayList<>(group);
    }

    @Override
    public void sendMessage(String message) {
        group.forEach(recipient -> recipient.sendMessage(message));
    }

    @Override
    public void sendWarnMessage(String warnMessage) {
        group.forEach(recipient -> recipient.sendWarnMessage(warnMessage));
    }

    @Override
    public void sendErrorMessage(String errorMessage) {
        group.forEach(recipient -> recipient.sendErrorMessage(errorMessage));
    }

    @Override
    public void sendErrorMessage(String errorMessage, Throwable throwable) {
        group.forEach(recipient -> recipient.sendErrorMessage(errorMessage, throwable));
    }

}
