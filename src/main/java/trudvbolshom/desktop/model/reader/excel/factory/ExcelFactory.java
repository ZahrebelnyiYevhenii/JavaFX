package trudvbolshom.desktop.model.reader.excel.factory;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import trudvbolshom.exception.InvalidExcelTypeException;

import java.io.IOException;

public interface ExcelFactory {
    Workbook makeWorkBook(String pathExcel) throws InvalidExcelTypeException, InvalidFormatException, IOException;
}
