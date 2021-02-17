//package trudvbolshom.desktop.starter;
//
//import org.apache.poi.ss.usermodel.Cell;
//import trudvbolshom.desktop.filter.ExcelFilters;
//import trudvbolshom.desktop.model.ExcelWorker;
//
//import java.util.List;
//import java.util.Map;
//
//public class TrudVBolshomGorode {
//
//
//    public static void main(String[] args) {
//        ExcelWorker excelWorker = new ExcelWorker("C:\\Users\\жека\\Downloads\\Список АО Шмидт.xlsx");
//        excelWorker.initData();
////        excelWorker.showExcelData();
////        excelWorker.getTitles().forEach(System.out::println);
////        excelWorker.getColumnData(0).forEach(System.out::println);
//        ExcelFilters excelFilters = new ExcelFilters(excelWorker);
//        Map<Integer, List<Cell>> employer = excelFilters.getByEmployer("Абрамычев Михаил Александрович");
//        System.out.println(employer.toString());
//    }
//}