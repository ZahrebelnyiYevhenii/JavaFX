package trudvbolshom.desktop.model.word;

import org.apache.poi.xwpf.usermodel.*;
import trudvbolshom.exception.WordWorkerException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static trudvbolshom.constants.ConstantsClass.SPECIAL_SYMBOL;

public class WordWorker {
    public XWPFDocument workDocument;
    public XWPFTableRow rowTable;
    public static final Pattern PATTERN = Pattern.compile(".*\\$(?<column>[0-9]?[0-9]).*");

    public void createWordDocument(String templateFile, String reportFile, Map<Integer, List<String>> listOfDocumentData) throws WordWorkerException {
        initWordDocument(templateFile);
        fillWordDocument(listOfDocumentData);
        createNewWordDocument(reportFile);
    }

    private void initWordDocument(String templateFile) throws WordWorkerException {
        try {
            workDocument = new XWPFDocument(new FileInputStream(templateFile));
        } catch (IOException e) {
            throw new WordWorkerException("$$$$$ word file - " + templateFile + " is not found $$$$$");
        }
    }

    private void fillWordDocument(Map<Integer, List<String>> listOfDocumentData) throws WordWorkerException {
        if (workDocument != null) {
            for (IBodyElement bodyElement : workDocument.getBodyElements()) {
                if (bodyElement instanceof XWPFParagraph) {
                    replaceParagraph((XWPFParagraph) bodyElement, 0, listOfDocumentData);
                } else if (bodyElement instanceof XWPFTable) {
                    rowTable = getRowTable((XWPFTable) bodyElement);

                    if (listOfDocumentData.size() > 1) {
                        addRowTable(listOfDocumentData);
                    }

                    replaceTable((XWPFTable) bodyElement, listOfDocumentData);
                }
            }
        }
    }

