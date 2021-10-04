package trudvbolshom.desktop.model.writer.word.updater;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import trudvbolshom.desktop.model.writer.word.validator.PatternValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;

import static trudvbolshom.constants.ConstantsClass.SPECIAL_SYMBOL;
import static trudvbolshom.constants.DateFormat.MEDIUM;
import static trudvbolshom.constants.SpecialData.DATE;
import static trudvbolshom.constants.SpecialData.PROTOCOL_NUMBER;

public class ParagraphUpdater extends Updater<XWPFParagraph> {
    public int countRowWithSymbol = 0;

    public ParagraphUpdater(Map<Integer, List<String>> listOfRowData) {
        super(listOfRowData);
    }

    @Override
    public void replace(XWPFParagraph paragraph) {
        if (paragraph.getRuns().size() != 0) {
            for (XWPFRun run : paragraph.getRuns()) {
                replaceRun(run);
            }
        }
    }

    private void replaceRun(XWPFRun run) {
        String columnText = run.getText(0);

        if (PatternValidator.isTextHasSpecialSymbol(columnText)) {
            replaceSymbol(run, columnText);
        } else if (isTableHasNumeration(columnText)) {
            increaseNumeration(run, columnText);
        }
    }

    private void replaceSymbol(XWPFRun run, String columnText) {
        replaceDate(run, columnText);
        replaceNumber(run, columnText);

        replaceSymbolToData(run, columnText);
    }

    private void replaceDate(XWPFRun run, String columnText) {
        int columnNumber = getColumnNumber(columnText);

        if (columnNumber == DATE.getSpecialNumber()) {
            String today = MEDIUM.format(LocalDateTime.now());

            run.setText(columnText.replace(SPECIAL_SYMBOL + columnNumber, today), 0);
        }
    }

    private void replaceNumber(XWPFRun run, String columnText) {
        int columnNumber = getColumnNumber(columnText);

        if (columnNumber == PROTOCOL_NUMBER.getSpecialNumber()) {
            LocalDateTime today = LocalDateTime.now();

            run.setText(columnText.replace(SPECIAL_SYMBOL + columnNumber, today.getYear() - 2000 + "" + today.getMonthValue() + " - " + new Random().nextInt(100000)), 0);
        }
    }

    private void replaceSymbolToData(XWPFRun run, String columnText) {
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

    private boolean isTableHasNumeration(String columnText) {
        return countRowWithSymbol != 0 && isColumnHasNumeration(columnText);
    }

    private boolean isColumnHasNumeration(String columnText) {
        return columnText.contains("1.");
    }

    private void increaseNumeration(XWPFRun run, String columnText) {
        run.setText(columnText.replace("1.", (countRowWithSymbol + 1) + "."), 0);
    }

    public int getCountRowWithSymbol() {
        return countRowWithSymbol;
    }

    public void setCountRowWithSymbol(int countRowWithSymbol) {
        this.countRowWithSymbol = countRowWithSymbol;
    }
}
