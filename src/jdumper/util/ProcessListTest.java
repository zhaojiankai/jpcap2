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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
/**
 * 
 * @包名 ：com.minimax.listeren<br>
 * @文件名 ：ProcessListTest.java<br>
 * @类描述 ：判断系统进程是否存在<br>
 * @作者 ：Andy.wang<br>
 * @创建时间 ：2014-3-5上午11:25:26<br>
 * @更改人 ：<br>
 * @更改时间 ：<br>
 */
public class ProcessListTest {
 
    public static void main(String[] args) throws IOException {
        System.out.println(ProcessListTest.isRunning("QQ.exe"));
    }
 
    /**
     * 
     * @方法名 ：isRunning<br>
     * @方法描述 ：判断系统进程是否存在<br>
     * @创建者 ：Andy.wang<br>
     * @创建时间 ：2014-3-5上午11:25:46 <br>
     * @param exeName ：进程名
     * @return
     * 返回类型 ：boolean
     */
    public static IdentityHashMap<String,Integer> isRunning(String exeName) {
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
                System.out.println(proStr+"    "+pid);
                info = br.readLine();
                process.put(proStr, pid);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
        return process;
    }
 
}