    //check
    private void replaceParagraph(XWPFParagraph paragraph, int rowIndex, Map<Integer, List<String>> listOfDocumentData) {
        if (paragraph.getRuns().size() != 0)
            for (XWPFRun run : paragraph.getRuns()) {
                String columnText = run.getText(0);

                if (isCorrectIndex(columnText)) {
                    int columnNumber = getColumnNumber(columnText);
                    if (listOfDocumentData.get(rowIndex).size() > columnNumber) {
                        run.setText(columnText.replace(SPECIAL_SYMBOL + columnNumber, listOfDocumentData.get(rowIndex).get(columnNumber)), 0);
                    }
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

    private XWPFTableRow getRowTable(XWPFTable iBodyElement) throws WordWorkerException {
        for (XWPFTableRow row : iBodyElement.getRows()) {
            if (isRowWithSpecialSymbol(row)) {
                return row;
            }
        }

        throw new WordWorkerException("$$$$ Row with special Symbol don't find $$$$");
    }

    private boolean isRowWithSpecialSymbol(XWPFTableRow row) {
        for (XWPFTableCell cell : row.getTableCells()) {
            for (XWPFParagraph paragraph : cell.getParagraphs()) {
                if (isParagraphHasSpecialSymbol(paragraph)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isParagraphHasSpecialSymbol(XWPFParagraph paragraph) {
        return paragraph.getRuns().size() != 0 && isCorrectIndex(paragraph.getRuns().get(0).getText(0));
    }

    private boolean isParagraphHasNumeration(XWPFParagraph paragraph) {
        return paragraph.getRuns().size() != 0 && paragraph.getRuns().get(0).getText(0).contains("1.");
    }

    private void addRowTable(Map<Integer, List<String>> listOfDocumentData) {
        for (IBodyElement bodyElement : workDocument.getBodyElements()) {
            if (bodyElement instanceof XWPFTable) {
                XWPFTable table = configTable((XWPFTable) bodyElement);
                XWPFTableRow rootTableRow = table.getRows().get(table.getRows().indexOf(rowTable));

                for (int counter = 0; counter < listOfDocumentData.size() - 1; counter++) {
                    createNewRowForPerson(table, rootTableRow);
                }
            }
        }
    }

    private XWPFTable configTable(XWPFTable bodyElement) {
        bodyElement.setCellMargins(25, 25, 25, 25);

        return bodyElement;
    }

    private void createNewRowForPerson(XWPFTable table, XWPFTableRow rootTableRow) {
        XWPFTableRow newTableRow = table.insertNewTableRow(table.getRows().indexOf(rowTable) + 1);

        for (int currentCellIndex = 0; currentCellIndex < table.getRows().get(table.getRows().indexOf(rowTable)).getTableICells().size(); currentCellIndex++) {
            configNewTableRow(newTableRow, currentCellIndex, rootTableRow);

            XWPFTableCell rootTableCell = rootTableRow.getCell(currentCellIndex);
            XWPFTableCell newTableCell = configNewTableCell(rootTableCell, newTableRow, currentCellIndex);

            XWPFParagraph newCellParagraph = newTableCell.addParagraph();
            newCellParagraph.setAlignment(ParagraphAlignment.CENTER);

            if (rootTableCell.getParagraphs().get(0).getRuns().size() != 0) {
                configParagraphRun(rootTableRow, currentCellIndex, newCellParagraph);
            }
        }
    }

    private void configNewTableRow(XWPFTableRow newTableRow, int currentCellIndex, XWPFTableRow rootTableRow) {
        if (newTableRow.getCell(currentCellIndex) == null) {
            newTableRow.addNewTableCell();
        }

        newTableRow.setHeight(rootTableRow.getHeight());
    }

    private XWPFTableCell configNewTableCell(XWPFTableCell rootTableCell, XWPFTableRow newTableRow, int currentCellIndex) {
        XWPFTableCell newTableCell = newTableRow.getCell(currentCellIndex);

        newTableCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        newTableCell.getCTTc().addNewTcPr().addNewTcBorders();
        newTableCell.getCTTc().getTcPr().setTcBorders(
                rootTableCell.getCTTc().getTcPr().getTcBorders());

        return newTableCell;
    }

    private XWPFRun configParagraphRun(XWPFTableRow rootTableRow, int currentCellIndex, XWPFParagraph newCellParagraph) {
        XWPFRun newParagraphRun = newCellParagraph.createRun();

        newParagraphRun.setBold(rootTableRow.getCell(currentCellIndex).getParagraphs().get(0).getRuns().get(0).isBold());
        newParagraphRun.setItalic(rootTableRow.getCell(currentCellIndex).getParagraphs().get(0).getRuns().get(0).isItalic());
        newParagraphRun.setFontFamily(rootTableRow.getCell(currentCellIndex).getParagraphs().get(0).getRuns().get(0).getFontFamily());
        newParagraphRun.setText(rootTableRow.getCell(currentCellIndex).getText());
        if (rootTableRow.getCell(currentCellIndex).getParagraphs().get(0).getRuns().get(0).getFontSizeAsDouble() != null)
            newParagraphRun.setFontSize(rootTableRow.getCell(currentCellIndex).getParagraphs().get(0).getRuns().get(0).getFontSizeAsDouble());

        return newParagraphRun;
    }

    private void replaceTable(XWPFTable iBodyElement, Map<Integer, List<String>> listOfDocumentData) {
        int rowIndex = 0;

        for (XWPFTableRow row : iBodyElement.getRows()) {
            if (isRowWithSpecialSymbol(row)) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        if (isParagraphHasSpecialSymbol(paragraph)) {
                            replaceParagraph(paragraph, rowIndex, listOfDocumentData);
                        } else if (isParagraphHasNumeration(paragraph) && rowIndex != 0) {
                            paragraph.getRuns().get(0).setText(paragraph.getRuns().get(0).getText(0).replace("1.", rowIndex + 1 + "."), 0);
                        }
                    }
                }
                rowIndex++;
            }
        }
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