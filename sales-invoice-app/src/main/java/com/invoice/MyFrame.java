package com.invoice;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import org.apache.commons.csv.*;
import java.io.*;
import java.nio.file.*;
import java.util.Vector;
import java.util.LinkedHashSet;
import javax.swing.event.*;

public class MyFrame extends JFrame implements ActionListener{
	private JTextField invoiceDate;
	private JTextField customerName;
	private JLabel invoiceTotal;
	private JPanel motherPanel;
	private JPanel lPanel;
	private JPanel lPanelButtons;
	private JButton lPanelCreate;
	private JButton lPanelDelete;
	private JPanel rPanel;
	private JPanel rPanelButtons;
	private JButton rPanelSave;
	private JButton rPanelCancel;
	private JPanel inputPanel;
	private JPanel panel0;
	private JPanel panel1;
	private JTable invoicesTable;
	private JTable table;
	private String[] headers1 = {"Item Name","Item Price","Count","Total"};
	private String[][] data1;
	private JMenuBar menuBar;
	private JMenu invoiceMenu;
	private JMenuItem loadMenuItem;
	private JMenuItem saveMenuItem;
	private int numOfItems;
	private DefaultTableModel dtm;
	private Thread[] threadArray;
	private Thread threadLabel;
	private DefaultTableModel csv_data;
	private String fileData="data.csv";
	private DefaultTableModel tmp_data;
	private DefaultTableModel tmp2_data;
	private Vector row2;
	private Vector row3;
	private JTable tabel2;
	public MyFrame(){
		super("Sales invoice generator");

		menuBar = new JMenuBar();
		loadMenuItem = new JMenuItem("Load File");
		loadMenuItem.addActionListener(this);
		loadMenuItem.setActionCommand("load");
		saveMenuItem = new JMenuItem("Save File");
		saveMenuItem.addActionListener(this);
                saveMenuItem.setActionCommand("save");
		invoiceMenu = new JMenu("File");
		invoiceMenu.add(loadMenuItem);
		invoiceMenu.add(saveMenuItem);
		menuBar.add(invoiceMenu);
		setJMenuBar(menuBar);

		motherPanel = new JPanel();
		motherPanel.setLayout(new GridLayout(0,2));
		lPanel = new JPanel();
		lPanelButtons = new JPanel();
		lPanelButtons.setLayout(new FlowLayout());
		rPanel = new JPanel();
		rPanelButtons = new JPanel();
		inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(0,2));
		panel0 = new JPanel();
                panel0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Invoices Tabel"));
                invoicesTable = new JTable();

                panel0.add(new JScrollPane(invoicesTable));
		panel0.setPreferredSize(new Dimension(480, 515));

		lPanelCreate = new JButton("Create New Invoice");
		lPanelCreate.addActionListener(this);
		lPanelCreate.setActionCommand("create");
		lPanelDelete = new JButton("Delete Invoice");
		lPanelDelete.addActionListener(this);
		lPanelDelete.setActionCommand("delete");
		lPanelButtons.add(lPanelCreate);
		lPanelButtons.add(lPanelDelete);

                lPanel.add(panel0);
		lPanel.add(lPanelButtons);

		inputPanel.add(new JLabel("Invoice Date"));
		invoiceDate = new JTextField(15);
		inputPanel.add(invoiceDate);

                inputPanel.add(new JLabel("Customer Name"));
                customerName = new JTextField(15);
                inputPanel.add(customerName);

		inputPanel.add(new JLabel("Invoice Total"));
		invoiceTotal = new JLabel("0");
		inputPanel.add(invoiceTotal);


		panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Invoice Items"));
		dtm = new DefaultTableModel(data1,headers1);
		table = new JTable(dtm);
		panel1.add(new JScrollPane(table));

		rPanelSave = new JButton("Save");
		rPanelSave.addActionListener(this);
		rPanelSave.setActionCommand("save");
                rPanelCancel = new JButton("Cancel");
		rPanelCancel.addActionListener(this);
		rPanelCancel.setActionCommand("cancel");
                rPanelButtons.add(rPanelSave);
                rPanelButtons.add(rPanelCancel);

		rPanel.add(inputPanel);
		rPanel.add(panel1);
		rPanel.add(rPanelButtons);
		setEnableRec(rPanel,false);
		motherPanel.add(lPanel);
		motherPanel.add(rPanel);
		
		tabel2 = new JTable();

