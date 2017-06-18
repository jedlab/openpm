package com.jedlab.framework.excel;

import org.apache.poi.ss.usermodel.Cell;

import com.jedlab.framework.excel.ExcelReader.RowCell;

/**
 * @author omidp
 *
 */
public interface ExcelDecorator
{

    public void processRowWithCell(RowCell rowCell);

    public boolean skipHeader();

    default public String readValueValue(Cell cell)
    {
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType())
        {
            return String.valueOf((int) cell.getNumericCellValue());
        }
        if (Cell.CELL_TYPE_STRING == cell.getCellType())
        {
            return String.valueOf(cell.getStringCellValue());
        }
        return null;
    }

}
