package mikita.external.enkanetwork.playerdata;

import mikita.util.YamlMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class Definitions {

    public static final Map<Short, String> prop, fightProp;

    private Definitions() {
    }

    static {
        Map<Short, String> propMap = new HashMap<>();
        Map<Short, String> fightPropMap = new HashMap<>();

        try (InputStream stream = Definitions.class.getResourceAsStream("/enka/props.yml")) {
            YamlMapper mapper = new YamlMapper(stream);

            List<String> propList = mapper.get("prop");
            List<String> fightPropList = mapper.get("fight-prop");

            BiConsumer<List<String>, Map<Short, String>> consumer = (list, map) -> list.forEach(str -> {
                String[] keyValuePair = str.split(":");
                short key = Short.parseShort(keyValuePair[0]);
                map.put(key, keyValuePair[1]);
            });

            consumer.accept(propList, propMap);
            consumer.accept(fightPropList, fightPropMap);
        } catch (IOException e) {
            throw new RuntimeException("Could not load elements of Enka definitions", e);
        }

        prop = Collections.unmodifiableMap(propMap);
        fightProp = Collections.unmodifiableMap(fightPropMap);
    }

}
