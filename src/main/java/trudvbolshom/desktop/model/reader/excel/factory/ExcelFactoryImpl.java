package trudvbolshom.desktop.model.reader.excel.factory;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import trudvbolshom.exception.InvalidExcelTypeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static trudvbolshom.constants.ConstantsClass.XLS;
import static trudvbolshom.constants.ConstantsClass.XLSX;

public class ExcelFactoryImpl implements ExcelFactory {

    @Override
    public Workbook makeWorkBook(String pathExcel) throws InvalidExcelTypeException, InvalidFormatException, IOException {
        File file = new File(pathExcel);

        if (pathExcel.endsWith(XLSX)) {
            return new XSSFWorkbook(file);
        } else if (pathExcel.endsWith(XLS)) {
            return new HSSFWorkbook(new FileInputStream(file));
        } else {
            throw new InvalidExcelTypeException("$$$$$ is not excel file - " + pathExcel);
        }
    }
}
