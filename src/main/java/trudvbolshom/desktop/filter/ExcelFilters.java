package trudvbolshom.desktop.filter;

public class ExcelFilters {
//    private ExcelWorker excelWorker;
//
//    public ExcelFilters(ExcelWorker excelWorker) {
//        this.excelWorker = excelWorker;
//    }
//
//    public Map<Integer, List<Cell>> getByEmployer(String name) {
//        List<Cell> employers = excelWorker.getColumn(0)
//                .stream()
//                .filter(cell -> cell.getStringCellValue().equals(name))
//                .collect(Collectors.toList());
//
//        List<Integer> columnIndexes = employers.stream().map(Cell::getRowIndex).collect(Collectors.toList());
//        Map<Integer, List<Cell>> findEmployers = new HashMap<>();
//
//        for(int index : columnIndexes){
//            findEmployers.put(index, excelWorker.getExcelDocumentData().getDataByRows().get(index + 1));
//        }
//
//        return findEmployers;
//    }
}
