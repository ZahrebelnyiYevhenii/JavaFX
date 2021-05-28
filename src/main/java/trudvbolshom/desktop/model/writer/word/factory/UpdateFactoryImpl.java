package trudvbolshom.desktop.model.writer.word.factory;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import trudvbolshom.desktop.model.writer.word.updater.ParagraphUpdater;
import trudvbolshom.desktop.model.writer.word.updater.TableUpdater;
import trudvbolshom.desktop.model.writer.word.updater.Updater;

import java.util.List;
import java.util.Map;

public class UpdateFactoryImpl implements UpdateFactory {

    @Override
    public Updater makeUpdater(IBodyElement bodyElement, Map<Integer, List<String>> listOfRowData) {
        if (bodyElement instanceof XWPFTable) {
            return new TableUpdater(listOfRowData);
        } else if (bodyElement instanceof XWPFParagraph) {
            return new ParagraphUpdater(listOfRowData);
        } else {
            //TODO EXCEPTION
            return null;
        }
    }
}
