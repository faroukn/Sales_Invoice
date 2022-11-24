import javax.swing.*;
import javax.swing.table.*;
import org.apache.commons.csv.*;
import java.util.List;
import java.util.Arrays;

public class SalesInvoice{

    public SalesInvoice(CSVPrinter csvPrinter,JTable table,DefaultTableModel dtm,String customerName,String invoiceDate,String invoiceTotal){
        try{
            for(int i=0; i< dtm.getRowCount(); i++){
                String[] stringArray = new String[] {invoiceDate,customerName,invoiceTotal, "", "", "", "" };
                List stringList = Arrays.asList(stringArray);
                for(int j=0; j < dtm.getColumnCount(); j++){
                    String data = String.valueOf(dtm.getValueAt(i, j));
                    stringList.set(j+3, data);

                }
                csvPrinter.printRecord(stringList);
            }
            csvPrinter.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
