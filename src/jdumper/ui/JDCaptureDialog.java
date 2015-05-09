package jdumper.ui;
import jpcap.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

public class JDCaptureDialog extends JDialog implements ActionListener
{
	public static JpcapCaptor jpcap=null;
	public  	ArrayList<String>pros = null;
  public int pid;
	IdentityHashMap<String,Integer> process =null;
	String[] proComBoxItem = null;
	NetworkInterface[] devices;
	
	JComboBox adapterComboBox,processComboBox;
	JTextField filterField,caplenField;
	JRadioButton wholeCheck,headCheck,userCheck;
	JCheckBox promiscCheck;
	
	public JDCaptureDialog(JFrame parent){
		super(parent,"Choose Device and Options",true);
		
		devices=JpcapCaptor.getDeviceList();
		if(devices==null){
			JOptionPane.showMessageDialog(parent,"No device found.");
			dispose();
			return;
		}else{
			String[] names=new String[devices.length];
			for(int i=0;i<names.length;i++)
				names[i]=(devices[i].description==null?devices[i].name:devices[i].description);
			adapterComboBox=new JComboBox(names);
		}
		
    processComboBox = new JComboBox();
    processComboBox.insertItemAt("选择进程", 0);
    processComboBox.insertItemAt("System,4", 1);
		process = getProcess();
		Iterator it = process.keySet().iterator();
		int i = 2;
		while(it.hasNext()){
		  //HashMap<String,Integer> temp = (HashMap<String, Integer>) it.next();

		  String temp =  (String) it.next();
		  String item = temp+","+process.get(temp);
		  processComboBox.insertItemAt(item, i);
		  i++;
		}

		processComboBox.setSelectedIndex(0);
		JPanel adapterPane=new JPanel();
		adapterPane.add(adapterComboBox);
		adapterPane.add(processComboBox);
		adapterPane.setBorder(BorderFactory.createTitledBorder("Choose capture device"));
		adapterPane.setAlignmentX(Component.LEFT_ALIGNMENT);

		promiscCheck=new JCheckBox("Put into promiscuous mode");
		promiscCheck.setSelected(true);
		promiscCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		filterField=new JTextField(20);
		//filterField.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
		JPanel filterPane=new JPanel();
		filterPane.add(new JLabel("Filter"));
		filterPane.add(filterField);
		filterPane.setBorder(BorderFactory.createTitledBorder("Capture filter"));
		filterPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		
		JPanel caplenPane=new JPanel();
		caplenPane.setLayout(new BoxLayout(caplenPane,BoxLayout.Y_AXIS));
		caplenField=new JTextField("1514");
		caplenField.setEnabled(false);
		caplenField.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
		wholeCheck=new JRadioButton("Whole packet");
		wholeCheck.setSelected(true);
		wholeCheck.setActionCommand("Whole");
		wholeCheck.addActionListener(this);
		headCheck=new JRadioButton("Header only");
		headCheck.setActionCommand("Head");
		headCheck.addActionListener(this);
		userCheck=new JRadioButton("Other");
		userCheck.setActionCommand("Other");
		userCheck.addActionListener(this);
		ButtonGroup group=new ButtonGroup();
		group.add(wholeCheck);
		group.add(headCheck);
		group.add(userCheck);
		caplenPane.add(caplenField);
		caplenPane.add(wholeCheck);
		caplenPane.add(headCheck);
		caplenPane.add(userCheck);
		caplenPane.setBorder(BorderFactory.createTitledBorder("Max capture length"));
		caplenPane.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		JPanel buttonPane=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton okButton=new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		JButton cancelButton=new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		buttonPane.add(okButton);
		buttonPane.add(cancelButton);
		buttonPane.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		JPanel westPane=new JPanel(),eastPane=new JPanel();
		westPane.setLayout(new BoxLayout(westPane,BoxLayout.Y_AXIS));
		westPane.add(Box.createRigidArea(new Dimension(5,5)));
		westPane.add(adapterPane);
		westPane.add(Box.createRigidArea(new Dimension(0,10)));
		westPane.add(promiscCheck);
		westPane.add(Box.createRigidArea(new Dimension(0,10)));
		westPane.add(filterPane);
		westPane.add(Box.createVerticalGlue());
		eastPane.add(Box.createRigidArea(new Dimension(5,5)));
		eastPane.setLayout(new BoxLayout(eastPane,BoxLayout.Y_AXIS));
		eastPane.add(caplenPane);
		eastPane.add(Box.createRigidArea(new Dimension(5,30)));
		eastPane.add(buttonPane);
		eastPane.add(Box.createRigidArea(new Dimension(5,5)));
		
		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.X_AXIS));
		getContentPane().add(Box.createRigidArea(new Dimension(10,10)));
		getContentPane().add(westPane);
		getContentPane().add(Box.createRigidArea(new Dimension(10,10)));
		getContentPane().add(eastPane);
		getContentPane().add(Box.createRigidArea(new Dimension(10,10)));
		pack();
		
		setLocation(parent.getLocation().x+100,parent.getLocation().y+100);
	}
	
	public void actionPerformed(ActionEvent evt){
		String cmd=evt.getActionCommand();
		
		if(cmd.equals("Whole")){
			caplenField.setText("1514");
			caplenField.setEnabled(false);
		}else if(cmd.equals("Head")){
			caplenField.setText("68");
			caplenField.setEnabled(false);
		}else if(cmd.equals("Other")){
			caplenField.setText("");
			caplenField.setEnabled(true);
			caplenField.requestFocus();
		}else if(cmd.equals("OK")){
			try{
				int caplen=Integer.parseInt(caplenField.getText());
				if(caplen<68 || caplen>1514){
					JOptionPane.showMessageDialog(null,"Capture length must be between 68 and 1514.");
					return;
				}
				if(!(processComboBox.getSelectedItem().equals("选择进程"))){
				  String selectItem = (String) (processComboBox.getSelectedItem());
				   pid = Integer.parseInt(selectItem.split(",")[1]);
				  //pros =getProByPid(pid);
				}
				jpcap=JpcapCaptor.openDevice(devices[adapterComboBox.getSelectedIndex()],caplen,
						promiscCheck.isSelected(),50);
				
				if(filterField.getText()!=null && filterField.getText().length()>0){
					jpcap.setFilter(filterField.getText(),true);
				}
			}catch(NumberFormatException e){
				JOptionPane.showMessageDialog(null,"Please input valid integer in capture length.");
			}catch(java.io.IOException e){
				JOptionPane.showMessageDialog(null,e.toString());
				jpcap=null;
			}finally{
				dispose();
			}
		}else if(cmd.equals("Cancel")){
			dispose();
		}
	}
	
	public static JDCaptureDialog getJpcap(JFrame parent){
		JDCaptureDialog capDialog = new JDCaptureDialog(parent);
		capDialog.setVisible(true);//产生对话框
		return capDialog;
	}
	
	 public   ArrayList<String> getProByPid(int pid) {
	    ArrayList<String> pro = new ArrayList<String>();
	    int lineNum = 0;
	    int proNum =0;
	    try {
	     // Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
	      Process p = Runtime.getRuntime().exec("netstat -anob");
	      BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream(),"GB2312"));
	      String line = input.readLine();
	      while (line!= null) {
	        if(line.indexOf(":")!=-1){
	          line = line.trim();
	           // System.out.println(line);
	            lineNum++;
	            String[] pids = line.split(" ");
	            if (Integer.parseInt(pids[pids.length-1])==pid){
	              String[] test = line.split("\\:");
	                String[] test1 = test[1].split(" ");
	                pro.add(test1[0]);
	              proNum++;
	            }
	          }
	        line = input.readLine();
	      }
	      input.close();
	    }catch (Exception err) {
	      err.printStackTrace();
	    }
	    if(proNum==0){
	      //JOptionPane.showMessageDialog(null,"选择进程没有网络活动");
	      return null;
	    }
	    return pro;
	}
  
	 public  IdentityHashMap<String,Integer> getProcess() {
     Process proc;
     String proStr;
     int pid = 0;
     IdentityHashMap<String,Integer> process = new IdentityHashMap<String, Integer>();
     try {
         proc = Runtime.getRuntime().exec("tasklist");
         BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream(),"GB2312"));
         String info = br.readLine();
         int lineNum = 0;
         while (info != null) {
           lineNum++;
           if(lineNum<=5){
             info = br.readLine();
             continue;
           }
             String[] test = info.split(" ");
             proStr = test[0];
             for(int  i=1;i<test.length;i++){
               if(!"".equals(test[i])){
                 pid = Integer.parseInt(test[i]);
                 break;
               }
             }
        //     System.out.println(proStr+"    "+pid);
             info = br.readLine();
             process.put(proStr, pid);
         }
     } catch (IOException e) {
         e.printStackTrace();
     } 
     return process;
 }
}
