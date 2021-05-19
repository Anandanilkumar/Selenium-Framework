package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


public class Excel 
{
	
  public static String getPropertyValue(String key){
		
		Properties prop=new Properties();
		String s2 = System.getProperty("user.dir");
		String path=s2+"\\src\\resources\\GlobalVariable.properties";
	     try {
			prop.load(new FileInputStream(path));
		} catch (Exception e) {
			
		}
	     
	     String value=prop.getProperty(key);
		return value;			
	}
  
 
	
	public static int getRowCount(String path,String sheet)
	{
		int r=0;
		try{
			
			r=WorkbookFactory.create(new FileInputStream(path)).getSheet(sheet).getLastRowNum();
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return r;
	}
	
	public static int getColumnCount(String path,String sheet,int row)
	{
		int c=0;
		try{
			
			c=WorkbookFactory.create(new FileInputStream(path)).getSheet(sheet).getRow(row).getLastCellNum();
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return c;
	}
	/**
	  * Description : to get the excel cell value
	  * @param path: Excel path
	  * @param sheet: Excel sheet
	  * @param row : Row value
	  * @param column : cell value
	  *@Sample format:
	  * @author A-7271
	  */
	public static String getCellValue(String path,String sheetName,String rowValue,String columnValue)
	{
		String value="";
		try{
			
			
			FileInputStream fileInputStream = new FileInputStream(path);
			Sheet sheet = null;
			Workbook workBook = null;
			workBook = new HSSFWorkbook(fileInputStream);

			sheet = workBook.getSheet(sheetName);
			
			

			  Iterator<Row> rows = sheet.rowIterator();
	            int columnIndex = -1;
	            int rowIndex = -1;
	            while (rows.hasNext()) {
	                  Row row = rows.next();
	                  row.getRowNum();
	                  
	                  for (Cell cell : row) {
	                	 
	                         if (cell.toString().equals(columnValue)) {
	                        	
	                                columnIndex = cell.getColumnIndex();
	                             
	                                break;
	                         }
	                  }
	                  if (columnIndex != -1)
	                         break;
	            }

	            rows = sheet.rowIterator();
	            while (rows.hasNext()) {
	                  Row row = rows.next();
	                  for (Cell cell : row) {
	                	 
	                	   if (cell.toString().equals(rowValue)) {
	                                rowIndex = row.getRowNum();
	                             
	                                break;
	                         }
	                  }
	                  if (rowIndex != -1)
	                         break;
	            }
	            Row row = sheet.getRow(rowIndex);
	            
	            Cell cell = row.getCell(columnIndex, Row.CREATE_NULL_AS_BLANK);
	           
	           
	            value=cell.toString();
	            return value;
	            
	           
		}
		catch(Exception e)
		{
			return "";
		}
		
	}
	public static String getCellValue(String path,String sheet,int r,int c)
	{
		String v="";
		try{
			
			v=WorkbookFactory.create(new FileInputStream(path)).getSheet(sheet).getRow(r).getCell(c).toString();
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return v;
	}
	
 public static int getColumnCount(String path,String sheet)
	{
		int c=0;
		try{
			
			c=WorkbookFactory.create(new FileInputStream(path)).getSheet(sheet).getRow(0).getLastCellNum();
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return c;
	}
	
	public static void setCellValue(String path,String sheet,int r,int c,String Value)
	{		
		try{
			
			FileInputStream fis=new FileInputStream(path);
			Workbook wb=WorkbookFactory.create(fis);
			Sheet sh=wb.getSheet(sheet);
			Row row=sh.getRow(r);
			Cell cell=row.createCell(c);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(Value);
			FileOutputStream fos=new FileOutputStream(path);
			wb.write(fos);
			fos.close();
			}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		
	}
	 public static void deleteXmlinXmlFiles() throws IOException 
	  {
		   String rootdir = System.getProperty("user.dir");
		   String subdir=Excel.getPropertyValue("filepath");
		  String folderpath = rootdir+subdir+Excel.getPropertyValue("xmlfilepath");
			
			File file = new File(folderpath);
			// Object files contains all the files under the selected folder
			File[] files = file.listFiles();
			if (files != null) {
				FileUtils.cleanDirectory(file);
				System.out.println("Delete operation is Completed.");
			}
		}
}
