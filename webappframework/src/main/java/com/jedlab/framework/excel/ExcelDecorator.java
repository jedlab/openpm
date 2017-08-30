package com.jedlab.framework.excel;

import java.util.List;

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
    
    public List getResult();

    default public String readValueValue(Cell cell)
    {
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType())
        {
//            return String.valueOf(cell.getNumericCellValue());
            return cell.toString();
        }
        if (Cell.CELL_TYPE_STRING == cell.getCellType())
        {
            return String.valueOf(cell.getStringCellValue());
        }
        return null;
    }

}
