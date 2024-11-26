package mes.app;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PDFReader {

    public String extractTextFromPDF(File pdffile, int startPage, int endPage) throws IOException {
        try (PDDocument document = PDDocument.load(pdffile)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();

            // 시작 페이지와 끝 페이지 설정
            pdfStripper.setStartPage(startPage);
            pdfStripper.setEndPage(endPage);

            // 해당 페이지의 텍스트만 추출
            return pdfStripper.getText(document);
        }
    }
}
