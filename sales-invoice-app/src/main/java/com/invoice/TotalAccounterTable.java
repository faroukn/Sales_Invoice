package com.invoice;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import org.apache.commons.csv.*;
import java.io.*;
import javax.swing.event.*;

public class TotalAccounterTable implements Runnable{

	private JTable table;
	private DefaultTableModel dtm;
	private int row;
	private int col;
	public TotalAccounterTable(JTable table,DefaultTableModel dtm,int row,int col){
		this.table=table;
		this.dtm=dtm;
		this.row=row;
		this.col=col;
	}

	public void run(){
		while(true){
			try{
				
				if(dtm.getValueAt(row, col) != null && dtm.getValueAt(row, col+1) != null){
					int itemPrice = Integer.valueOf((String)dtm.getValueAt(row, col));
					int count = Integer.valueOf((String)dtm.getValueAt(row, col+1));
					int total = itemPrice*count;
					dtm.setValueAt(total,row,col+2);
					dtm.fireTableDataChanged();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
