/* ==================================================================
 * 恒生电子股份有限公司拥有该文件的使用、复制、修改和分发的许可权
 * 如果你想得到更多信息，请访问 <http://www.hundsun.com/>
 *
 * Hundsun Technologies Inc. owns permission to use, copy, modify and
 * distribute this documentation.
 * For more information, please see <http://www.hundsun.com/>.
 * ==================================================================
 */

package test;

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
class ICMP
{
    public static void main(String[] args) throws java.io.IOException{
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        if(args.length<1){
            System.out.println("Usage: java ICMP ");
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
        captor.setFilter("icmp",true);
        JpcapSender sender=captor.getJpcapSenderInstance();
        
        ICMPPacket p=new ICMPPacket();
        p.type=ICMPPacket.ICMP_ECHO;
        p.seq=(short)0x0005;
        p.id=(short)0x0006;
        
        p.setIPv4Parameter(0,false,false,false,0,false,false,false,0,1010101,100,IPPacket.IPPROTO_ICMP,
            InetAddress.getByName("192.168.1.108"),InetAddress.getByName("192.168.1.101"));
        p.data="abcdefghijklmnopqrstuvwabcdehghi".getBytes();
        EthernetPacket ether=new EthernetPacket();
        ether.frametype=EthernetPacket.ETHERTYPE_IP;
// 填写自己和对方的 mac 地址，必须要正确填写，如果有错误将无法收到回包
//        ether.dst_mac=new byte[]{(byte)0x00,(byte)0x03,(byte)0x2d,(byte)0x02,(byte)0xd1,(byte)0x69};
//        ether.src_mac=new byte[]{(byte)0x08,(byte)0x00,(byte)0x46,(byte)0xad,(byte)0x3c,(byte)0x12};
        ether.dst_mac=stomac("bc-d1-77-d0-56-a4");
        ether.src_mac=stomac("e0-b9-a5-1a-4f-94");
        p.datalink=ether;
        
        sender.sendPacket(p);
        System.out.println("send...");
        Packet rp= null;
        while(true){
          rp=captor.getPacket();
          if (rp instanceof ICMPPacket) {
            ICMPPacket icmpp=(ICMPPacket)rp;
            if(icmpp==null){
                throw new IllegalArgumentException("no rcv icmp echo reply");
            }else
            {
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

