package trudvbolshom.desktop.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import trudvbolshom.desktop.model.list.RowList;
import trudvbolshom.desktop.model.reader.FileReader;
import trudvbolshom.desktop.model.reader.excel.ExcelReader;
import trudvbolshom.desktop.model.writer.word.WordWriter;
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

    private AppFX appFX;
    private FileReader fileReader;

    @FXML
    private void uploadExcelFile() {
        load();
        initTable();
        fillTable();
        fillDisplayElement();
    }

    private void load() {
        try {
            loadExcelFile();
        } catch (ExcelWorkerException e) {
            showStatusLabel(e.getMessage());
        }
    }

    private void loadExcelFile() throws ExcelWorkerException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(appFX.getPrimaryStage());

        fileReader = new ExcelReader(file.getPath());

        showStatusLabel(SUCCESS);
    }

    private void showStatusLabel(String text) {
        smallPanel.setVisible(true);
        loadLabel.setText(text);
    }

    private void initTable() {
        table.setEditable(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getColumns().clear();
    }

    private void fillTable() {
        createColumnsWithTitle();
        fillCell();
    }

    private void createColumnsWithTitle() {
        int columnNumber = 0;

        for (String title : fileReader.getTitles()) {
            TableColumn<ObservableList<String>, String> column = createColumn(columnNumber, title);

            table.getColumns().add(column);
            columnNumber++;
        }
    }

    private TableColumn<ObservableList<String>, String> createColumn(int columnNumber, String title) {
        TableColumn<ObservableList<String>, String> column = new TableColumn<>(title);

        column.setEditable(true);
        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(columnNumber)));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        // TODO changes cell text, in future
        //        column.setOnEditCommit(event -> System.out.println(event.getTablePosition().getTableColumn().getCellData(event.getTablePosition().getRow())));

        return column;
    }

    private void fillCell() {
        RowList rowList = new RowList(fileReader);

        rowList.fillRows();

        table.setItems(rowList.getRows());
    }


    @FXML
    private void createWordTemplate() {
        try {
            String templateFilePath = RESOURCE_FOLDER + TEMPLATE_DIR + choiceTemplate.getValue() + WORD_DOCUMENT;
            Map<Integer, List<String>> listOfRowData = getListOfRowData();
            WordWriter wordWriter = new WordWriter(templateFilePath);

            wordWriter.fillWordDocument(listOfRowData);

            String reportFilePath = RESOURCE_FOLDER + REPORT_DIR + choiceTemplate.getValue() + "/" + listOfRowData.get(NUMBER_NAME_COLUMN).get(NUMBER_NAME_COLUMN) + WORD_DOCUMENT;
            wordWriter.createNewWordDocument(reportFilePath);
        } catch (WordWorkerException e) {
            showStatusLabel(e.getMessage());
        }
    }

    private Map<Integer, List<String>> getListOfRowData() {
        ObservableList<ObservableList<String>> selectedRow = table.getSelectionModel().getSelectedItems();
        Map<Integer, List<String>> listRowData = new HashMap<>();

        for (ObservableList<String> listOfRowData : selectedRow) {
            listRowData.put(selectedRow.indexOf(listOfRowData), listOfRowData);
        }

        return listRowData;
    }

    private void fillDisplayElement() {
        choiceTitle.setItems(FXCollections.observableArrayList(fileReader.getTitles()));
        choiceTitle.setValue(fileReader.getTitles().get(0));
        choiceTemplate.setItems(FXCollections.observableArrayList(getAllTemplateName()));
        choiceTemplate.setValue(getAllTemplateName().get(0));
    }

    public List<String> getAllTemplateName() {
        List<String> allTemplate = new ArrayList<>();

        try {
            File file = new File(RESOURCE_FOLDER + TEMPLATE_DIR);

            for (File template : file.listFiles()) {
                allTemplate.add(template.getName().replace(WORD_DOCUMENT, ""));
            }

            for (String fileName : allTemplate) {
                Files.createDirectories(Paths.get(RESOURCE_FOLDER + REPORT_DIR + fileName));
            }
        } catch (IOException e) {
            showStatusLabel(ERROR);
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
}
