package mikita.util;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public class YamlMapper {

    private Map<String, Object> root;

    public YamlMapper(InputStream inputStream) {
        Objects.requireNonNull(inputStream, "inputStream");
        Yaml yaml = new Yaml();
        root = yaml.load(inputStream);
    }

    private YamlMapper(Map<String, Object> root) {
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    public Set<String> getKeys(boolean deep) {
        Set<String> keys = new HashSet<>(root.size());
        if (!deep) {
            keys.addAll(root.keySet());
        } else {
            Deque<Entry<String, Object>> stack = new ArrayDeque<>(root.size());
            root.entrySet().forEach(stack::push);

            while (!stack.isEmpty()) {
                Entry<String, Object> entry = stack.pop();
                String keyPath = entry.getKey();
                Object value = entry.getValue();

                keys.add(keyPath);

                if (value instanceof Map<?, ?>) {
                    Map<String, Object> nestedMap = (Map<String, Object>) value;
                    for (Entry<String, Object> nestedEntry : nestedMap.entrySet()) {
                        stack.push(new SimpleEntry<>(keyPath + "." + nestedEntry.getKey(), nestedEntry.getValue()));
                    }
                }
            }
        }
        return keys;
    }

    public YamlMapper subMapper(String key) {
        Map<String, Object> sub = get(key);
        if (sub == null) {
            return null;
        }
        return new YamlMapper(sub);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        String[] keys = key.split("\\.");
        Object result = root;
        for (String k : keys) {
            if (result == null) {
                return null;
            }
            if (result instanceof Map<?,?> map) {
                result = map.get(k);
            } else {
                return null;
            }
        }
        return (T) result;
    }

    public static YamlMapper resource(String path) throws IOException {
        InputStream stream = YamlMapper.class.getResourceAsStream(path);
        if (stream != null) {
            try (stream) {
                return new YamlMapper(stream);
            }
        }
        return null;
    }

}
