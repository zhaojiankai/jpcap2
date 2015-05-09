/* ==================================================================
 * 恒生电子股份有限公司拥有该文件的使用、复制、修改和分发的许可权
 * 如果你想得到更多信息，请访问 <http://www.hundsun.com/>
 *
 * Hundsun Technologies Inc. owns permission to use, copy, modify and
 * distribute this documentation.
 * For more information, please see <http://www.hundsun.com/>.
 * ==================================================================
 */

package jdumper.util;

/**
 * <p>
 * .
 * </p>
 *
 * @author zhaojiankai
 * @version 1.0
 */
import java.net.InetAddress;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import jpcap.packet.ICMPPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
class TCP
{
    public static void main(String[] args) throws java.io.IOException{
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        if(args.length<1){
            System.out.println("Usage: java tcp ");
            //            for(int i=0;i<devices.length;i++) 
            for(int i=0;i<devices.length;i++) 
              System.out.println(i+":"+devices[i].name+"("+devices[i].description+")");
            //System.exit(0);
        }
       // int index=Integer.parseInt(args[0]);
        int index=0;
// 开启网络设备
        JpcapCaptor captor=JpcapCaptor.openDevice(devices[index],2000,false,3000);
// 设置只过滤  icmp 包
        captor.setFilter("tcp",true);
        JpcapSender sender=captor.getJpcapSenderInstance();
        
        TCPPacket packet = new TCPPacket(5854, 25214, (long)25884, (long)0, false, false, false, false, true, false, true, true, 1000, 0);
        packet.setIPv4Parameter(0,false,false,false,0,false,false,false,0,1010101,100,IPPacket.IPPROTO_TCP, InetAddress.getByName("192.168.1.108"), InetAddress.getByName("192.168.1.102"));
        packet.data = "".getBytes();
        
        byte[] broadcast = new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255,(byte) 255 }; //广播地址
        //构造以太帧首部
        EthernetPacket ether = new EthernetPacket();
        ether.frametype = EthernetPacket.ETHERTYPE_IP; //帧类型
        ether.src_mac = devices[index].mac_address; //源MAC地址
        ether.dst_mac = broadcast; //以太网目的地址，request包为广播地址,reply包为目的地址
        packet.datalink = ether; //将arp报文的数据链路层的帧设置为刚刚构造的以太帧赋给
        
        sender.sendPacket(packet);
        System.out.println("send...");
        Packet rp= null;
        while(true){
          rp=captor.getPacket();
          if (rp instanceof TCPPacket) {
            TCPPacket tcp=(TCPPacket)rp;
            if(tcp==null){
           
                throw new IllegalArgumentException("no rcv icmp echo reply");
              
            }else
            {
              System.out.println(tcp.toString());
                System.out.println("rcv icmp echo reply");
               // return ;
            }
          }
        }
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
}

