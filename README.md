

[APKdemo](https://github.com/AnJiaoDe/DPScreenAdaptation/blob/master/app/build/outputs/apk/app-debug.apk)

[toc]

## 小编尝试过2种屏幕适配方法： 

## 1.PX适配

使用PXGenerator代码生成各种分辨率的文件夹以及文件，
以某分辨率比如480x800为基准，1px=1px,
按比例生成其他各种分辨率的dimen文件，会有1px=4px,1px=3px之类的情况，
以次达到屏幕适配的目的

>![20181020192203851.png](https://upload-images.jianshu.io/upload_images/11866078-01bedf27b963d0dc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

```
<dimen name="py11">14.7px</dimen>
	<dimen name="py12">16.0px</dimen>
	<dimen name="py13">17.3px</dimen>
	<dimen name="py14">18.7px</dimen>
	<dimen name="py15">20.0px</dimen>
	<dimen name="py16">21.3px</dimen>
	<dimen name="py17">22.7px</dimen>
	<dimen name="py18">24.0px</dimen>
	<dimen name="py19">25.3px</dimen>
	<dimen name="py20">26.7px</dimen>
	<dimen name="py21">28.0px</dimen>
	<dimen name="py22">29.3px</dimen>
	<dimen name="py23">30.7px</dimen>
	<dimen name="py24">32.0px</dimen>
	<dimen name="py25">33.3px</dimen>
	<dimen name="py26">34.7px</dimen>
	<dimen name="py27">36.0px</dimen>
	<dimen name="py28">37.3px</dimen>
	<dimen name="py29">38.7px</dimen>
```

```
public class PXGenerator {
	
	private static final String HEAD="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";//头部
	private static final String START_TAG="<resources>\n";//开始标签
	private static final String END_TAG="</resources>\n";//结束标签
	
	private static final String ROOT="F:\\AndroidStudioWorkSpace\\ScreenAdaptation\\app\\src\\main\\res\\values-2560x1800\\";//文件夹
	private static final String FILE_NAME="dimen_py2560.xml";//文件名
	
	private static final String path=ROOT+FILE_NAME;//文件路径
	
	private static final float TIMES=1920*1.0f/2560;
	
	private static final int DIMENSION=1920;//以次为基准
	
	
	public static void main(String[] args) {
		generateXMl();
	}
	
	private static void generateXMl()
	{
		try 
		{
			File diectoryFile = new File(ROOT);
			if(!diectoryFile.exists()){
				diectoryFile.mkdirs();
			}
			File file = new File(path);
			if(file.exists()){
				file.delete();
			}
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(HEAD);
			fileWriter.write(START_TAG);
			for(int i=0;i<=DIMENSION;i++){
				String output="\t<dimen name=\"py"+i+"\">"+roundString(i)+"px</dimen>\n";
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
	
	private static String roundString(int data)
	{
		String result="";
		float floatResult=data/TIMES;
		DecimalFormat df = new DecimalFormat("0.00");
		result = df.format(floatResult);
		return result;
	}
	
}

```
**缺点：需要生成的相关分辨率的dimen实在太多，增大APK包体积**

## 2.DP适配由来
[参考CSDN阿杜，写得贼好，帮助贼大，贼感谢](https://blog.csdn.net/fesdgasdgasdg)

**先熟知如下图所示的各种参数**
![1540035378(1).png](https://upload-images.jianshu.io/upload_images/11866078-31fd9176061e2b1a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![1540043028(1).png](https://upload-images.jianshu.io/upload_images/11866078-10a08d2a2aad207f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![1540043059(1).png](https://upload-images.jianshu.io/upload_images/11866078-407ab5f1709cedb1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

```
 private String getScreenParams() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int heightPixels = dm.heightPixels;//高的像素
        int widthPixels = dm.widthPixels;//宽的像素
        int densityDpi = dm.densityDpi;//dpi
        float xdpi = dm.xdpi;//xdpi
        float ydpi = dm.ydpi;//ydpi
        float density = dm.density;//density=dpi/160,密度比
        float scaledDensity = dm.scaledDensity;//scaledDensity=dpi/160 字体缩放密度比
        float heightDP = heightPixels / density;//高度的dp
        float widthDP = widthPixels / density;//宽度的dp
        String str = "heightPixels: " + heightPixels + "px";
        str += "\nwidthPixels: " + widthPixels + "px";
        str += "\ndensityDpi: " + densityDpi + "dpi";
        str += "\nxdpi: " + xdpi + "dpi";
        str += "\nydpi: " + ydpi + "dpi";
        str += "\ndensity: " + density;
        str += "\nscaledDensity: " + scaledDensity;
        str += "\nheightDP: " + heightDP + "dp";
        str += "\nwidthDP: " + widthDP + "dp";

        return str;
    }
```

**1.px：**
>屏幕分辨率：在橫纵向上的像素点数。单位：px即1px=1个像素点。
 一般以纵向像素x横向像素表示，如1920x1080
 
**2.dpi:**
>屏幕像素密度，指每英寸上的像素点数，dot per inch的缩写，与屏幕尺寸和屏幕分辨率有关。以Nexus5为例，官方参数为1920*1080,dpi=445，4.95 inch  那么，这个445的dpi是怎么算出来的呢？由上面介绍可知，屏幕尺寸4.95是屏幕对角线的长度，而dpi是指每英寸上的像素点数，所以应该由对角线所占的像素值除以4.95，如下：
![20161111133928845.png](https://upload-images.jianshu.io/upload_images/11866078-b0214b6fa3421fb1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


**3.dp/dip:** 
>dp和dip是一样的，密度无关像素，Density Independent Pixels的缩写，以160dpi为基准。在160dpi设备  上1dp=1px，在240dpi设备上1dp=1.5px,以此类推

**4.density:**
 density=dpi/160,密度比
 
**5.scaledDensity :**
 scaledDensity=dpi/160 字体缩放密度比
 
**6.heightDP：**
 heightDP = heightPixels / density;//高度的dp
 
 **7.widthDP：**
  heightDP = widthPixels / density;//宽度的dp

[参考android官网，官网告诉我们如何去适配](https://developer.android.google.cn/guide/practices/screens_support)
![1540034794(1).png](https://upload-images.jianshu.io/upload_images/11866078-6229756ffec98286.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![1540034888(1).png](https://upload-images.jianshu.io/upload_images/11866078-8b37ff391c73c6bc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


**先看上图sw< N >dp:**
smallestWidth,最小宽度，
![1540036835(1).png](https://upload-images.jianshu.io/upload_images/11866078-a4222e700218e2d7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


**官方提供了多种尺寸限制符，sw指的是最小宽度，和竖屏横屏无关:**

如800DPx480DP(最小宽度是480dp，比如部分800x480手机),
592DPx360DP(最小宽度是360dp，比如部分1920x1080手机),
604DPx360DP(最小宽度是360dp,比如部分1920x1080手机),
680DPx360DP(最小宽度是360dp.比如部分2160x1080手机（2018年左右开始流行长屏幕手机、刘海屏手机）),
1232DPx900DP(最小宽度是900dp，比如部分2560x1800手机（平板）)

这里发现多种分辨率手机最小宽度都是360DP，尤其是1920x1080,是2018年左右最流行的屏幕分辨率，因此考虑以宽度360dp为基准做适配（这也是为何使用DP适配，而不使用PX适配的原因，因为DP适配需要的dimens文件会少很多）

**sw< N >dp会向上兼容:**

比如valuse-sw481dp文件夹，当且仅当手机最小宽度dp>=481dp才会去该目录寻找数值，800DPx480DP最小宽度480dp,是无法进入valuse-sw481dp寻找数值的，如果values目录下没有对应的数值，只有一个结果GG=APP崩溃。**所以任何时候都记得一定要在values目录下创建对应的数值**
![20181020225528473.png](https://upload-images.jianshu.io/upload_images/11866078-5f823c02ab9cca68.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


800DPx480DP手机，由于没有valuse-sw480dp文件夹，只能向下寻找，找到valuse-sw100dp文件夹，结果界面当然很丑，（适配具体操作 容后再谈）

![1540048214(1).png](https://upload-images.jianshu.io/upload_images/11866078-ed2cdb08651e31a7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

上文说到：多种分辨率手机最小宽度都是360DP，尤其是1920x1080,是2018年左右最流行的屏幕分辨率，因此考虑以宽度360dp为基准做适配



## **3.DP适配原理以及具体操作**



常见的swdp如下：

```
    //    private static final int[] dps = {360, 384, 392, 400, 410, 411, 480, 533, 592,
    //            600, 640, 662, 720, 768, 800, 811, 820,900, 960, 961, 1024, 1280};//常见dp列表
```
为了适配更多手机，dp应尽可能包含更多，而且连续性强，间隔小

**dp适配原理：**
和px适配原理类似（按比例计算），以sw360dp为基准，

```
 <dimen name="height_title">48dp</dimen>
    <dimen name="sp18">18sp</dimen>
    <dimen name="padding_icon">12dp</dimen>
    <dimen name="dp360">360dp</dimen>
    <dimen name="sp10">10sp</dimen>
    <dimen name="dp30">30dp</dimen>
    <dimen name="dp10">10dp</dimen>
    <dimen name="sp14">14sp</dimen>
    <dimen name="dp40">40dp</dimen>
    <dimen name="dp70">70dp</dimen>
    <dimen name="dp100">100dp</dimen>
    <dimen name="dp80">80dp</dimen>
    <dimen name="sp12">12sp</dimen>
    <dimen name="dp60">60dp</dimen>
    <dimen name="_dp10">-10dp</dimen>
    <dimen name="dp4_5">4.5dp</dimen>
    <dimen name="_dp5_5">-5.5dp</dimen>
```
那么sw720dp如下：
全是2倍值
```
<dimen name="height_title">96dp</dimen>
    <dimen name="sp18">36sp</dimen>
    <dimen name="padding_icon">24dp</dimen>
    <dimen name="dp360">720dp</dimen>
    <dimen name="sp10">20sp</dimen>
    <dimen name="dp30">60dp</dimen>
    <dimen name="dp10">20dp</dimen>
    <dimen name="sp14">28sp</dimen>
    <dimen name="dp40">80dp</dimen>
    <dimen name="dp70">140dp</dimen>
    <dimen name="dp100">200dp</dimen>
    <dimen name="dp80">160dp</dimen>
    <dimen name="sp12">24sp</dimen>
    <dimen name="dp60">120dp</dimen>
    <dimen name="_dp10">-20dp</dimen>
    <dimen name="dp4_5">9dp</dimen>
    <dimen name="_dp5_5">-11dp</dimen>
```
**具体操作：**

**1）**
以sw360dp为基准，按照1920x1080的UI设计图，可以让设计师用“标你妹”等软件标注dp和sp,也可以上传到蓝湖，蓝湖上的标注更爽，在values下的dimens文件中定义dp值对应的dimen标签。
比如，UI图上标注的一个按钮高度是40dp,那么在dimens文件中创建

```
<dimen name="height_btn_">40dp</dimen>
```
name名字可以随便取

**2）**
将如下代码类放到当前android项目的app module下，记得更改root目录为自己的android项目的目录，
![1540050491(1).png](https://upload-images.jianshu.io/upload_images/11866078-0bbf6dd9b4586ed7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


```
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

    private static final String root="F:\\AndroidStudioWorkSpace\\ScreenAdaptation\\app\\src\\main\\res\\";//生成文件的主目录

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

```



如图执行工具类的main方法，即可生成各种dimens文件

![生成xml.gif](https://upload-images.jianshu.io/upload_images/11866078-9c6c29b0da6993ec.gif?imageMogr2/auto-orient/strip)


 **3）使用的相关原理及技巧**
 
1.工具类解析values目录下的dimens文件，根据其中所有的dimen标签，name和values,生成其他各种dp对应的dimens文件

2.DP_BASE可修改为自己定的基准（你可以根据实际情况定为其他dp,一般情况下，定义为360dp,因为大部分手机sw都是360dp）

3.root对应的是当前android项目的主目录路径，千万不能出错

4.size_thread,线程池大小默认5，你可以设置其他数值

5.你还可以根据自己强迫症的各种习惯和喜好，各种更改成自己喜欢的风格，代码很精简，修改很容易

6.在每次打包（如果需要调试sw360dp之外的手机，或者上线APP）之前，记得执行DPGeneratorLittle的main方法，以确保所有的dimen文件都是最新的

7.负数的dp，小数的dp，甚至小数的sp(一般没这情况)都可以生成，放心大胆使用

8.如果发现某台手机适配效果不是很好，那么真机调试，得到其swdp,然后在用DPGeneratorLittle生成对应的swdp，dimen文件

9.执行DPGeneratorLittle报错的时候，记得删除所有生成的valuse目录，clean项目，重新执行

## 4.欣赏DP适配案例
800PXx480PX的手机

![1540043059(1).png](https://upload-images.jianshu.io/upload_images/11866078-62809470148ec93c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

1920PXx1080PX的手机
![1540035378(1).png](https://upload-images.jianshu.io/upload_images/11866078-246587bcd27e79ad.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

2560PXx1800PX的手机
![1540043028(1).png](https://upload-images.jianshu.io/upload_images/11866078-731c4947411ac514.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


**可以看到适配效果很好，几乎没有无法接受的地方
代码如下：**

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/activity_main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="@dimen/height_title"
        android:background="#1d953f"
        android:gravity="center_vertical">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"

            android:layout_toRightOf="@+id/iv_back"
            android:gravity="center_vertical"

            android:text="屏幕适配"
            android:textColor="#ffffff"
            android:textSize="@dimen/sp18" />

        <ImageView
            android:id="@+id/iv_back"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:layout_centerVertical="true"

            android:padding="@dimen/padding_icon"
            android:scaleType="centerInside"
            android:src="@drawable/back" />


    </RelativeLayout>

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="@dimen/dp360">

        <ImageView
            android:id="@+id/imageView"
            android:layout_width="@dimen/dp360"
            android:layout_height="@dimen/dp360"
            android:layout_gravity="center_horizontal"
            android:scaleType="centerCrop"
            android:src="@drawable/rec" />

        <TextView
            android:id="@+id/tv"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:gravity="center"
            android:textColor="#fff"
            android:textSize="@dimen/sp18" />
    </FrameLayout>


    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="horizontal">

        <View
            android:layout_width="@dimen/dp40"
            android:layout_height="@dimen/dp40"
            android:layout_marginLeft="@dimen/_dp5_5"
            android:background="#436876" />

        <com.cy.screenadaptation.ViewCanMeasure
            android:id="@+id/view_bottom"
            android:layout_width="@dimen/dp60"
            android:layout_height="@dimen/dp60"
            android:background="#00f"
            android:gravity="center"
            android:textColor="#fff"
            android:textSize="@dimen/sp12" />

        <View
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:layout_weight="1" />


        <View
            android:layout_width="@dimen/dp4_5"
            android:layout_height="@dimen/dp40"
            android:layout_marginRight="@dimen/dp10"
            android:background="#00f" />

        <View
            android:layout_width="@dimen/dp40"
            android:layout_height="@dimen/dp40"
            android:layout_marginRight="@dimen/_dp10"
            android:background="#f00" />
    </LinearLayout>

</LinearLayout>

```

```
public class MainActivity extends AppCompatActivity {


    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);


        tv.setText(getScreenParams());

    }

    private String getScreenParams() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int heightPixels = dm.heightPixels;//高的像素
        int widthPixels = dm.widthPixels;//宽的像素
        int densityDpi = dm.densityDpi;//dpi
        float xdpi = dm.xdpi;//xdpi
        float ydpi = dm.ydpi;//ydpi
        float density = dm.density;//density=dpi/160,密度比
        float scaledDensity = dm.scaledDensity;//scaledDensity=dpi/160 字体缩放密度比
        float heightDP = heightPixels / density;//高度的dp
        float widthDP = widthPixels / density;//宽度的dp
        String str = "heightPixels: " + heightPixels + "px";
        str += "\nwidthPixels: " + widthPixels + "px";
        str += "\ndensityDpi: " + densityDpi + "dpi";
        str += "\nxdpi: " + xdpi + "dpi";
        str += "\nydpi: " + ydpi + "dpi";
        str += "\ndensity: " + density;
        str += "\nscaledDensity: " + scaledDensity;
        str += "\nheightDP: " + heightDP + "dp";
        str += "\nwidthDP: " + widthDP + "dp";

        return str;
    }
}

```
**dp适配优点：**dimens文件很少，不影响apk包的体积，
缺点：在每次打包（如果需要调试sw360dp之外的手机，或者上线APP）之前，都需要执行DPGeneratorLittle的main方法，以确保所有的dimen文件都是最新的，有点烦躁

想不烦躁可以，你可以用[github_DPScreenAdaptation](https://github.com/AnJiaoDe/DPScreenAdaptation)的DPGenerator类
直接生成所有可能用到的dp,sp，但是这样，可能会增加好几百KB文件

## 各位老铁有问题欢迎及时联系、指正、批评、撕逼

[GitHub](https://github.com/AnJiaoDe)

关注专题[Android开发常用开源库](https://www.jianshu.com/c/3ff4b3951dc5)

