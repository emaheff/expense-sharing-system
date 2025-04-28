package backend.storage;

import backend.logic.Category;
import backend.logic.Event;
import backend.logic.Participant;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ExcelExporter generates an Excel report for a given Event using Apache POI.
 * It includes sheets for expenses, consumptions, participant balances, debts, and category summaries.
 */
public class ExcelExporter {

    static final String PATH = "docs/excel-reports/";

    /**
     * Creates a sheet listing each expense with the amount, participant name, and category.
     */
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

    /**
     * Creates a sheet showing which participants consumed each category.
     * Each column represents a category, and rows represent consumers.
     */
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

    /**
     * Creates a sheet summarizing debts between participants.
     */
    private static void createDebtsSheet(Event event, Workbook workbook) {
        Sheet sheet = workbook.createSheet("Debts");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Debtor");
        header.createCell(1).setCellValue("Creditor");
        header.createCell(2).setCellValue("Amount");

        for (int row = 0; row < event.getDebts().size(); row++) {
            Row debtRow = sheet.createRow(row + 1);
            debtRow.createCell(0).setCellValue(event.getDebts().get(row).getDebtor().getName());
            debtRow.createCell(1).setCellValue(event.getDebts().get(row).getCreditor().getName());
            debtRow.createCell(2).setCellValue(event.getDebts().get(row).getAmount());
        }

        for (int col = 0; col < 3; col++) {
            sheet.autoSizeColumn(col);
        }
    }

    /**
     * Creates a sheet with participants' financial summary including
     * total consumed (including participation fee), total expense, and net balance.
     */
    private static void creteParticipantBalanceSheet(Event event, Workbook workbook) {
        Sheet sheet = workbook.createSheet("Participants Balance");

        Row header = sheet.createRow(0);
        header.createCell(1).setCellValue("Total Consumed");
        header.createCell(2).setCellValue("Total Expense");
        header.createCell(3).setCellValue("Balance");

        List<Participant> participants = event.getParticipants();
        for (int row = 0; row < participants.size(); row++) {
            Row participantRow = sheet.createRow(row + 1);
            participantRow.createCell(0).setCellValue(participants.get(row).getName());
            double totalConsumed = participants.get(row).getTotalConsumed();
            participantRow.createCell(1).setCellValue(totalConsumed + event.getParticipationFee());
            participantRow.createCell(2).setCellValue(participants.get(row).getTotalExpense());
            participantRow.createCell(3).setCellValue(participants.get(row).getBalance());
        }

        for (int col = 0; col <= 3; col++) {
            sheet.autoSizeColumn(col);
        }
    }

    /**
     * Creates a sheet summarizing raw and adjusted costs per category,
     * including per-consumer cost and total participation fee.
     */
    private static void createCategorySummarySheet(Event event, Workbook workbook) {
        List<Category> eventCategory = event.getCategories();
        double totalParticipationFee = event.getParticipationFee() * event.getParticipants().size();
        Map<Category, Double> expensePerCategory = event.getTotalExpensePerCategory();

        Sheet sheet = workbook.createSheet("Category Summary");

        Row header = sheet.createRow(0);
        Row rawCostRow = sheet.createRow(1);
        Row adjustedCostRow = sheet.createRow(2);
        Row perCnsumerRow = sheet.createRow(3);

        rawCostRow.createCell(0).setCellValue("Raw cost");
        adjustedCostRow.createCell(0).setCellValue("Adjust Cost");
        perCnsumerRow.createCell(0).setCellValue("Per Consumer");

        for (int col = 0; col <eventCategory.size(); col++) {
            Category category = eventCategory.get(col);
            String categoryName = category.getName();

            header.createCell(col + 1).setCellValue(categoryName);
            rawCostRow.createCell(col + 1).setCellValue(expensePerCategory.get(category));
            double adjustPrice = event.getAdjustedTotalExpensePerCategory().get(category);
            adjustedCostRow.createCell(col + 1).setCellValue(adjustPrice);
            double perConsumer = adjustPrice / event.getConsumedPerCategory().get(category).size();
            perCnsumerRow.createCell(col + 1).setCellValue(perConsumer);
        }

        int feeCol = eventCategory.size() + 1;
        header.createCell(feeCol).setCellValue("Total Participation Fee");
        rawCostRow.createCell(feeCol).setCellValue(totalParticipationFee);
        adjustedCostRow.createCell(feeCol).setCellValue(0);
        perCnsumerRow.createCell(feeCol).setCellValue(event.getParticipationFee());

        for (int col = 0; col <= feeCol; col++) {
            sheet.autoSizeColumn(col);
        }
    }

    /**
     * Exports the entire event data to a single Excel file containing multiple sheets.
     *
     * @param event the event to export
     */
    public static void exportToFile(Event event) {

        Workbook workbook = new XSSFWorkbook();

        createExpensesSheet(event, workbook);
        createConsumersSheet(event, workbook);
        createCategorySummarySheet(event, workbook);
        creteParticipantBalanceSheet(event, workbook);
        createDebtsSheet(event, workbook);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = event.getDate().format(formatter);


        String fileName = event.getEventName().replaceAll("\\s+", "_") + "_" + formattedDate + ".xlsx";


        String outputPath = PATH + fileName;


        new File(PATH).mkdirs();


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
