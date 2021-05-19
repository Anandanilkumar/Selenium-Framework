package common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.testng.annotations.Optional;

public class Xls_Read {
	private String languageXLSPath;
	private String xpathXLSPath;

	public Xls_Read(@Optional("false") String languageXLS,
			@Optional("english") String xpathFilepath) {

		languageXLSPath = languageXLS;
		xpathXLSPath = xpathFilepath;

	}

	public String getCellValue(String sheetName, int rowIndex, int colIndex) {
		InputStream inp = null;
		HSSFWorkbook wb = null;
		try {
			String s2 = System.getProperty("user.dir");
//			String path = s2 + "\\src\\resources\\"+xpathXLSPath;
			String path = s2 + xpathXLSPath;
			inp = new FileInputStream(path);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		// InputStream inp = new FileInputStream("workbook.xlsx");

		try {
			wb = new HSSFWorkbook(inp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Sheet sheet = wb.getSheet(sheetName);
		Row row = sheet.getRow(rowIndex);
		Cell cell = row.getCell(colIndex);

		return cell.getStringCellValue();
	}


    public String getCellValue(String sheetName, String key) {
        Cell retCell = null;
        InputStream inp = null;
        HSSFWorkbook wb = null;

        try {
               String s2 = System.getProperty("user.dir");
               String path = s2 + xpathXLSPath;
               inp = new FileInputStream(path);
               wb = new HSSFWorkbook(inp);
        } catch (FileNotFoundException e) {
               System.out.println("File not found");
               e.printStackTrace();
        } catch (IOException e) {
               System.out.println("An error occured while accessing the file");
               e.printStackTrace();
        }

        Sheet sheet = wb.getSheet(sheetName);
        for (Row row : sheet) {
               for (Cell cell : row) {
                     cell.setCellType(Cell.CELL_TYPE_STRING);
                     if (cell.getStringCellValue().equalsIgnoreCase(key)) {
                            int currentRow = cell.getRowIndex();
                            int curentCol = cell.getColumnIndex();
                            retCell = sheet.getRow(currentRow).getCell(curentCol + 1);
                            // break from the loop
                            break;
                     }
               }
               // Break if received the cell value
               if (retCell != null) {
                     break;
               }
        }
        return retCell.getStringCellValue();
 }
 public String getCellValue(String sheetName, String key,String value) {
        Cell retCell = null;
        InputStream inp = null;
        HSSFWorkbook wb = null;

        try {
               String s2 = System.getProperty("user.dir");
               String path = s2 + xpathXLSPath;
               inp = new FileInputStream(path);
               wb = new HSSFWorkbook(inp);
        } catch (FileNotFoundException e) {
               System.out.println("File not found");
               e.printStackTrace();
        } catch (IOException e) {
               System.out.println("An error occured while accessing the file");
               e.printStackTrace();
        }

        Sheet sheet = wb.getSheet(sheetName);
        for (Row row : sheet) {
               for (Cell cell : row) {
                     cell.setCellType(Cell.CELL_TYPE_STRING);
                     if (cell.getStringCellValue().equalsIgnoreCase(key)) {
                            int currentRow = cell.getRowIndex();
                            int curentCol = cell.getColumnIndex();
                            retCell = sheet.getRow(currentRow).getCell(curentCol - 1);
                            // break from the loop
                            break;
                     }
               }
               // Break if received the cell value
               if (retCell != null) {
                     break;
               }
        }
        return retCell.getStringCellValue();
 }

	private static int getColIndex(HSSFSheet sheet, String cellContent) {
		for (Row row : sheet) {
			for (Cell cell : row) {
				if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					if (cell.getRichStringCellValue().getString().trim()
							.equals(cellContent)) {
						return cell.getColumnIndex();
						// return row.getRowNum();
					}
				}
			}
		}
		return 0;
	}
	
	
	 public static  int getRowNum(String sheetName, String key) {
	        Cell retCell = null;
	        InputStream inp = null;
	        HSSFWorkbook wb = null;
	        int currentRow=0;

	        try {
	               String s2 = System.getProperty("user.dir");
	               String path = s2 + Excel.getPropertyValue("sanityReportFile");
	               inp = new FileInputStream(path);
	               wb = new HSSFWorkbook(inp);
	        } catch (FileNotFoundException e) {
	               System.out.println("File not found");
	               e.printStackTrace();
	        } catch (IOException e) {
	               System.out.println("An error occured while accessing the file");
	               e.printStackTrace();
	        }

	        Sheet sheet = wb.getSheet(sheetName);
	        for (Row row : sheet) {
	               for (Cell cell : row) {
	                     cell.setCellType(Cell.CELL_TYPE_STRING);
	                     if (cell.getStringCellValue().equalsIgnoreCase(key)) {
	                             currentRow = cell.getRowIndex();	                                                     
	                            // break from the loop
	                            break;
	                     }
	               }
	               // Break if received the cell value
	               if (retCell != null) {
	                     break;
	               }
	        }
	        return currentRow;
	 }
}
