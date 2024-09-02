package mikita.util;

import lombok.AllArgsConstructor;
import mikita.exception.ImplementationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class ImplementationsTest {

    @Mock
    private Implementations implementations;

    private AutoCloseable closeable;

    @BeforeEach
    public void buildMock() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void closeMock() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("Should succesfully instantiate a TestClass with correct arguments")
    public void test1() throws ImplementationException {
        Class<?>[] params = getParams();
        Args args = getArgs();
        Object[] array = args.args();
        Object any = args.any();

        TestClass testClassInstance = new TestClass(
                "test",
                42,
                'c',
                any,
                new double[] {1.0, 2.0},
                new Float[] {3.98f, -7.452f},
                new String[] {"a", "b"},
                new Object[] {"x", 25.2}
        );

        when(implementations.getCurrentImplementationInstance(eq(TestClass.class), any(), any())).thenReturn(testClassInstance);

        TestClass instance = implementations.getCurrentImplementationInstance(TestClass.class, params, array);

        assertNotNull(instance);
        assertEquals(array[0], instance.stringAttribute);
        assertEquals(array[1], instance.primitiveAttribute);
        assertEquals(array[2], instance.wrapperAttribute);
        assertEquals(array[3], instance.anyTypeAttribute);
        assertArrayEquals((double[]) array[4], instance.primitiveArrayAttribute);
        assertArrayEquals((Float[]) array[5], instance.wrapperArrayAttribute);
        assertArrayEquals((String[]) array[6], instance.stringArrayAttribute);
        assertArrayEquals((Object[]) array[7], instance.anyTypeArrayAttribute);
    }

    @Test
    @DisplayName("Should throw ImplementationException for primitive null parameter")
    public void test2() throws ImplementationException {
        when(implementations.getCurrentImplementationInstance(eq(TestClass.class), any(), any())).thenThrow(new ImplementationException("Primitive types don't accept null"));

        Class<?>[] params = getParams();
        Args args = getArgs();
        Object[] array = args.args();
        array[1] = null;

        assertThrows(ImplementationException.class, () -> implementations.getCurrentImplementationInstance(TestClass.class, params, array));
    }

    @Test
    @DisplayName("Should throw ImplementationException for incompatible values")
    public void test3() throws ImplementationException {
        when(implementations.getCurrentImplementationInstance(eq(TestClass.class), any(), any())).thenThrow(new ImplementationException("Params don't match args (types)"));

        Class<?>[] params = getParams();
        Args args = getArgs();
        Object[] array = args.args();
        array[0] = new DummyClass();

        assertThrows(ImplementationException.class, () -> implementations.getCurrentImplementationInstance(TestClass.class, params, array));
    }

    @Test
    @DisplayName("Should return null when implementation is not found and throw NullPointerException when null is passed as argument of getCurrentImplementation")
    public void test4() {
        when(implementations.getCurrentImplementation(null)).thenThrow(NullPointerException.class);
        assertThrows(NullPointerException.class, () -> implementations.getCurrentImplementation(null));

        when(implementations.getCurrentImplementation(DummyClass.class)).thenReturn(null);
        assertNull(implementations.getCurrentImplementation(DummyClass.class));
    }

    @AllArgsConstructor
    private static class TestClass {

        String stringAttribute;
        int primitiveAttribute;
        Character wrapperAttribute;
        Object anyTypeAttribute;
        double[] primitiveArrayAttribute;
        Float[] wrapperArrayAttribute;
        String[] stringArrayAttribute;
        Object[] anyTypeArrayAttribute;

    }

    private static class DummyClass {
    }

    private static Class<?>[] getParams() {
        Class<?>[] params = new Class<?>[8];

        params[0] = String.class;
        params[1] = int.class;
        params[2] = Character.class;
        params[3] = Object.class;
        params[4] = double[].class;
        params[5] = Float[].class;
        params[6] = String[].class;
        params[7] = Object[].class;

        return params;
    }

    private static Args getArgs() {
        Object any = new Object();

        Object[] args = new Object[8];

        args[0] = "test";
        args[1] = 42;
        args[2] = 'c';
        args[3] = any;
        args[4] = new double[] {1.0, 2.0};
        args[5] = new Float[] {3.98f, -7.452f};
        args[6] = new String[]{"a", "b"};
        args[7] = new Object[]{"x", 25.2};

        return new Args(args, any);
    }

    private record Args(Object[] args, Object any) {
    }

}
