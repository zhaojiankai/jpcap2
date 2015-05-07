package jdumper.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;

/**
 * <p>
 * .
 * </p>
 *
 * @author zhaojiankai
 * @version 1.0
 */
public class JDSender  extends JDialog implements ActionListener{
  static JpcapCaptor jpcap=null;
  JpcapSender sender ; //发送器JpcapSender，用来发送报文
  NetworkInterface[] devices;
  NetworkInterface device;
  //JDCaptureDialog jdcap;
  JComboBox adapterComboBox;


  public  JPanel buttonPane;
  public JPanel adapterPane;
  
  public JDSender(JFrame parent){
    super(parent,"Choose Device and Options",true);
    
    //jdcap=new JDCaptureDialog(parent);
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
    adapterPane=new JPanel();
    //adapterPane.setLayout(new BoxLayout(adapterPane,BoxLayout.X_AXIS));
    adapterPane.add(adapterComboBox);
    adapterPane.setBorder(BorderFactory.createTitledBorder("Choose capture device"));
    adapterPane.setAlignmentX(Component.LEFT_ALIGNMENT);

//    promiscCheck=new JCheckBox("Put into promiscuous mode");
//    promiscCheck.setSelected(true);
//    promiscCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
//    
//    filterField=new JTextField(20);
//    //filterField.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
//    JPanel filterPane=new JPanel();
//    filterPane.add(new JLabel("Filter"));
//    filterPane.add(filterField);
//    filterPane.setBorder(BorderFactory.createTitledBorder("Capture filter"));
//    filterPane.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    
    
    buttonPane=new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton okButton=new JButton("OK");
    okButton.setActionCommand("OK");
    okButton.addActionListener(this);
    JButton cancelButton=new JButton("Cancel");
    cancelButton.setActionCommand("Cancel");
    cancelButton.addActionListener(this);
    buttonPane.add(okButton);
    buttonPane.add(cancelButton);
    buttonPane.setAlignmentX(Component.BOTTOM_ALIGNMENT);
    
//    JPanel westPane=new JPanel(),eastPane=new JPanel();
//    westPane.setLayout(new BoxLayout(westPane,BoxLayout.Y_AXIS));
//    westPane.add(Box.createRigidArea(new Dimension(5,5)));
//    westPane.add(adapterPane);
//
//
//    westPane.add(Box.createVerticalGlue());
//    eastPane.add(Box.createRigidArea(new Dimension(5,5)));
//
//    eastPane.add(Box.createRigidArea(new Dimension(5,30)));
//    eastPane.add(buttonPane);
//    eastPane.add(Box.createRigidArea(new Dimension(5,5)));
//    
//    getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.X_AXIS));
//    getContentPane().add(Box.createRigidArea(new Dimension(10,10)));
//    getContentPane().add(westPane);
//    getContentPane().add(Box.createRigidArea(new Dimension(10,10)));
//    getContentPane().add(eastPane);
//    getContentPane().add(Box.createRigidArea(new Dimension(10,10)));
//    pack();
//    
//    setLocation(parent.getLocation().x+100,parent.getLocation().y+100);
    
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub
    
  }
  /**
   * 将字符串形式的MAC地址转换成存放在byte数组内的MAC地址
   * 
   * @param str 字符串形式的MAC地址，如：AA-AA-AA-AA-AA
   * @return 保存在byte数组内的MAC地址
   */
  public static byte[] stomac(String str) throws NumberFormatException {
    byte[] mac = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00 };
    String[] temp = str.split("-");
    for (int x = 0; x < temp.length; x++) {
      mac[x] = (byte) ((Integer.parseInt(temp[x], 16)) & 0xff);
    }
    return mac;
  }
  public JpcapSender getSender() throws IOException {

      device = devices[adapterComboBox.getSelectedIndex()];
      //jpcap = JpcapCaptor.openDevice(devices[adapterComboBox.getSelectedIndex()], 2000, false, 3000);
      this.sender = JpcapSender.openDevice(device);
      return this.sender;

  }
public void startCapture(String filter) throws IOException{
//  JDCaptureDialog jdcap = new j
  jpcap=JpcapCaptor.openDevice(device,2000,false,3000);
  

  jpcap.setFilter(filter,true);

}

public static JpcapCaptor getJpcap(JFrame parent,String ptype) {
  JDSender jdsender = null;
  Class<?> class1 = null;
  try {
    class1 = Class.forName("jdumper.ui.JDSender"+ptype);

  Constructor<?>[] constructors = class1.getConstructors();
  jdsender = (JDSender) constructors[0].newInstance(parent);
  jdsender.setVisible(true);
  }
  catch (ClassNotFoundException e) {
    // TODO Auto-generated catch block
    JOptionPane.showMessageDialog(null,e.toString());
  }
  catch (IllegalArgumentException e) {
    // TODO Auto-generated catch block
    JOptionPane.showMessageDialog(null,e.toString());
  }
  catch (InstantiationException e) {
    // TODO Auto-generated catch block
    JOptionPane.showMessageDialog(null,e.toString());
  }
  catch (IllegalAccessException e) {
    // TODO Auto-generated catch block
    JOptionPane.showMessageDialog(null,e.toString());
  }
  catch (InvocationTargetException e) {
    // TODO Auto-generated catch block
    JOptionPane.showMessageDialog(null,e.toString());
  }

  return jdsender.jpcap;
}


//  @Override
//  public void actionPerformed(ActionEvent evt) {
//    // TODO Auto-generated method stub
//String cmd=evt.getActionCommand();
//    
//    if(cmd.equals("request")){
//      caplenField.setText("1514");
//      caplenField.setEnabled(false);
//    }else if(cmd.equals("reply")){
//      caplenField.setText("68");
//      caplenField.setEnabled(false);
//    }else if(cmd.equals("Other")){
//      caplenField.setText("");
//      caplenField.setEnabled(true);
//      caplenField.requestFocus();
//    }else if(cmd.equals("OK")){
//      try{
//        int caplen=Integer.parseInt(caplenField.getText());
//        if(caplen<68 || caplen>1514){
//          JOptionPane.showMessageDialog(null,"Capture length must be between 68 and 1514.");
//          return;
//        }
//        
//        jpcap=JpcapCaptor.openDevice(devices[adapterComboBox.getSelectedIndex()],caplen,
//            promiscCheck.isSelected(),50);
//        
//        if(filterField.getText()!=null && filterField.getText().length()>0){
//          jpcap.setFilter(filterField.getText(),true);
//        }
//      }catch(NumberFormatException e){
//        JOptionPane.showMessageDialog(null,"Please input valid integer in capture length.");
//      }catch(java.io.IOException e){
//        JOptionPane.showMessageDialog(null,e.toString());
//        jpcap=null;
//      }finally{
//        dispose();
//      }
//    }else if(cmd.equals("Cancel")){
//      dispose();
//    }
//  }

}

