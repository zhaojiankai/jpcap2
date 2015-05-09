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
import java.io.*;
import java.util.*;

public class WindowsUtils {
  public static  ArrayList<String> getProByPid(int pid) {
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
    if(proNum==0)return null;
    return pro;
}

  public static void main(String[] args){
    ArrayList<String>processes = getProByPid(3620444);
      String result = "";

      // display the result 
      int i = 0;
     // for (String s : processes) {
  //      System.out.println(s);
//         result += s +",";
//         i++;
//         if (i==10) {
//             result += "\n";
//             i = 0;
//         }
      }
      //msgBox("Running processes : " + result);
//  }

  public static void msgBox(String msg) {
    javax.swing.JOptionPane.showConfirmDialog((java.awt.Component) null, msg, "WindowsUtils", javax.swing.JOptionPane.DEFAULT_OPTION);
  }
}
