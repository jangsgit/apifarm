package mes.app.actas_inspec.service;

import mes.app.actas_inspec.service.CustomTextStripper;
import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFTableExtractor {
    private PDDocument document;
    private List<List<Table>> allTables = new ArrayList<>();

    public PDFTableExtractor(File pdfFile) throws IOException {
        this.document = PDDocument.load(pdfFile);
        try {
            extractAllTables();
        } catch (IOException e) {
            // 예외 처리: 리소스 해제 필요
            close();
            throw e;
        }
    }

    // 모든 테이블을 추출하는 메서드
    private void extractAllTables() throws IOException {
        ObjectExtractor extractor = new ObjectExtractor(document);
        SpreadsheetExtractionAlgorithm algorithm = new SpreadsheetExtractionAlgorithm();

        for (int i = 1; i <= document.getNumberOfPages(); i++) {
            Page page = extractor.extract(i);
            List<Table> tables = algorithm.extract(page);
            allTables.add(tables);
        }
        extractor.close(); // 리소스 정리
    }

    // 특정 텍스트 이후 첫 번째 테이블을 찾는 메서드
    public List<List<String>> findTableAfterText(String searchText) throws IOException {
        // 텍스트 좌표 찾기
        CustomTextStripper stripper = new CustomTextStripper(searchText);
        stripper.setSortByPosition(true);

        // PDF 문서에서 텍스트 추출
        stripper.getText(document);

        // 텍스트의 Y 좌표를 얻음
        float yPosition = stripper.getYPosition();
        if (yPosition == -1) {
            // 텍스트를 찾지 못했을 경우 빈 리스트 반환
            return new ArrayList<>();
        }

        // 특정 텍스트 이후의 테이블을 찾음
        for (int pageIndex = 0; pageIndex < allTables.size(); pageIndex++) {
            List<Table> tablesOnPage = allTables.get(pageIndex);
            for (Table table : tablesOnPage) {
                // 빈 테이블을 검사
                if (table.getRows().isEmpty() || table.getRows().get(0).isEmpty()) {
                    continue; // 빈 테이블이면 스킵
                }

                // Y 좌표 비교하여 첫 번째 테이블 찾기
                if (table.getRows().get(0).get(0).getY() > yPosition) {
                    return convertTableToList(table);
                }
            }
        }

        // 테이블을 찾지 못했을 경우 빈 리스트 반환
        return new ArrayList<>();
    }

    // 테이블을 리스트로 변환하는 메서드
    private List<List<String>> convertTableToList(Table table) {
        List<List<String>> tableData = new ArrayList<>();
        for (List<RectangularTextContainer> row : table.getRows()) {
            List<String> rowData = new ArrayList<>();
            for (RectangularTextContainer cell : row) {
                rowData.add(cell.getText());
            }
            tableData.add(rowData);
        }
        return tableData;
    }

    // 리소스를 정리하는 메서드
    public void close() throws IOException {
        if (document != null) {
            document.close();
        }
    }
}
