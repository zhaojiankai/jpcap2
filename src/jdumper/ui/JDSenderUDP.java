package jdumper.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jpcap.JpcapSender;
import jpcap.packet.EthernetPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.UDPPacket;

/**
 * <p>
 * .
 * </p>
 *
 * @author zhaojiankai
 * @version 1.0
 */
public class JDSenderUDP  extends JDSender{

  JTextField targetIp,sendIp,sendContent,srcPort,dstPort;

  //JPanel protocolsPane;

  
  public JDSenderUDP(JFrame parent){
    super(parent);

    targetIp=new JTextField(20);
    targetIp.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
    sendIp=new JTextField(20);
    sendIp.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
    sendContent=new JTextField(20);
    sendContent.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
    srcPort=new JTextField(20);
    srcPort.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
    dstPort=new JTextField(20);
    dstPort.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
    //sendContent.setEnabled(false);
    JPanel packetPane=new JPanel();
    packetPane.add(new JLabel("目标IP"));
    packetPane.add(targetIp);
    packetPane.add(new JLabel("发送IP"));
    packetPane.add(sendIp);
    packetPane.add(new JLabel("发送内容"));
    packetPane.add(sendContent);
    packetPane.setLayout(new BoxLayout(packetPane,BoxLayout.Y_AXIS));
    packetPane.add(new JLabel("源端口"));
    packetPane.add(srcPort);
    packetPane.add(new JLabel("目的端口"));
    packetPane.add(dstPort);
    packetPane.setBorder(BorderFactory.createTitledBorder("报文内容"));
    packetPane.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    
//    JPanel caplenPane=new JPanel();
//    caplenPane.setLayout(new BoxLayout(caplenPane,BoxLayout.Y_AXIS));
//    caplenField=new JTextField("1");
//    caplenField.setEnabled(false);
//    caplenField.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
//    requestCheck=new JRadioButton("ARP请求包");
//    requestCheck.setSelected(true);
//    requestCheck.setActionCommand("request");
//    requestCheck.addActionListener(this);
//    replyCheck=new JRadioButton("ARP应答包");
//    replyCheck.setActionCommand("reply");
//    replyCheck.addActionListener(this);
//    
//    ButtonGroup group=new ButtonGroup();
//    group.add(requestCheck);
//    group.add(replyCheck);
//    caplenPane.add(caplenField);
//    caplenPane.add(requestCheck);
//    caplenPane.add(replyCheck);
//    caplenPane.setBorder(BorderFactory.createTitledBorder("ARP包类型"));
//    caplenPane.setAlignmentX(Component.RIGHT_ALIGNMENT);
    
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
//    protocolsPane = new JPanel();
//   // protocolsPane.setLayout(new BoxLayout(protocolsPane,BoxLayout.X_AXIS));
//    protocolsPane.add(protocolsBox);
//    protocolsPane.setBorder(BorderFactory.createTitledBorder("Choose protocol type"));
//    protocolsPane.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    JPanel westPane=new JPanel(),eastPane=new JPanel();
    westPane.setLayout(new BoxLayout(westPane,BoxLayout.Y_AXIS));
    westPane.add(Box.createRigidArea(new Dimension(5,5)));
    westPane.add(adapterPane);
//    westPane.setLayout(new BoxLayout(westPane,BoxLayout.Y_AXIS));
//    westPane.add(Box.createRigidArea(new Dimension(5,5)));
//    westPane.add(protocolsPane);

    westPane.add(Box.createRigidArea(new Dimension(0,10)));
    westPane.add(packetPane);
//    westPane.add(Box.createVerticalGlue());
//    eastPane.add(Box.createRigidArea(new Dimension(5,5)));
//    eastPane.setLayout(new BoxLayout(eastPane,BoxLayout.Y_AXIS));
//    eastPane.add(caplenPane);
    eastPane.add(Box.createRigidArea(new Dimension(5,30)));
    eastPane.add(buttonPane);
    eastPane.add(Box.createRigidArea(new Dimension(5,5)));
    
    getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
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
    
 if(cmd.equals("OK")){
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
    String targetip = targetIp.getText();
    String sendip = sendIp.getText();
    String sendcontent = sendContent.getText();
    int srcport = Integer.parseInt(srcPort.getText());
    int dstport = Integer.parseInt(dstPort.getText());
    byte[] targetMac = getMAC(sendip,targetip);

    InetAddress senderIP = InetAddress.getByName(sendip); //设置本地主机的IP地址，方便接收对方返回的报文
    InetAddress targetIP = InetAddress.getByName(targetip); //目标主机的IP地址
    
    UDPPacket packet = new UDPPacket(srcport, dstport);
    packet.setIPv4Parameter(0,false,false,false,0,false,false,false,0,1010101,100,IPPacket.IPPROTO_UDP,senderIP,targetIP);
    packet.data = sendcontent.getBytes();
    
    byte[] broadcast = new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255,(byte) 255 }; //广播地址
    //构造以太帧首部
    EthernetPacket ether = new EthernetPacket();
    ether.frametype = EthernetPacket.ETHERTYPE_IP; //帧类型
    ether.src_mac = device.mac_address; //源MAC地址
    ether.dst_mac = targetMac; //以太网目的地址，request包为广播地址,reply包为目的地址
    packet.datalink = ether; //将arp报文的数据链路层的帧设置为刚刚构造的以太帧赋给

    startCapture("udp");//开始捕获
    sender.sendPacket(packet); //发送ARP报文
    
   
  }


}

