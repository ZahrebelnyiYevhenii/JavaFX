package trudvbolshom.desktop.model.writer.word.updater;

import org.apache.poi.xwpf.usermodel.*;
import trudvbolshom.desktop.model.writer.word.validator.PatternValidator;
import trudvbolshom.exception.WordWorkerException;

import java.util.List;
import java.util.Map;

public class TableUpdater extends Updater<XWPFTable> {
    private final ParagraphUpdater paragraphUpdater;

    public TableUpdater(Map<Integer, List<String>> listOfRowData) {
        super(listOfRowData);
        paragraphUpdater = new ParagraphUpdater(listOfRowData);
    }

    @Override
    public void replace(XWPFTable table) {
        if (isRowsMoreThanOne()) {
            addMoreRowToTable(table);
        } else {
            replaceRows(table);
        }
    }

    private boolean isRowsMoreThanOne() {
        return listOfRowData.size() > 1;
    }

    private void addMoreRowToTable(XWPFTable table) {
        try {
            createNewRow(table);
        } catch (WordWorkerException e) {
            //TODO EXCEPTION
            e.printStackTrace();
        }
    }

    private void createNewRow(XWPFTable table) throws WordWorkerException {
        configTable(table);
        XWPFTableRow rootTableRow = getFirstRowTable(table);

        for (int counter = 0; counter < listOfRowData.size() - 1; counter++) {
            createNewRowForPerson(table, rootTableRow);
        }
    }

    private void configTable(XWPFTable table) {
        table.setCellMargins(25, 25, 25, 25);
    }

    private XWPFTableRow getFirstRowTable(XWPFTable table) throws WordWorkerException {
        for (XWPFTableRow row : table.getRows()) {
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
        return paragraph.getRuns().size() != 0 &&
                PatternValidator.isTextHasSpecialSymbol(paragraph.getRuns().get(0).getText(0));
    }

    private void createNewRowForPerson(XWPFTable table, XWPFTableRow rootTableRow) {
        XWPFTableRow newTableRow = table.insertNewTableRow(table.getRows().indexOf(rootTableRow) + 1);

        for (int currentCellIndex = 0; currentCellIndex < table.getRows().get(table.getRows().indexOf(rootTableRow)).getTableICells().size(); currentCellIndex++) {
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

    private void replaceRows(XWPFTable table) {
        int countRowWithSymbol = 0;

        for (XWPFTableRow rowWithSymbol : table.getRows()) {
            if (isRowWithSpecialSymbol(rowWithSymbol)) {
                replaceRow(rowWithSymbol, countRowWithSymbol);
                countRowWithSymbol++;
            }
        }
    }

    private void replaceRow(XWPFTableRow rowWithSymbol, int countRowWithSymbol) {
        for (XWPFTableCell cell : rowWithSymbol.getTableCells()) {
            replaceCell(cell, countRowWithSymbol);
        }
    }

    //refact later
    private void replaceCell(XWPFTableCell cell, int countRowWithSymbol) {
        for (XWPFParagraph paragraph : cell.getParagraphs()) {
            if (isParagraphHasSpecialSymbol(paragraph)) {
                paragraphUpdater.setCountRowWithSymbol(countRowWithSymbol);
                paragraphUpdater.replace(paragraph);
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




}
