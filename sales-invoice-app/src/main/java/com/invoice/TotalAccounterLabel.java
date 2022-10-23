package com.invoice;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import org.apache.commons.csv.*;
import java.io.*;
import javax.swing.event.*;

public class TotalAccounterLabel implements Runnable{

        private JLabel label;
        private int numOfRows;
	private DefaultTableModel dtm;

        public TotalAccounterLabel(JLabel label,DefaultTableModel dtm,int numOfRows){
                this.label=label;
                this.numOfRows=numOfRows;
		this.dtm=dtm;
        }

        public void run(){
                while(true){
                        try{
				int[] allTotals = new int[numOfRows];
				for(int i=0;i<numOfRows;i++){
                                	if(dtm.getValueAt(i, 3) != null){
						String string= dtm.getValueAt(i, 3).toString();
 						int total = Integer.parseInt(string);
						allTotals[i] = total;
                                	}
				}
				int totals =0;
				for(int j=0;j<allTotals.length;j++){
					totals+=allTotals[j];
				}
				String stotals=Integer.toString(totals);
				label.setText(stotals);

                        }catch(Exception e){
                                e.printStackTrace();
                        }
                }
        }
}