		add(motherPanel);
		setSize(980,650);
		setResizable(false);
                setLocation(100,100);
                setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void cleanrPanel(){
		invoiceDate.setText("");
		customerName.setText("");
		invoiceTotal.setText("");
		dtm.setRowCount(0);
		dtm.fireTableDataChanged();

	}
	public void startTotalThreadLabel(int numOfRows,DefaultTableModel dtm){
		threadLabel = new Thread(new TotalAccounterLabel(invoiceTotal,dtm,numOfRows));
		threadLabel.start();
	}
	public void stopTotalThreadLabel(){
		threadLabel.stop();	
	}
	public void startTotalThread(int numOfThreads,DefaultTableModel dtm){
		
		threadArray = new Thread[numOfThreads];
		for(int i=0;i<numOfThreads;i++){
                        threadArray[i] = new Thread(new TotalAccounterTable(table,dtm,i,1));
                        threadArray[i].start();
                }

	}
	public void stopTotalThread(){
		for(int i=0;i<threadArray.length;i++){
			threadArray[i].stop();
		}
	}


	private void setEnableRec(Component container, boolean enable){
    		container.setEnabled(enable);

    		try {
        		Component[] components= ((Container) container).getComponents();
        		for (int i = 0; i < components.length; i++) {
            			setEnableRec(components[i], enable);
        		}
    		} catch (ClassCastException e) {

    			}
		}

