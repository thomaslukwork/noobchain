package com.thomaslukwork.stamping;

import com.itextpdf.text.Annotation;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class PdfStamp {

  public static void main(String[] args) throws IOException, DocumentException {

    PdfStamp pdfStamp = new PdfStamp();
//    pdfStamp.getFileStream();

//    pdfStamp.imageToPDF();

//    pdfStamp.docToPDF();

//    pdfStamp.convertFileToGrayScale();

//    pdfStamp.convertImageToGrayScale();
  }

  private void convertImageToGrayScale() {
//    String inputPath = "driving-licence.jpeg"; // .gif and .jpg are ok too!
//    String outputPath = "driving-licence-gray.jpeg";

//    String inputPath = "Sample-png-image.png";
//    String outputPath = "Sample-png-image-gray.png";

    String inputPath = "colour.pdf";
    String outputPath = "test-gray.pdf";

    try {
      File input = new File(inputPath);
      BufferedImage image = ImageIO.read(input);

      // Check if image
      String mimetype = Files.probeContentType(input.toPath());
      String type = mimetype.split("/")[0];
      String extension = mimetype.split("/")[1];
      if (type.equals("image")) {
        System.out.println("It's an image, extension is " + extension);

        BufferedImage result = new BufferedImage(
            image.getWidth(),
            image.getHeight(),
            BufferedImage.TYPE_INT_RGB);

        Graphics2D graphic = result.createGraphics();
        graphic.drawImage(image, 0, 0, Color.WHITE, null);

        for (int i = 0; i < result.getHeight(); i++) {
          for (int j = 0; j < result.getWidth(); j++) {
            Color c = new Color(result.getRGB(j, i));
            int red = (int) (c.getRed() * 0.299);
            int green = (int) (c.getGreen() * 0.587);
            int blue = (int) (c.getBlue() * 0.114);
            Color newColor = new Color(
                red + green + blue,
                red + green + blue,
                red + green + blue);
            result.setRGB(j, i, newColor.getRGB());
          }
        }
        File output = new File(outputPath);
        ImageIO.write(result, extension, output);

      } else {
        System.out.println("It's NOT an image");
      }


    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void convertFileToGrayScale() throws IOException, DocumentException {

    PdfReader pdfReader = new PdfReader("colour.pdf");
    PdfGState gstate = new PdfGState();
    gstate.setBlendMode(PdfName.SATURATION);

    PdfStamper pdfStamper = new PdfStamper(pdfReader,
        new FileOutputStream("gray.pdf"));

    for (int pageName = 1; pdfReader.getNumberOfPages() >= pageName; pageName++) {

      PdfContentByte canvas = pdfStamper.getOverContent(pageName);
      canvas.setGState(gstate);
      Rectangle mediaBox = pdfReader.getPageSize(pageName);
      canvas.setColorFill(BaseColor.BLACK);
      canvas.rectangle(mediaBox.getLeft(), mediaBox.getBottom(), mediaBox.getWidth(),
          mediaBox.getHeight());
      canvas.fill();

      canvas = pdfStamper.getUnderContent(pageName);
      canvas.setColorFill(BaseColor.WHITE);
      canvas.rectangle(mediaBox.getLeft(), mediaBox.getBottom(), mediaBox.getWidth(),
          mediaBox.getHeight());
      canvas.fill();
    }
    pdfStamper.close();
    pdfReader.close();
  }


  private void getFileStream() throws IOException, DocumentException {

    PdfReader pdfReader = new PdfReader("sample.pdf");
    PdfStamper pdfStamper = new PdfStamper(pdfReader,
        new FileOutputStream("sampleStamp.pdf"));

    PdfContentByte canvas = pdfStamper.getOverContent(1);

    Image image = Image.getInstance("stamp.jpeg");
    image.scaleAbsolute(50, 20);
    image.setAbsolutePosition(100, 100);
    image.setAnnotation(new Annotation(0, 0, 0, 0, 3));
    canvas.addImage(image);

//    ColumnText.showTextAligned(
//        canvas, Element.ALIGN_LEFT, new Phrase("Hello people!"), 250, 750, 0);
    pdfStamper.close();
    pdfReader.close();
  }

  private void imageToPDF() {
    Document document = new Document();
    String input = "driving-licence.jpeg"; // .gif and .jpg are ok too!
    String output = "driving-licence-stamped.pdf";
    try {
      FileOutputStream fos = new FileOutputStream(output);
      PdfWriter writer = PdfWriter.getInstance(document, fos);
      writer.open();
      document.open();
      document.add(Image.getInstance(input));
      document.close();
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void docToPDF() throws IOException {
    String inputFile = "sampleDocument.docx";
    String outputFile = "sampleDocument-stamped.pdf";
//    if (args != null && args.length == 2) {
//      inputFile=args[0];
//      outputFile=args[1];
//    }
    System.out.println("inputFile:" + inputFile + ",outputFile:" + outputFile);
    FileInputStream in = new FileInputStream(inputFile);
    XWPFDocument document = new XWPFDocument(in);
    File outFile = new File(outputFile);
    OutputStream out = new FileOutputStream(outFile);
    PdfOptions options = PdfOptions.create();
    PdfConverter.getInstance().convert(document, out, options);
  }

//
//  public void convertOtherImages2pdf(byte[] in, OutputStream out, String title, String author)
//      throws IOException, DocumentException {
//    Image image = Image.getInstance(in);
//
//    Rectangle imageSize = new Rectangle(image.getPlainWidth() + 1f, image.getHeight() + 1f);
//    image.scaleAbsolute(image.getPlainWidth(), image.getHeight());
//    Document document = new Document(imageSize, 0, 0, 0, 0);
//    PdfWriter writer = PdfWriter.getInstance(document, out);
//    document.open();
//    document.add(image);
//    document.close();
//    writer.close();
//  }
}
