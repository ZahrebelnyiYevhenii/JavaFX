package trudvbolshom.desktop.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Worker {
    private List<String> column = new ArrayList<>();
    private String сотрудник;

    public Worker(String сотрудник, String... column) {
        this.column.addAll(Arrays.asList(column));
        this.сотрудник = сотрудник;
    }

    public List<String> getColumn() {
        return column;
    }

    public void setColumn(List<String> column) {
        this.column = column;
    }

    public String getСотрудник() {
        return сотрудник;
    }

    public void setСотрудник(String сотрудник) {
        this.сотрудник = сотрудник;
    }
}