	public void loadInvoice(){
		JFileChooser filechooser = new JFileChooser();
		int ap = filechooser.showOpenDialog(null);
		csv_data = new DefaultTableModel();
		tmp_data = new DefaultTableModel();
		tmp2_data = new DefaultTableModel();
		tabel2.setModel(tmp2_data);
		invoicesTable.setModel(csv_data);

                if (ap == JFileChooser.APPROVE_OPTION) {
		    setEnableRec(rPanel,true);
                    File f = filechooser.getSelectedFile();
                    String filePath = f.getPath();
                    String fileName = f.getName();
                    csv_data = new DefaultTableModel();
		    row2 = new Vector();
		    row3 = new Vector();
		    try {

                        int start = 0;
                        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(filePath));
                        CSVParser csvParser = CSVFormat.DEFAULT.parse(inputStreamReader);
			
			for (CSVRecord csvRecord : csvParser) {
                            if (start == 0) {
                                start = 1;
                                csv_data.addColumn(csvRecord.get(0));
                                csv_data.addColumn(csvRecord.get(1));
                                csv_data.addColumn(csvRecord.get(2));
				tmp2_data.addColumn(csvRecord.get(3));
				tmp2_data.addColumn(csvRecord.get(4));
				tmp2_data.addColumn(csvRecord.get(5));
				tmp2_data.addColumn(csvRecord.get(6));
				for(int i=0;i<7;i++){
					tmp_data.addColumn(i);
				}
                            } else {
                                Vector row = new Vector();
				Vector row03 = new Vector();
				Vector row04 = new Vector();
                                row.add(csvRecord.get(0));
                                row.add(csvRecord.get(1));
                                row.add(csvRecord.get(2));
				row2.add(row.get(0)+"@"+row.get(1)+"@"+row.get(2));
				LinkedHashSet hashSet = new LinkedHashSet(row2);
				row2.clear();
				row2.addAll(hashSet);
				for(int jj=3;jj<7;jj++){
					row03.add(csvRecord.get(jj));
				}

				tmp2_data.addRow(row03);
				for(int jjj=0;jjj<7;jjj++){
					row04.add(csvRecord.get(jjj));
				}
				tmp_data.addRow(row04);
                             
                            }
			}
			for(int i=0;i<row2.size();i++){
				String str = String.valueOf(row2.get(i));
				
				String[] row4=str.split("@",3);
				
				csv_data.addRow(row4);
			}
			
		    }catch(Exception e){e.printStackTrace();}
		    this.fileData=filePath;
		    invoicesTable.setModel(csv_data);
		    invoicesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
        		public void valueChanged(ListSelectionEvent event) {
				
				dtm.setRowCount(0);
				int rowCount = tabel2.getRowCount();
				

				for(int i=0;i<rowCount;i++){
					Vector<Object> rowData = tmp_data.getDataVector().elementAt(i);
					String head = rowData.get(0)+"@"+rowData.get(1)+"@"+rowData.get(2);
				
					if(head.equals(String.valueOf(row2.get(invoicesTable.getSelectedRow())))){
						
						dtm.addRow(tmp2_data.getDataVector().elementAt(i));
						invoiceDate.setText(String.valueOf(tmp_data.getValueAt(i,0)));
						invoiceDate.setEditable(false);
						customerName.setText(String.valueOf(tmp_data.getValueAt(i,1)));
						customerName.setEditable(false);
						invoiceTotal.setText(String.valueOf(tmp_data.getValueAt(i,2)));
						
						dtm.fireTableDataChanged();
					}else{
						System.out.print("");
					}
					dtm.fireTableDataChanged();
					table.setEnabled(false);
					rPanelSave.setEnabled(false);
					rPanelCancel.setEnabled(false);
				}

        		}
    	});
		}
	}

	public void saveInvoice(){
		if (customerName.getText().isBlank() | invoiceDate.getText().isBlank() | invoiceTotal.getText().equals("0")){
			JOptionPane.showMessageDialog(null,
                                "Enter All data",
                                "Save Failed",
                                JOptionPane.ERROR_MESSAGE);
		}else{

			if(new File(fileData).isFile()){
				try{
					BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileData),StandardOpenOption.APPEND, StandardOpenOption.CREATE);
					CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
					stopTotalThread();
					stopTotalThreadLabel();
					SalesInvoice si = new SalesInvoice(csvPrinter,table,dtm,customerName.getText(),invoiceDate.getText(),invoiceTotal.getText());
					JOptionPane.showMessageDialog(null,
                                		"Save Complete",
                                		"Save Complete",
                                		JOptionPane.INFORMATION_MESSAGE);

					cleanrPanel();
					setEnableRec(rPanel,false);

				}catch(Exception eee){
					JOptionPane.showMessageDialog(null,
                                	"Cant't Open Data File",
                                	"Save Failed",
                                	JOptionPane.ERROR_MESSAGE);
				}	
			}else{
				try{
					BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileData));
					CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Date", "Customer", "Total", "ItemName","ItemPrice","Count","ItemTotal"));

					stopTotalThread();
					stopTotalThreadLabel();
					SalesInvoice si = new SalesInvoice(csvPrinter,table,dtm,customerName.getText(),invoiceDate.getText(),invoiceTotal.getText());
					JOptionPane.showMessageDialog(null,
                                		"Save Complete",
                                		"Save Complete",
                                		JOptionPane.INFORMATION_MESSAGE);
	
					cleanrPanel();
					setEnableRec(rPanel,false);

				}catch(Exception eee){

					JOptionPane.showMessageDialog(null,
                                	"Cant't Creat Data File",
                                	"Save Failed",
                                	JOptionPane.ERROR_MESSAGE);
				}
				
			}
		}
	}


	public void moveDtm(DefaultTableModel dtm,int numOfItems){
						table.setModel(dtm);
						dtm.fireTableDataChanged();
						table.repaint();
						panel1.repaint();
						rPanel.repaint();
						motherPanel.repaint();
						setEnableRec(rPanel,true);
						
						startTotalThread(numOfItems,dtm);
						startTotalThreadLabel(numOfItems,dtm);
						invoiceDate.setText("");
						customerName.setText("");
						invoiceTotal.setText("");
						invoiceDate.setEditable(true);
						customerName.setEditable(true);
					}
	public void actionPerformed(ActionEvent e){
		switch (e.getActionCommand()){
			case "create":
				
				String noi = JOptionPane.showInputDialog(null, "Enter Number Of Items?", "Create Sales Invoice", JOptionPane.INFORMATION_MESSAGE);
				try{
					numOfItems = Integer.valueOf(noi);
					
					if(numOfItems <= 0){
						JOptionPane.showMessageDialog(null,
                                		"Number Must Be Positive",
                                		"Creation Failed",
                                		JOptionPane.ERROR_MESSAGE);
					}else if (numOfItems>0){
						dtm.setRowCount(0);
						Vector[] row = new Vector[headers1.length];
						for (int i=0;i<numOfItems;i++){
							
							dtm.addRow(row);
							
							if(i+1==numOfItems){
								moveDtm(dtm,numOfItems);
							}
						   }
											
					
					}
				}catch(Exception ee){
					JOptionPane.showMessageDialog(null,
                                "Inputs Must be Numbers",
                                "Craetion Failed",
                                JOptionPane.ERROR_MESSAGE);
					ee.printStackTrace();
				}
				break;

			case "delete":
				

				invoicesTable.setModel(new DefaultTableModel());
				lPanel.remove(invoicesTable);
				lPanel.revalidate();
				lPanel.repaint();
				motherPanel.repaint();
				
				cleanrPanel();
				
				break;

			case "save":
				
				saveInvoice();
				break;

			case "cancel":
				
				break;

			case "load":
				
				loadInvoice();

				break;
		}
	}

}
