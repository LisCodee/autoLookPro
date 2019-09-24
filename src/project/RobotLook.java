package project;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class RobotLook {
	private int count = 0;
	//第一节课列表的坐标和翻页坐标以及每一节课距离
	private int task_x = 1748,task_y = 173,next_page_x = 1915,next_page_y=1021
			,distance = 25;
	//任务完成左上坐标和长宽
	private int complete_x = 456,complete_y = 355, complete_width = 30, 
			complete_height = 30;
	private int video_x = 773, video_y = 285, center_x = 812,center_y = 655;
	//问题窗口左上坐标和长宽
	private int question_x = 461,question_y = 388,
			question_width = 300,question_height = 80;
	private int a_x = 496,a_y = 449;	//选项A坐标
	private int b_x = 496,b_y = 482;	//选项B坐标
	private int ok_x = 526,ok_y = 528;	//提交按钮坐标
	private int wrong_x = 734,wrong_y = 66,wrong_width = 450,
			wrong_height = 132,wrong_ok_x = 1135,wrong_ok_y = 168;
	private String root_path = "G:\\workspace\\autoLook\\src\\image\\";
	private String question_path = root_path + "出现问题.jpg",
			wrong_path = root_path + "回答错误.jpg",
			complete_path = root_path + "完成任务.jpg",
			img_path = root_path + "image.jpg";
	//图像比较
	public int[] getData(String name) {
//		System.out.println(name);
        try {
            BufferedImage img = ImageIO.read(new File(name));
            BufferedImage slt = new BufferedImage(100, 100,
                    BufferedImage.TYPE_INT_RGB);
            slt.getGraphics().drawImage(img, 0, 0, 100, 100, null);
            // ImageIO.write(slt,"jpeg",new File("slt.jpg"));
            int[] data = new int[256];
            for (int x = 0; x < slt.getWidth(); x++) {
                for (int y = 0; y < slt.getHeight(); y++) {
                    int rgb = slt.getRGB(x, y);
                    Color myColor = new Color(rgb);
                    int r = myColor.getRed();
                    int g = myColor.getGreen();
                    int b = myColor.getBlue();
                    data[(r + g + b) / 3]++;
                }
            }
            // data 就是所谓图形学当中的直方图的概念
            return data;
        } catch (Exception exception) {
            System.out.println("有文件没有找到,请检查文件是否存在或路径是否正确");
            return null;
        }
    }

    public float compareImage(int[] s, int[] t) {
        try {
            float result = 0F;
            for (int i = 0; i < 256; i++) {
                int abs = Math.abs(s[i] - t[i]);
                int max = Math.max(s[i], t[i]);
                result += (1 - ((float) abs / (max == 0 ? 1 : max)));
            }
            return (result / 256) * 100;
        } catch (Exception exception) {
            return 0;
        }
    }
	//鼠标点击方法
	public void autoClick(int x, int y) {
		try {
			Robot robot = new Robot();
			robot.setAutoWaitForIdle(true);	
			for(int i = 0;i<5;i++) {		//校准鼠标焦点
				robot.mouseMove(x, y);
			}
			robot.mousePress(InputEvent.BUTTON1_MASK);
//			System.out.println("按下左键");
			robot.delay(100);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
//			System.out.println("松开左键");
			Thread.sleep(1000);			//线程延迟1s
		} catch (AWTException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	//截图判断是否出现了问题
	public void response() {
		PrintScreen img = new PrintScreen(question_x,question_y,question_width,
				question_height);
		img.jietu();
		float similarity = compareImage(getData(img_path), getData(question_path));
		if(similarity > 31) {
			//相似度大于31说明出现了问题
			System.out.println("与出现问题的相似度" + similarity);
			autoClick(a_x,a_y);
			autoClick(ok_x,ok_y);
			System.out.println("选了A");
		}
	}
	//判断是否回答错误
	public void wrong() {
		PrintScreen img = new PrintScreen(wrong_x,wrong_y,wrong_width,
				wrong_height);
		img.jietu();
		float similarity = compareImage(getData(img_path), getData(wrong_path));
		if(similarity > 80) {
			//相似度大于45说明出现了问题
			System.out.println("与回答错误的相似度" + similarity);
			autoClick(wrong_ok_x,wrong_ok_y);
			autoClick(b_x,b_y);
			autoClick(ok_x,ok_y);
			System.out.println("选了B");
		}
	}
	//开始听课
	public void start_look() {
		try {
			Thread.sleep(5000);
			autoClick(video_x,video_y);
			autoClick(center_x,center_y);
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	//切换到下一节课
	public void nextCourse() {
		task_y += distance; 
		if(task_y < 1035) {
			autoClick(task_x,task_y);
			start_look();
		}
		else {
			task_y = 173;	//初始化y坐标
			for(int i = 0;i < 21;i++)	//翻页
				autoClick(next_page_x,next_page_y);
			autoClick(task_x,task_y);
			start_look();
		}
	}
	//判断是否完成任务
	public void completed() {
		PrintScreen img = new PrintScreen(complete_x,complete_y,complete_width,
				complete_height);
		img.jietu();
		float similarity = compareImage(getData(img_path), getData(complete_path));
		System.out.println("与完成任务的相似度" + similarity);
		if(similarity > 59) {
			//相似度大于90说明出现了问题
			System.out.println("与完成任务的相似度" + similarity);
			nextCourse();
			count++;
			System.out.println("下一课，共计刷课" + count + "节");
		}
	}
	public void start() {
		while(true) {
			try {
				Thread.sleep(2000);
				response();
				Thread.sleep(1000);
				wrong();
				Thread.sleep(1000);
				completed();
				Thread.sleep(3000);
			}
			catch (Exception e) {
				System.out.println(e);// TODO: handle exception
			}
		}
	}
	public static void main(String[] args) {
		RobotLook robot = new RobotLook();
		robot.start();
	}
}
