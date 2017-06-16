package com.jedlab.framework.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader
{

    InputStream file;
    String extension;
    /**
     * pass this class for further development with java reflection
     */
    ExcelDecorator excelDecorator;

    public ExcelReader(InputStream file, String extension, ExcelDecorator excelDecorator)
    {
        this.file = file;
        this.extension = extension;
        this.excelDecorator = excelDecorator;
    }

    public void read() throws IOException
    {
        org.apache.poi.ss.usermodel.Sheet sheet = sheetFactory(this.extension, this.file);
        if (sheet == null)
            return;
        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = sheet.iterator();

        // skip header
        if (excelDecorator.skipHeader())
        {
            Row next = rowIterator.next();
        }
        while (rowIterator.hasNext())
        {
            Row row = (Row) rowIterator.next();
            RowCell rc = new RowCell(row);
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext())
            {
                Cell cell = (Cell) cellIterator.next();
                rc.addToCell(cell);
            }
            if (rc.hasCell())
            {
                excelDecorator.processRowWithCell(rc);
            }
        }
    }

    public static class RowCell
    {
        private Row row;
        private List<Cell> cells;

        public RowCell(Row row)
        {
            this.row = row;
            this.cells = new ArrayList<>();
        }

        public void addToCell(Cell cell)
        {            
            cells.add(cell);
        }

        public boolean hasCell()
        {
            return cells != null && cells.size() > 0;
        }

        public Row getRow()
        {
            return row;
        }

        public List<Cell> getCells()
        {
            return Collections.unmodifiableList(cells);
        }

    }

    public org.apache.poi.ss.usermodel.Sheet sheetFactory(String extension, InputStream is) throws IOException
    {
        if (extension == null || "".equals(extension.trim()))
            return null;
        if (extension.endsWith("xls"))
        {
            HSSFWorkbook workbook = new HSSFWorkbook(is);
            return workbook.getSheetAt(0);
        }
        if (extension.endsWith("xlsx"))
        {
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            return workbook.getSheetAt(0);
        }
        return null;
    }

}
