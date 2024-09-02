package mikita.message;

import mikita.BatchList;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Printer {

    private static final int PAGE_SIZE = 5;

    private Printer() {}

    public static <T> void listPageable(Messageable messageable, int page, Collection<T> elements,
                                        String elementName, Function<T, String> toMessage) {
        listPageable(messageable, page, elements.stream(), elementName, toMessage);
    }

    public static <T> void listPageable(Messageable messageable, int page, Stream<T> elements,
                                        String elementName, Function<T, String> toMessage) {
        int index = page - 1;
        if(index < 0) {
            messageable.sendErrorMessage("A página deve ser maior que 0");
            return;
        }
        BatchList<T> batchList = elements.collect(BatchList.collector(PAGE_SIZE));
        List<T> batch;
        try {
            batch = batchList.getBatch(index);
        } catch(IndexOutOfBoundsException e) {
            messageable.sendErrorMessage("Página maior que a quantidade máxima existente");
            return;
        }
        messageable.sendMessage("Lista de " + elementName + " (p. " + page + "/" + batchList.getTotalBatches() + "):");
        for(T element : batch) {
            messageable.sendMessage("- " + toMessage.apply(element));
        }
    }

}
