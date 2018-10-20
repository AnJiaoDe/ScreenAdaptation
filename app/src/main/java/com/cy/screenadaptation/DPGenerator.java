package com.cy.screenadaptation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DPGenerator {

    private static final String HEAD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";//头部
    private static final String START_TAG = "<resources>\n";//开始标签
    private static final String END_TAG = "</resources>\n";//结束标签


    private static final float DP_BASE = 360;//360dp为基准

    private static final int DP_MAX = 720;//所有dimens文件dp从0生成到这个值
    private static final int SP_MAX = 48;//SP最大

    private static final int[] dps = {360, 384, 392, 400, 410, 411, 480, 533, 592,
            600, 640, 662, 720, 768, 800, 811, 820, 960, 961, 1024, 1280};//所有dp列表

    private static ExecutorService fixedThreadPool;//线程池，用于生成XML文件
    private static int size_thread = 5;//线程池大小

    public static void main(String[] args) {
        fixedThreadPool = Executors.newFixedThreadPool(size_thread);

        for (int i = 0; i < dps.length; i++) {

            XMLThread xmlThread = new XMLThread(i);
            fixedThreadPool.execute(xmlThread);//线程启动执行

        }

    }

    private static class XMLThread implements Runnable {

        private int index = 0;

        public XMLThread(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            //文件件改成自己当前项目的路径
            generateXMl(index, "F:\\AndroidStudioWorkSpace\\ScreenAdaptation\\app\\src\\main\\res\\values-sw" + dps[index] + "dp\\", "dimens.xml");
        }
    }


    private static void generateXMl(int index, String pathDir, String fileName) {
        try {
            File diectoryFile = new File(pathDir);
            if (!diectoryFile.exists()) {
                diectoryFile.mkdirs();
            }
            File file = new File(pathDir + fileName);
            if (file.exists()) {
                file.delete();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(HEAD);
            fileWriter.write(START_TAG);

            //????????????????????????????????????????????????????????????????????????
            fileWriter.write("\t<!--" + "dp从-100到-0.5" + "-->\n");

            for (int i = 100; i > 0; i--) {
                if (i < 50) {

                    float ii = i + 0.5f;
                    String output2 = "\t<dimen name=\"_dp" + i + "_5" + "\">" + roundString(-ii,index) + "dp</dimen>\n";
                    fileWriter.write(output2);
                }
                String output = "\t<dimen name=\"_dp" + i + "\">" + roundString(-i,index) + "dp</dimen>\n";
                fileWriter.write(output);

            }
            //????????????????????????????????????????????????????????????????????????

            fileWriter.write("\t<!--" + "dp从0到dp_max" + "-->\n");

            for (int i = 0; i <= DP_MAX; i++) {
                String output = "\t<dimen name=\"dp" + i + "\">" + roundString(i,index) + "dp</dimen>\n";
                fileWriter.write(output);
                if (i >= 50) continue;
                float ii = i + 0.5f;
                String output2 = "\t<dimen name=\"dp" + i + "_5" + "\">" + roundString(ii,index) + "dp</dimen>\n";
                fileWriter.write(output2);
            }
            //????????????????????????????????????????????????????????????????????????

            fileWriter.write("\t<!--" + "//sp从0到sp_max" + "-->\n");

            for (int i = 0; i <= SP_MAX; i++) {
                String output = "\t<dimen name=\"sp" + i + "\">" + roundString(i,index) + "sp</dimen>\n";
                fileWriter.write(output);
            }


            fileWriter.write(END_TAG);
            fileWriter.flush();
            fileWriter.close();

            System.out.println("写入成功");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("写入失败");
        }
    }

    //精确到小数点后2位,并且四舍五入(因为有SW1280dp,基准是160dp，1dp=1px,
    // 如果精确到小数点后一位，四舍五入会有0.5dp误差，在sw1280dp中会有4PX误差，精确到小数点后2位，四舍五入，误差控制在1PX之内)
    private static String roundString(float data,int index) {
        String result = "";
        float floatResult = data * dps[index] / DP_BASE;
        DecimalFormat df = new DecimalFormat("0.00");
        result = df.format(floatResult);
        return result;
    }

}
