package ru.hepera.bug.tracker.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.hepera.bug.tracker.model.Defect;

public class FileUtil {

  public static InputStream getCsv(Iterable<Defect> defects) {

    String[] headers = {"ID", "NAME", "STATE", "IMPORTANCE",
      "EXECUTOR", "AUTHOR", "FOUND_VERSION", "FIX_VERSION",
      "STEPS", "EXPECTED_RESULT", "ACTUAL_RESULT"};

    StringWriter sw = new StringWriter();
    CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader(headers).build();

    try (final CSVPrinter printer = new CSVPrinter(sw, csvFormat)) {
      defects.forEach(defect -> {
        try {
          printer.printRecord(defect.getId(), defect.getName(), defect.getState().toString(),
            defect.getImportance().toString(), defect.getExecutor().getUsername(), defect.getAuthor().getUsername(),
            defect.getFoundVersion(), defect.getFixVersion(), defect.getSteps(), defect.getExpectedResult(),
            defect.getActualResult());
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new ByteArrayInputStream(sw.toString().getBytes());
  }

  public static InputStream getXlsx(Iterable<Defect> defects) {
    byte[] bytes = new byte[0];
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      //Страница
      Sheet sheet = workbook.createSheet("Defects");
      //Заголовок
      Row header = sheet.createRow(0);
      Cell headerCell = header.createCell(0);
      headerCell.setCellValue("ID");
      headerCell = header.createCell(1);
      headerCell.setCellValue("NAME");
      headerCell = header.createCell(2);
      headerCell.setCellValue("STATE");
      headerCell = header.createCell(3);
      headerCell.setCellValue("IMPORTANCE");
      headerCell = header.createCell(4);
      headerCell.setCellValue("EXECUTOR");
      headerCell = header.createCell(5);
      headerCell.setCellValue("AUTHOR");
      headerCell = header.createCell(6);
      headerCell.setCellValue("FOUND_VERSION");
      headerCell = header.createCell(7);
      headerCell.setCellValue("FIX_VERSION");
      headerCell = header.createCell(8);
      headerCell.setCellValue("STEPS");
      headerCell = header.createCell(9);
      headerCell.setCellValue("EXPECTED_RESULT");
      headerCell = header.createCell(10);
      headerCell.setCellValue("ACTUAL_RESULT");
      //Содержимое
      List<Defect> defectList = new LinkedList<>();
      defects.forEach(defectList::add);
      for (int i = 0; i < defectList.size(); i++) {
        //создание строки
        Row row = sheet.createRow(2 + i);
        //берем дефект
        Defect defect = defectList.get(i);
        //запись полей
        Cell cell = row.createCell(0);
        cell.setCellValue(defect.getId());
        cell = row.createCell(1);
        cell.setCellValue(defect.getName());
        cell = row.createCell(2);
        cell.setCellValue(defect.getState().toString());
        cell = row.createCell(3);
        cell.setCellValue(defect.getImportance().toString());
        cell = row.createCell(4);
        cell.setCellValue(defect.getExecutor().getUsername());
        cell = row.createCell(5);
        cell.setCellValue(defect.getAuthor().getUsername());
        cell = row.createCell(6);
        cell.setCellValue(defect.getFoundVersion());
        cell = row.createCell(7);
        cell.setCellValue(defect.getFixVersion());
        cell = row.createCell(8);
        cell.setCellValue(defect.getSteps());
        cell = row.createCell(9);
        cell.setCellValue(defect.getExpectedResult());
        cell = row.createCell(10);
        cell.setCellValue(defect.getActualResult());
      }
      //вывод
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      workbook.write(baos);
      bytes = baos.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new ByteArrayInputStream(bytes);
  }

  public static String convertFileToBase64(InputStream fileStream) throws IOException {
    byte[] buffer = new byte[fileStream.available()];
    fileStream.read(buffer);
    return Base64.getEncoder().encodeToString(buffer);
  }

  public static File convertBase64ToFile(String fileName, String base64Value) throws IOException {
    byte[] buffer = Base64.getDecoder().decode(base64Value);
    File targetFile = new File(fileName);
    try (OutputStream outStream = new FileOutputStream(targetFile)) {
      outStream.write(buffer);
    }
    return targetFile;
  }
}
