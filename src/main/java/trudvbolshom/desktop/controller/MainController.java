package trudvbolshom.desktop.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import trudvbolshom.desktop.model.excel.ExcelWorker;
import trudvbolshom.desktop.model.word.WordWorker;
import trudvbolshom.desktop.starter.AppFX;
import trudvbolshom.exception.ExcelWorkerException;
import trudvbolshom.exception.WordWorkerException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static trudvbolshom.constants.ConstantsClass.*;

public class MainController {
    private AppFX appFX;
    private ExcelWorker excelWorker;
    private WordWorker wordWorker;

    @FXML
    private void loader() {
        loadExcelFile();
        fillTable();
        wordWorker = new WordWorker();
        fillDisplayElement();
    }

    private void loadExcelFile() {
        try {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(appFX.getPrimaryStage());

            excelWorker = new ExcelWorker(file.getPath());

            showLoadLabel(SUCCESS);
        } catch (ExcelWorkerException e) {
            showLoadLabel(e.getMessage());
        }
    }

    private void showLoadLabel(String text) {
        smallPanel.setVisible(true);
        fillLoadLabel(text);
    }

    private void fillLoadLabel(String text) {
        loadLabel.setText(text);
    }

    private void fillTable() {
        table.setEditable(true);
        table.getColumns().clear();
        fillColumnCell();
        fillTableColumns();
    }

    private void fillColumnCell() {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        for (int rowNumber = 0; rowNumber < excelWorker.getCountRows(); rowNumber++) {
            data.add(getExcelRowData(rowNumber));
        }

        table.setItems(data);
    }

    private ObservableList<String> getExcelRowData(int rowNumber) {
        ObservableList<String> row = FXCollections.observableArrayList();

        for (String title : excelWorker.getTitles()) {
            row.add(excelWorker.getTableData().get(title).get(rowNumber));
        }

        return row;
    }

    private void fillTableColumns() {
        int columnNumber = 0;

        for (String title : excelWorker.getTitles()) {
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(title);

            int finalColumnNumber = columnNumber;
            column.setEditable(true);
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(finalColumnNumber)));
            column.setCellFactory(TextFieldTableCell.forTableColumn());
            column.setOnEditCommit(event -> System.out.println(event.getTablePosition().getTableColumn().getCellData(event.getTablePosition().getRow())));

            table.getColumns().add(column);
            columnNumber++;
        }
    }

    @FXML
    private void createWordTemplate() {
        try {
            String templateFilePath = RESOURCE_FOLDER + TEMPLATE_DIR + choiceTemplate.getValue() + WORD_DOCUMENT;
            Map<Integer, String> rowData = getRowData();
            String reportFilePath = RESOURCE_FOLDER + REPORT_DIR + choiceTemplate.getValue() + "/" + rowData.get(NUMBER_NAME_COLUMN) + WORD_DOCUMENT;
            wordWorker.createWordDocument(templateFilePath, reportFilePath, rowData);
        } catch (WordWorkerException e) {
            showLoadLabel(e.getMessage());
        }
    }

    private Map<Integer, String> getRowData() {
        ObservableList<String> selectedRow = table.getSelectionModel().getSelectedItem();
        Map<Integer, String> rowData = new HashMap<>();

        for (String cellOnRow : selectedRow) {
            rowData.put(selectedRow.indexOf(cellOnRow), cellOnRow);
        }

        return rowData;
    }

    private void fillDisplayElement() {
        choiceTitle.setItems(FXCollections.observableArrayList(excelWorker.getTitles()));
        choiceTitle.setValue(excelWorker.getTitles().get(0));
        choiceTemplate.setItems(FXCollections.observableArrayList(getAllTemplateName()));
        choiceTemplate.setValue(getAllTemplateName().get(0));
    }

    public List<String> getAllTemplateName() {
        List<String> allTemplate = new ArrayList<>();

        try {
            File file = new File("src/main/resources/template");

            for (File template : file.listFiles()) {
                allTemplate.add(template.getName().replace(".docx", ""));
            }

            for (String fileName : allTemplate) {
                Files.createDirectories(Paths.get(RESOURCE_FOLDER + REPORT_DIR + fileName));
            }
        } catch (IOException e) {
           showLoadLabel(ERROR);
        }

        return allTemplate;
    }

    @FXML
    private void close() {
        smallPanel.setVisible(false);
    }

    public void setAppFX(AppFX appFX) {
        this.appFX = appFX;
    }

    public ExcelWorker getExcelWorker() {
        return excelWorker;
    }

    public void setExcelWorker(ExcelWorker excelWorker) {
        this.excelWorker = excelWorker;
    }

    @FXML
    private AnchorPane smallPanel;
    @FXML
    private Label loadLabel;
    @FXML
    private TableView<ObservableList<String>> table;
    @FXML
    private ChoiceBox<String> choiceTitle;
    @FXML
    private ChoiceBox<String> choiceTemplate;
}
