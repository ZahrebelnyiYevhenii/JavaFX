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
import trudvbolshom.desktop.model.reader.excel.list.ExcelRowList;
import trudvbolshom.desktop.model.writer.word.WordWriter;
import trudvbolshom.desktop.starter.AppFX;
import trudvbolshom.desktop.starter.GitUpdater;
import trudvbolshom.exception.NotFoundTemplateFiles;

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
    private void updateProgram() throws IOException {
        GitUpdater gitUpdater = new GitUpdater();

        gitUpdater.updateNewVersion();

        showStatusLabel(PROGRAM_UPDATED);
    }

    @FXML
    private void uploadExcelFile() {
        load();
        initTable();
        fillTable();
        fillDisplayElement();
    }

    private void load() {
        loadExcelFile();
    }

    private void loadExcelFile() {
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
            TableColumn<ObservableList<String>, String> columnTitle = createColumnTitle(columnNumber, title);

            table.getColumns().add(columnTitle);
            columnNumber++;
        }
    }

    private TableColumn<ObservableList<String>, String> createColumnTitle(int columnNumber, String title) {
        TableColumn<ObservableList<String>, String> columnTitle = new TableColumn<>(title);

        columnTitle.setEditable(true);
        columnTitle.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(columnNumber)));
        columnTitle.setCellFactory(TextFieldTableCell.forTableColumn());
        // TODO changes cell text, in future
        //        column.setOnEditCommit(event -> System.out.println(event.getTablePosition().getTableColumn().getCellData(event.getTablePosition().getRow())));

        return columnTitle;
    }

    private void fillCell() {
        ExcelRowList excelRowList = new ExcelRowList(fileReader);

        excelRowList.fillRows();

        table.setItems(excelRowList.getRowList().getRows());
    }

    private void fillDisplayElement() {
        choiceTitle.setItems(FXCollections.observableArrayList(fileReader.getTitles()));
        choiceTitle.setValue(fileReader.getTitles().get(0));
        choiceTemplate.setItems(FXCollections.observableArrayList(getAllTemplatesName()));
        choiceTemplate.setValue(getAllTemplatesName().get(0));
    }

    public List<String> getAllTemplatesName() {
        try {
            return getTemplatesName();
        } catch (IOException e) {
            showStatusLabel(ERROR_WITH_FOLDER);
        }
        throw new NotFoundTemplateFiles(ERROR_WITH_FOLDER);
    }

    private List<String> getTemplatesName() throws IOException {
        List<String> allTemplates = new ArrayList<>();
        File file = new File(TEMPLATE_DIR);

        if (file.listFiles() != null) {
            allTemplates.addAll(createTemplatesName(file));
            createReportFolder(allTemplates);
        }

        return allTemplates;
    }

    private List<String> createTemplatesName(File file) {
        List<String> allTemplates = new ArrayList<>();

        for (File template : file.listFiles()) {
            allTemplates.add(template.getName().replace(WORD_DOCUMENT, ""));
        }

        return allTemplates;
    }

    private void createReportFolder(List<String> allTemplates) throws IOException {
        for (String fileName : allTemplates) {
            Files.createDirectories(Paths.get(REPORT_DIR + fileName));
        }
    }

    @FXML
    private void createWordTemplate() {
        createWord();
    }

    private void createWord() {
        String templateFilePath = TEMPLATE_DIR + choiceTemplate.getValue() + WORD_DOCUMENT;
        WordWriter wordWriter = new WordWriter(templateFilePath);

        Map<Integer, List<String>> listOfRowData = getListOfRowData();
        wordWriter.fillWordDocument(listOfRowData);

        String reportFilePath = REPORT_DIR + choiceTemplate.getValue() + "/" + listOfRowData.get(NUMBER_NAME_COLUMN).get(NUMBER_NAME_COLUMN) + WORD_DOCUMENT;
        wordWriter.createNewWordDocument(reportFilePath);
    }

    private Map<Integer, List<String>> getListOfRowData() {
        RowList selectedRow = new RowList(table.getSelectionModel().getSelectedItems());
        Map<Integer, List<String>> rowIndexToRowCells = new HashMap<>();

        int rowIndex = 0;
        for (ObservableList<String> listOfRowData : selectedRow.getRows()) {
            rowIndexToRowCells.put(rowIndex++, listOfRowData);
        }

        return rowIndexToRowCells;
    }

    @FXML
    private void close() {
        smallPanel.setVisible(false);
    }

    public void setAppFX(AppFX appFX) {
        this.appFX = appFX;
    }
}
