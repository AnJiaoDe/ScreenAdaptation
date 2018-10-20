package com.cy.screenadaptation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

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
