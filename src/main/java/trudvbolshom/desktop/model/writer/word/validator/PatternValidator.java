package trudvbolshom.desktop.model.writer.word.validator;

import java.util.regex.Pattern;

public class PatternValidator {
    public static final Pattern SPECIAL_SYMBOL = Pattern.compile(".*\\$(?<column>[0-9]?[0-9]).*");

    public static boolean isTextHasSpecialSymbol(String columnText) {
        return columnText != null && SPECIAL_SYMBOL.matcher(columnText).matches();
    }
}
