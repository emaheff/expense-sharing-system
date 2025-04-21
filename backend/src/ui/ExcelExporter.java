package ui;

import logic.Category;
import logic.Event;
import logic.Participant;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExcelExporter {


    private static void createExpensesSheet(Event event, Workbook workbook) {
        Sheet expensesSheet = workbook.createSheet("Expenses");
        Row header1 = expensesSheet.createRow(0);
        header1.createCell(0).setCellValue("Amount Paid");
        header1.createCell(1).setCellValue("Paid By");
        header1.createCell(2).setCellValue("Category");

        int rowIndex = 1;

        for (Participant participant : event.getParticipants()) {
            for (Category category: participant.getExpenses().keySet()) {
                String categoryName = category.getName();
                double amount = participant.getExpenses().get(category);

                Row row = expensesSheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(amount);
                row.createCell(1).setCellValue(participant.getName());
                row.createCell(2).setCellValue(categoryName);
            }
        }

        for (int i = 0; i < 3; i++) {
            expensesSheet.autoSizeColumn(i);
        }
    }

    private static void createConsumersSheet(Event event, Workbook workbook) {
        Sheet sheet = workbook.createSheet("Consumers");

        List<Category> categories = event.getCategories();
        List<Participant> participants = event.getParticipants();


        Row header = sheet.createRow(0);
        for (int col = 0; col < categories.size(); col++) {
            header.createCell(col).setCellValue(categories.get(col).getName());
        }

        // for each col (category) we will save the list of the consumers
        List<List<String>> columnsData = new ArrayList<>();
        for (Category category : categories) {
            List<String> consumers = new ArrayList<>();
            for (Participant participant : participants) {
                if (participant.getConsumedCategories().contains(category)) {
                    consumers.add(participant.getName());
                }
            }
            columnsData.add(consumers);
        }

        // calculate the maximum number of rows for the category with the most participants
        int maxRows = columnsData.stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        // enter participants names to rows
        for (int rowIdx = 1; rowIdx <= maxRows; rowIdx++) {
            Row row = sheet.createRow(rowIdx);
            for (int col = 0; col < columnsData.size(); col++) {
                List<String> consumers = columnsData.get(col);
                if (rowIdx - 1 < consumers.size()) {
                    row.createCell(col).setCellValue(consumers.get(rowIdx - 1));
                }
            }
        }


        for (int col = 0; col < categories.size(); col++) {
            sheet.autoSizeColumn(col);
        }
    }


    public static void exportToFile(Event event) {

        Workbook workbook = new XSSFWorkbook();

        createExpensesSheet(event, workbook);
        createConsumersSheet(event, workbook);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = event.getDate().format(formatter);


        String fileName = event.getEventName().replaceAll("\\s+", "_") + "_" + formattedDate + ".xlsx";


        String outputPath = "docs/excel-reports/" + fileName;


        new File("docs/excel-reports").mkdirs();


        try (FileOutputStream fileOut = new FileOutputStream(outputPath)) {
            workbook.write(fileOut);
            workbook.close();
            System.out.println("Excel file saved to: " + outputPath);
        } catch (IOException e) {
            System.err.println("Failed to write Excel file:");
            e.printStackTrace();
        }
    }

}
