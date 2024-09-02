package mikita.util;

import mikita.exception.ImplementationException;
import mikita.exception.InvalidResourceException;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.util.*;

@Component
public class Implementations {

    private Map<Class<?>, Selection> implementations;

    public Implementations() {
        try (FileInputStream fis = new FileInputStream("implementations.yml");
             BufferedInputStream buff = new BufferedInputStream(fis)) {
            YamlMapper mapper = new YamlMapper(buff);
            Set<String> keys = mapper.getKeys(false);
            implementations = new HashMap<>(keys.size());
            for (String key : keys) {
                List<String> classNames = mapper.get(key + ".classes");
                int index = mapper.get(key + ".current");
                int size = classNames.size();
                if (index < 0 || index >= size) {
                    throw new InvalidResourceException("Index " + index + " out of bounds for size " + size);
                }
                List<Class<?>> classes = new ArrayList<>(size);
                for (String className : classNames) {
                    classes.add(Class.forName(className));
                }
                implementations.put(Class.forName(key.replace('_', '.')), new Selection(classes, index));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read implementations file", e);
        } catch (ClassNotFoundException e) {
            throw new InvalidResourceException("Yaml key is not in java class format or class does not exist", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> getCurrentImplementation(Class<T> superr) {
        if (superr == null) {
            throw new NullPointerException("Super class or interface must not be null");
        }
        Selection selection = implementations.get(superr);
        if (selection == null) {
            return null;
        }
        return (Class<T>) selection.classes().get(selection.index());
    }

    public <T> T getCurrentImplementationInstance(Class<T> superr) throws ImplementationException {
        return getCurrentImplementationInstance(superr, new Class<?>[0], new Object[0]);
    }

    public <T> T getCurrentImplementationInstance(Class<T> superr, Class<?>[] constructorParams, Object[] constructorArgs) throws ImplementationException {
        try {
            if (constructorParams.length != constructorArgs.length) {
                throw new ImplementationException("Params don't match args (length)");
            }
            for (int i = 0; i < constructorArgs.length; i++) {
                Class<?> param = constructorParams[i];
                Object arg = constructorArgs[i];
                if (arg == null) {
                    if (param.isPrimitive()) {
                        throw new ImplementationException("Primitive types don't accept null");
                    } else {
                        continue;
                    }
                }
                if (!param.isPrimitive()) {
                    if (!param.isInstance(arg)) {
                        throwTypes();
                    }
                    continue;
                }
                switch (arg) {
                    case Byte ignored -> comparePrimitive(param, byte.class);
                    case Short ignored -> comparePrimitive(param, short.class);
                    case Integer ignored -> comparePrimitive(param, int.class);
                    case Long ignored -> comparePrimitive(param, long.class);
                    case Float ignored -> comparePrimitive(param, float.class);
                    case Double ignored -> comparePrimitive(param, double.class);
                    case Boolean ignored -> comparePrimitive(param, boolean.class);
                    case Character ignored -> comparePrimitive(param, char.class);
                    default -> throwTypes();
                }
            }
            Class<? extends T> current = getCurrentImplementation(superr);
            Constructor<? extends T> constructor = current.getConstructor(constructorParams);
            return constructor.newInstance(constructorArgs);
        } catch (Exception e) {
            throw new ImplementationException("Could not create instance of implementation of " + superr.getName(), e);
        }
    }

    private static void comparePrimitive(Class<?> clazz, Class<?> primitive) throws ImplementationException {
        if (clazz != primitive) {
            throwTypes();
        }
    }

    private static void throwTypes() throws ImplementationException {
        throw new ImplementationException("Params don't match args (types)");
    }

    public record Selection(List<Class<?>> classes, int index) {
    }

}
