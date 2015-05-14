package jdumper.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import jpcap.JpcapSender;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;

/**
 * <p>
 * .
 * </p>
 *
 * @author zhaojiankai
 * @version 1.0
 */
public class JDSenderARP  extends JDSender{

  JTextField targetIp,sendIp,sendMac,caplenField;
  JRadioButton requestCheck,replyCheck,userCheck;

  
  public JDSenderARP(JFrame parent){
    super(parent);
    


//    promiscCheck=new JCheckBox("Put into promiscuous mode");
//    promiscCheck.setSelected(true);
//    promiscCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
//    
    targetIp=new JTextField(20);
    targetIp.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
    sendIp=new JTextField(20);
    sendIp.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
    sendMac=new JTextField(20);
    sendMac.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
    sendMac.setEnabled(false);
    JPanel packetPane=new JPanel();
    packetPane.add(new JLabel("目标IP"));
    packetPane.add(targetIp);
    packetPane.add(new JLabel("发送IP"));
    packetPane.add(sendIp);
    packetPane.add(new JLabel("发送MAC"));
    packetPane.add(sendMac);
    packetPane.setBorder(BorderFactory.createTitledBorder("报文内容"));
    packetPane.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    
    JPanel caplenPane=new JPanel();
    caplenPane.setLayout(new BoxLayout(caplenPane,BoxLayout.Y_AXIS));
    caplenField=new JTextField("1");
    caplenField.setEnabled(false);
    caplenField.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
    requestCheck=new JRadioButton("ARP请求包");
    requestCheck.setSelected(true);
    requestCheck.setActionCommand("request");
    requestCheck.addActionListener(this);
    replyCheck=new JRadioButton("ARP应答包");
    replyCheck.setActionCommand("reply");
    replyCheck.addActionListener(this);
    
    ButtonGroup group=new ButtonGroup();
    group.add(requestCheck);
    group.add(replyCheck);
    caplenPane.add(caplenField);
    caplenPane.add(requestCheck);
    caplenPane.add(replyCheck);
    caplenPane.setBorder(BorderFactory.createTitledBorder("ARP包类型"));
    caplenPane.setAlignmentX(Component.RIGHT_ALIGNMENT);
    
//    JPanel buttonPane=new JPanel(new FlowLayout(FlowLayout.RIGHT));
//    JButton okButton=new JButton("OK");
//    okButton.setActionCommand("OK");
//    okButton.addActionListener(this);
//    JButton cancelButton=new JButton("Cancel");
//    cancelButton.setActionCommand("Cancel");
//    cancelButton.addActionListener(this);
//    buttonPane.add(okButton);
//    buttonPane.add(cancelButton);
//    buttonPane.setAlignmentX(Component.RIGHT_ALIGNMENT);
    
    JPanel westPane=new JPanel(),eastPane=new JPanel();
    westPane.setLayout(new BoxLayout(westPane,BoxLayout.Y_AXIS));
    westPane.add(Box.createRigidArea(new Dimension(5,5)));
    westPane.add(adapterPane);

    westPane.add(Box.createRigidArea(new Dimension(0,10)));
    westPane.add(packetPane);
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

  @Override
  public void actionPerformed(ActionEvent evt) {
    // TODO Auto-generated method stub
String cmd=evt.getActionCommand();
    
    if(cmd.equals("request")){
      caplenField.setText("1");
      caplenField.setEnabled(false);
      sendMac.setText("");
      sendMac.setEnabled(false);
    }else if(cmd.equals("reply")){
      caplenField.setText("2");
      caplenField.setEnabled(false);
      sendMac.setEnabled(true);
    }else if(cmd.equals("OK")){
      try {
        sendPacket();
      }
      catch(java.io.IOException e){
        JOptionPane.showMessageDialog(null,e.toString());
        jpcap=null;
      }catch(java.lang.NumberFormatException e){
          JOptionPane.showMessageDialog(null,e.toString());
            jpcap=null;
      }finally{
        dispose();
      }
        
    }else if(cmd.equals("Cancel")){
      dispose();
    }
  }
  
  public void sendPacket() throws IOException,NumberFormatException{
    JpcapSender sender = getSender(); //发送器JpcapSender，用来发送报文
    short operation = (short) Integer.parseInt(caplenField.getText());
    String targetip = targetIp.getText();
    String sendip = sendIp.getText();
    String sendmac = sendMac.getText();
    byte[] mac = null;
    if(!"".equals(sendmac)){
       mac = stomac(sendmac);
    }
    byte[] targetMac = getNextMAC(sendip,targetip);
    
    InetAddress senderIP = InetAddress.getByName(sendip); //设置本地主机的IP地址，方便接收对方返回的报文
    InetAddress targetIP = InetAddress.getByName(targetip); //目标主机的IP地址
    
    ARPPacket packet = new ARPPacket(); //开始构造一个ARP包
    packet.hardtype = ARPPacket.HARDTYPE_ETHER; //硬件类型
    packet.prototype = ARPPacket.PROTOTYPE_IP; //协议类型
    packet.operation = operation; //指明ARP包类型
    packet.hlen = 6; //物理地址长度
    packet.plen = 4; //协议地址长度
    packet.sender_hardaddr = device.mac_address; //ARP包的发送端以太网地址
    if (operation == 2){
      packet.sender_hardaddr = mac;
    }
    packet.sender_protoaddr = senderIP.getAddress(); //发送端IP地址

    byte[] broadcast = new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255,(byte) 255 }; //广播地址
    packet.target_hardaddr = broadcast; //设置目的端的以太网地址为广播地址
    if (operation == 2){
      packet.target_hardaddr = targetMac;
    }
    packet.target_protoaddr = targetIP.getAddress(); //目的端IP地址

    //构造以太帧首部
    EthernetPacket ether = new EthernetPacket();
    ether.frametype = EthernetPacket.ETHERTYPE_ARP; //帧类型
    ether.src_mac = device.mac_address; //源MAC地址
    if (operation == 2){
      ether.src_mac = mac;
    }
    ether.dst_mac = broadcast; //以太网目的地址，request包为广播地址,reply包为目的地址
    if (operation == 2){
      ether.dst_mac = targetMac;
    }
    packet.datalink = ether; //将arp报文的数据链路层的帧设置为刚刚构造的以太帧赋给

    startCapture("arp");//开始捕获
    sender.sendPacket(packet); //发送ARP报文
    
   
  }


}

