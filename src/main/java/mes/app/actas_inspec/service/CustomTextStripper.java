package mes.app.actas_inspec.service;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomTextStripper extends PDFTextStripper {

    private String searchText;
    private float yPosition = -1;
    private List<String> foundTexts = new ArrayList<>();  // 찾은 텍스트들을 저장하는 리스트

    public CustomTextStripper(String searchText) throws IOException {
        super();
        this.searchText = searchText;
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        for (TextPosition text : textPositions) {
            // 각 텍스트 조각을 리스트에 추가
            foundTexts.add(text.getUnicode());

            // 찾으려는 텍스트가 포함되는지 확인
            if (combineText().contains(searchText)) {
                yPosition = text.getYDirAdj();  // 텍스트의 Y 좌표 저장
                //System.out.println("찾은 Y 좌표: " + yPosition);  // 디버깅용 출력
                break;
            }
        }
        super.writeString(string, textPositions);
    }

    // 누적된 텍스트를 조합하여 하나의 문자열로 반환
    private String combineText() {
        StringBuilder combinedText = new StringBuilder();
        for (String s : foundTexts) {
            combinedText.append(s);
        }
        return combinedText.toString();
    }

    public float getYPosition() {
        return yPosition;
    }

}
