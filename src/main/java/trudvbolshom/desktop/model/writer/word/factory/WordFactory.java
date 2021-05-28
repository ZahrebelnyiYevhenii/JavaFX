package trudvbolshom.desktop.model.writer.word.factory;

import org.apache.poi.xwpf.usermodel.Document;
import trudvbolshom.exception.InvalidWordTypeException;

import java.io.IOException;

public interface WordFactory {
    Document makeDocument(String templateWordFile) throws InvalidWordTypeException, IOException;
}
