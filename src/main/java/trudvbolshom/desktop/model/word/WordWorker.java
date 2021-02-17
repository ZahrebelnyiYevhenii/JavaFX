package trudvbolshom.desktop.model.word;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordWorker {

    public XWPFDocument workDocument;

    public Map<Integer, String> getPerson() {
        return person;
    }

    public Map<Integer, String> person = new HashMap<>();

    {
        person.put(0, "Vasia");
        person.put(1, "01.20.2020");
        person.put(2, "Бухгалтер");
        person.put(3, "уволен");
    }

    public static void main(String[] args) throws FileNotFoundException {
        WordWorker wordWorker = new WordWorker();
        wordWorker.createWordDocument("C:\\template.docx", "C:\\template\\FILE.docx", wordWorker.getPerson());
        wordWorker.getAllTemplateName();
    }

    public WordWorker() {

    }

    public void createWordDocument(String templateFile, String reportFile, Map<Integer, String> documentData) throws FileNotFoundException {
        initWordDocument(templateFile);
        fillWordDocument(documentData);

        createNewWordDocument(reportFile);
    }


    private void initWordDocument(String templateFile) throws FileNotFoundException {
        try {
            FileInputStream fileInputStream = new FileInputStream(templateFile);
            workDocument = new XWPFDocument(OPCPackage.open(fileInputStream));
        } catch (InvalidFormatException | IOException e) {
            throw new FileNotFoundException("$$$$$ word file is not found $$$$$" + e);
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
            if (run.getText(0) != null && run.getText(0).contains("$")) {
                int columnNumber = getColumnNumber(run.getText(0));
                run.setText(run.getText(0).replace("$" + columnNumber, documentData.get(columnNumber%4)), 0);
            }
        }
    }

    private int getColumnNumber(String columnText) {
        Pattern pattern = Pattern.compile(".*\\$(?<column>[0-9]?[0-9]).*");
        Matcher matcher = pattern.matcher(columnText);

        if (matcher.matches()) {
            return Integer.parseInt(matcher.group("column"));
        }

        throw new RuntimeException("$$$ column number is bad $$$");
    }

    private void replaceTable(XWPFTable iBodyElement, Map<Integer, String> documentData) {
        iBodyElement.getRows().forEach(row -> row.getTableCells()
                .forEach(xwpfTableCell -> xwpfTableCell.getParagraphs()
                        .forEach(paragraph -> replaceParagraph(paragraph, documentData))));
    }

    private void createNewWordDocument(String file) throws FileNotFoundException {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);

            workDocument.write(outputStream);

            outputStream.close();
        } catch (IOException e) {
            throw new FileNotFoundException("$$$$ create word file is exception $$$$" + e);
        }
    }

    public List<String> getAllTemplateName(){
        List<String> allTemplate = new ArrayList<>();
        File file = new File("src/main/resources/template");
        for (File template : file.listFiles()) {
            allTemplate.add(template.getName().replace(".docx", ""));
        }
        return allTemplate;
    }
}