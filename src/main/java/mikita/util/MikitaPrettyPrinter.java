package mikita.util;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class MikitaPrettyPrinter extends DefaultPrettyPrinter {

    public MikitaPrettyPrinter() {
        indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        _objectFieldValueSeparatorWithSpaces = ": ";
    }

    @Override
    public MikitaPrettyPrinter createInstance() {
        return new MikitaPrettyPrinter();
    }
}
