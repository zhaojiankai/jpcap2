package jdumper.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.Arrays;

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
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;

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
    adapterPane.setBorder(BorderFactory.createTitledBorder("Choose send and capture device"));
    adapterPane.setAlignmentX(Component.LEFT_ALIGNMENT);

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
      jpcap=JpcapCaptor.openDevice(device,2000,true,3000);
      this.sender = JpcapSender.openDevice(device);
      return this.sender;

  }
public void startCapture(String filter) throws IOException{
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

public  byte[] getMAC(String srcip,String targetip) throws IOException {
  String[] src = srcip.split("\\.");
  String[] target = targetip.split("\\.");
  if (!(src[0].equals(target[0])&&src[1].equals(target[1])&&src[2].equals(target[2])))
      targetip = src[0]+"."+src[1]+"."+src[2]+".1";
    // targetip = "10.230.0.1";
  device = devices[adapterComboBox.getSelectedIndex()];
  
//  JpcapCaptor jc=JpcapCaptor.openDevice(device,2000,false,3000);
//  JpcapSender sender = jc.getJpcapSenderInstance(); //发送器JpcapSender，用来发送报文
  //InetAddress senderIP = InetAddress.getByName("10.96.33.232"); //设置本地主机的IP地址，方便接收对方返回的报文
  InetAddress senderIP = InetAddress.getByName(srcip); //设置本地主机的IP地址，方便接收对方返回的报文
  InetAddress targetIP = InetAddress.getByName(targetip); //目标主机的IP地址

  ARPPacket arp = new ARPPacket(); //开始构造一个ARP包
  arp.hardtype = ARPPacket.HARDTYPE_ETHER; //硬件类型
  arp.prototype = ARPPacket.PROTOTYPE_IP; //协议类型
  arp.operation = ARPPacket.ARP_REQUEST; //指明是ARP请求包
  arp.hlen = 6; //物理地址长度
  arp.plen = 4; //协议地址长度
  arp.sender_hardaddr = device.mac_address; //ARP包的发送端以太网地址,在这里即本地主机地址
  arp.sender_protoaddr = senderIP.getAddress(); //发送端IP地址, 在这里即本地IP地址

  byte[] broadcast = new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255,
      (byte) 255 }; //广播地址
  arp.target_hardaddr = broadcast; //设置目的端的以太网地址为广播地址
  arp.target_protoaddr = targetIP.getAddress(); //目的端IP地址

  //构造以太帧首部
  EthernetPacket ether = new EthernetPacket();
  ether.frametype = EthernetPacket.ETHERTYPE_ARP; //帧类型
  ether.src_mac = device.mac_address; //源MAC地址
  ether.dst_mac = broadcast; //以太网目的地址，广播地址
  arp.datalink = ether; //将arp报文的数据链路层的帧设置为刚刚构造的以太帧赋给

  sender.sendPacket(arp); //发送ARP报文
  int i = 0;
  while (true) { //获取ARP回复包，从中提取出目的主机的MAC地址，如果返回的是网关地址，表明目的IP不是局域网内的地址
    Packet packet = jpcap.getPacket();
    if(i++>50){
      JOptionPane.showMessageDialog(null,"ip不正确");
      return null;
    }
    if (packet instanceof ARPPacket) {
      ARPPacket p = (ARPPacket) packet;
      if (p == null) {
        throw new IllegalArgumentException(targetIP + " is not a local address"); //这种情况也属于目的主机不是本地地址
      }
      if (Arrays.equals(p.target_protoaddr, senderIP.getAddress())) {
        System.out.println("get mac ok");
        return p.sender_hardaddr; //返回
      }
    }
  }
}



}

