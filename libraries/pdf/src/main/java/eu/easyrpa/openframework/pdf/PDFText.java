package eu.easyrpa.openframework.pdf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PDFText {

    private PDFont font;
    private String text;
    private Color color;
    private int xCoordinate;
    private int yCoordinate;
    private float size;
    private float leading;

    public PDFText yCoordinate(int yCoordinate) {
        this.setYCoordinate(yCoordinate);
        return this;
    }

    public PDFText size(float size) {
        this.setSize(size);
        return this;
    }

    public PDFText color(Color color) {
        this.setColor(color);
        return this;
    }

    public PDFText text(String text) {
        this.setText(text);
        return this;
    }

    public PDFText xCoordinate(int xCoordinate) {
        this.setXCoordinate(xCoordinate);
        return this;
    }

    public PDFText font(PDFont font) {
        this.setFont(font);
        return this;
    }

    public PDFText leading(float leading) {
        this.setLeading(leading);
        return this;
    }

    public static PDFTextBuilder newBuilder(){
        return new PDFText().new PDFTextBuilder();
    }

    public class PDFTextBuilder {

        private PDFTextBuilder(){

        }

        public PDFTextBuilder text(String text) {
            PDFText.this.text = text;
            return this;
        }

        public PDFTextBuilder font(PDFont font){
            PDFText.this.font = font;
            return this;
        }

        public PDFTextBuilder size(float size) {
            PDFText.this.size = size;
            return this;
        }

        public PDFTextBuilder leading(float leading) {
            PDFText.this.leading = leading;
            return this;
        }

        public PDFTextBuilder color(Color color) {
            PDFText.this.color = color;
            return this;
        }

        public PDFTextBuilder xCoordinate(int xCoordinate) {
            PDFText.this.xCoordinate = xCoordinate;
            return this;
        }

        public PDFTextBuilder yCoordinate(int yCoordinate) {
            PDFText.this.yCoordinate = yCoordinate;
            return this;
        }

        public PDFText build(){
            return PDFText.this;
        }
    }


}
