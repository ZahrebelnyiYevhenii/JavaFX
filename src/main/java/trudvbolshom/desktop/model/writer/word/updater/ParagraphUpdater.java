package trudvbolshom.desktop.model.writer.word.updater;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import trudvbolshom.desktop.model.writer.word.validator.PatternValidator;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static trudvbolshom.constants.ConstantsClass.SPECIAL_SYMBOL;

public class ParagraphUpdater extends Updater<XWPFParagraph>{
    public int countRowWithSymbol = 0;

    public ParagraphUpdater(Map<Integer, List<String>> listOfRowData) {
        super(listOfRowData);
    }

    @Override
    public void replace(XWPFParagraph paragraph) {
        if (paragraph.getRuns().size() != 0) {
            for (XWPFRun run : paragraph.getRuns()) {
                replaceRun(run, countRowWithSymbol);
            }
        }
    }

    private void replaceRun(XWPFRun run, int countRowWithSymbol) {
        String columnText = run.getText(0);

        if (PatternValidator.isTextHasSpecialSymbol(columnText)) {
            replaceSymbolToData(run, countRowWithSymbol, columnText);
        } else if (isTableHasNumeration(run, countRowWithSymbol)) {
            increaseNumeration(run, countRowWithSymbol);
        }
    }


    private void replaceSymbolToData(XWPFRun run, int countRowWithSymbol, String columnText) {
        int columnNumber = getColumnNumber(columnText);

        if (listOfRowData.get(countRowWithSymbol).size() > columnNumber) {
            run.setText(columnText.replace(SPECIAL_SYMBOL + columnNumber, listOfRowData.get(countRowWithSymbol).get(columnNumber)), 0);
        }
    }

    private int getColumnNumber(String columnText) {
        Matcher matcher = PatternValidator.SPECIAL_SYMBOL.matcher(columnText);

        if (matcher.matches()) {
            return Integer.parseInt(matcher.group("column"));
        }

        return -1;
    }

    private boolean isTableHasNumeration(XWPFRun run, int countRowWithSymbol) {
        return countRowWithSymbol != 0 && isRunHasNumeration(run);
    }

    private boolean isRunHasNumeration(XWPFRun run) {
        return run.getText(0).contains("1.");
    }

    private void increaseNumeration(XWPFRun run, int countRowWithSymbol) {
        run.setText(run.getText(0).replace("1.", countRowWithSymbol + 1 + "."), 0);
    }

    public int getCountRowWithSymbol() {
        return countRowWithSymbol;
    }

    public void setCountRowWithSymbol(int countRowWithSymbol) {
        this.countRowWithSymbol = countRowWithSymbol;
    }
}
