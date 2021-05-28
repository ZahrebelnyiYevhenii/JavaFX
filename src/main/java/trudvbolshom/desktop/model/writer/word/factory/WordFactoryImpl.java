package trudvbolshom.desktop.model.writer.word.factory;

import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import trudvbolshom.exception.InvalidWordTypeException;

import java.io.FileInputStream;
import java.io.IOException;

import static trudvbolshom.constants.ConstantsClass.DOCX;

public class WordFactoryImpl implements WordFactory {

    @Override
    public Document makeDocument(String templateWordFile) throws InvalidWordTypeException, IOException {

        if (templateWordFile.endsWith(DOCX)) {
            return new XWPFDocument(new FileInputStream(templateWordFile));
        } else {
            throw new InvalidWordTypeException("$$$$$ is not excel file - " + templateWordFile);
        }
    }
}
