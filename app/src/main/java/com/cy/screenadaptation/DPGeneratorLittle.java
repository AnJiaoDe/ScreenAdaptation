package com.cy.screenadaptation;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DPGeneratorLittle {

    private static final String HEAD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";//头部
    private static final String START_TAG = "<resources>\n";//开始标签
    private static final String END_TAG = "</resources>\n";//结束标签


    private static final float DP_BASE = 360;//360dp为基准

    private static final int DP_MAX = 720;//所有dimens文件dp从0生成到这个值
    private static final int SP_MAX = 48;//SP最大

    private static final int[] dps = {360, 384, 392, 400, 410, 411, 480, 533, 592,
            600, 640, 662, 720, 768, 800, 811, 820,900, 960, 961, 1024, 1280};//常见dp列表
//    private static final int[] dps = {100,481,510,720,900};//常见dp列表

    private static final String root="F:\\AndroidStudioWorkSpace\\ScreenAdaptation\\app\\src\\main\\res\\";

    private static ExecutorService fixedThreadPool;//线程池，用于生成XML文件
    private static int size_thread = 5;//线程池大小


    private static DocumentBuilderFactory dbFactory;
    private static DocumentBuilder db;
    private static Document document;


    public static void main(String[] args) {
        try {
            dbFactory = DocumentBuilderFactory.newInstance();
            db = dbFactory.newDocumentBuilder();
            //将给定 URI 的内容解析为一个 XML 文档,并返回Document对象
            //记得改成自己当前项目的路径
            document = db.parse(root+"values\\dimens.xml");


            //按文档顺序返回包含在文档中且具有给定标记名称的所有 Element 的 NodeList
            NodeList dimenList = document.getElementsByTagName("dimen");
            if (dimenList.getLength()==0)return;
            List<Dimen> list = new ArrayList<>();
            for (int i = 0; i < dimenList.getLength(); i++) {
                //获取第i个book结点
                Node node = dimenList.item(i);
                //获取第i个dimen的所有属性
                NamedNodeMap namedNodeMap = node.getAttributes();
                //获取已知名为name的属性值
                String atrName = namedNodeMap.getNamedItem("name").getTextContent();

                String value = node.getTextContent();

                System.out.println("+++atrName++++++++++++++++++++" + atrName);
                System.out.println("+++++++++++++value++++++++++" + value);


                list.add(new Dimen(atrName, value));


            }


            fixedThreadPool = Executors.newFixedThreadPool(size_thread);

            for (int i = 0; i < dps.length; i++) {

                XMLThread xmlThread = new XMLThread(i, list);
                fixedThreadPool.execute(xmlThread);//线程启动执行

            }


        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }


    }

    private static class XMLThread implements Runnable {

        private int index = 0;
        private List<Dimen> list;

        public XMLThread(int index, List<Dimen> list) {
            this.index = index;
            this.list = list;
        }

        @Override
        public void run() {
            //记得改成自己当前项目的路径
            generateXMl(list, index, root+"values-sw" + dps[index] + "dp\\", "dimens.xml");
        }
    }


    private static void generateXMl(List<Dimen> list, int index, String pathDir, String fileName) {
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


            //?????????????????????????????????????????????
            int size = list.size();
            String atrName;
            String value;
            for (int i = 0; i < size; i++) {
                atrName = list.get(i).getAtrName();
                value = list.get(i).getValue();

                String output = "\t<dimen name=\"" + atrName + "\">" +
                        roundString(Float.valueOf(value.substring(0, value.length() - 2)), index) +
                        value.substring(value.length()-2)+"</dimen>\n";
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
    private static String roundString(float data, int index) {
        String result = "";
        float floatResult = data * dps[index] / DP_BASE;
        DecimalFormat df = new DecimalFormat("0.00");
        result = df.format(floatResult);
        return result;
    }

    private static class Dimen {
        private String atrName;
        private String value;

        public Dimen(String atrName, String value) {
            this.atrName = atrName;
            this.value = value;
        }

        public String getAtrName() {
            return atrName;
        }

        public void setAtrName(String atrName) {
            this.atrName = atrName;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
