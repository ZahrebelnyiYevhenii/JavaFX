package trudvbolshom.desktop.model.word;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import trudvbolshom.exception.WordWorkerException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static trudvbolshom.constants.ConstantsClass.SPECIAL_SYMBOL;

public class WordWorker {
    public XWPFDocument workDocument;
    public static final Pattern PATTERN = Pattern.compile(".*\\$(?<column>[0-9]?[0-9]).*");

    public void createWordDocument(String templateFile, String reportFile, Map<Integer, String> documentData) throws WordWorkerException {
        initWordDocument(templateFile);
        fillWordDocument(documentData);

        createNewWordDocument(reportFile);
    }

    private void initWordDocument(String templateFile) throws WordWorkerException {
        try {
            FileInputStream fileInputStream = new FileInputStream(templateFile);
            workDocument = new XWPFDocument(OPCPackage.open(fileInputStream));
        } catch (InvalidFormatException | IOException e) {
            throw new WordWorkerException("$$$$$ word file - " + templateFile + " is not found $$$$$");
        }
    }

    private void fillWordDocument(Map<Integer, String> documentData) {
        if (workDocument != null) {
            workDocument.getBodyElements().forEach(iBodyElement -> {
                if (iBodyElement instanceof XWPFParagraph) {
                    replaceParagraph((XWPFParagraph) iBodyElement, documentData);
                }
                if (iBodyElement instanceof XWPFTable) {
                    replaceTable((XWPFTable) iBodyElement, documentData);
                }
            });
        }
    }

    private void replaceParagraph(XWPFParagraph paragraph, Map<Integer, String> documentData) {
        for (XWPFRun run : paragraph.getRuns()) {
            String columnText = run.getText(0);

            if (isCorrectIndex(columnText)) {
                int columnNumber = getColumnNumber(columnText);

                if (documentData.containsKey(columnNumber))
                    run.setText(columnText.replace(SPECIAL_SYMBOL + columnNumber, documentData.get(columnNumber)), 0);
            }
        }
    }

    private boolean isCorrectIndex(String columnText) {
        return columnText != null && PATTERN.matcher(columnText).matches();
    }

    private int getColumnNumber(String columnText) {
        Matcher matcher = PATTERN.matcher(columnText);

        if (matcher.matches()) {
            return Integer.parseInt(matcher.group("column"));
        }

        return -1;
    }

    private void replaceTable(XWPFTable iBodyElement, Map<Integer, String> documentData) {
        iBodyElement.getRows().forEach(row -> row.getTableCells()
                .forEach(xwpfTableCell -> xwpfTableCell.getParagraphs()
                        .forEach(paragraph -> replaceParagraph(paragraph, documentData))));
    }

    private void createNewWordDocument(String reportFile) throws WordWorkerException {
        try {
            FileOutputStream outputStream = new FileOutputStream(reportFile);

            workDocument.write(outputStream);

            outputStream.close();
            workDocument.close();
        } catch (IOException e) {
            throw new WordWorkerException("$$$$ create word file " + reportFile + " is exception $$$$");
        }
    }
}