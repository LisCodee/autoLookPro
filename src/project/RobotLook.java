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
	//��һ�ڿ��б������ͷ�ҳ�����Լ�ÿһ�ڿξ���
	private int task_x = 1748,task_y = 173,next_page_x = 1915,next_page_y=1021
			,distance = 25;
	//���������������ͳ���
	private int complete_x = 456,complete_y = 355, complete_width = 30, 
			complete_height = 30;
	private int video_x = 773, video_y = 285, center_x = 812,center_y = 655;
	//���ⴰ����������ͳ���
	private int question_x = 461,question_y = 388,
			question_width = 300,question_height = 80;
	private int a_x = 496,a_y = 449;	//ѡ��A����
	private int b_x = 496,b_y = 482;	//ѡ��B����
	private int ok_x = 526,ok_y = 528;	//�ύ��ť����
	private int wrong_x = 734,wrong_y = 66,wrong_width = 450,
			wrong_height = 132,wrong_ok_x = 1135,wrong_ok_y = 168;
	private String root_path = "G:\\workspace\\autoLook\\src\\image\\";
	private String question_path = root_path + "��������.jpg",
			wrong_path = root_path + "�ش����.jpg",
			complete_path = root_path + "�������.jpg",
			img_path = root_path + "image.jpg";
	//ͼ��Ƚ�
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
            // data ������νͼ��ѧ���е�ֱ��ͼ�ĸ���
            return data;
        } catch (Exception exception) {
            System.out.println("���ļ�û���ҵ�,�����ļ��Ƿ���ڻ�·���Ƿ���ȷ");
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
	//���������
	public void autoClick(int x, int y) {
		try {
			Robot robot = new Robot();
			robot.setAutoWaitForIdle(true);	
			for(int i = 0;i<5;i++) {		//У׼��꽹��
				robot.mouseMove(x, y);
			}
			robot.mousePress(InputEvent.BUTTON1_MASK);
//			System.out.println("�������");
			robot.delay(100);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
//			System.out.println("�ɿ����");
			Thread.sleep(1000);			//�߳��ӳ�1s
		} catch (AWTException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	//��ͼ�ж��Ƿ����������
	public void response() {
		PrintScreen img = new PrintScreen(question_x,question_y,question_width,
				question_height);
		img.jietu();
		float similarity = compareImage(getData(img_path), getData(question_path));
		if(similarity > 31) {
			//���ƶȴ���31˵������������
			System.out.println("�������������ƶ�" + similarity);
			autoClick(a_x,a_y);
			autoClick(ok_x,ok_y);
			System.out.println("ѡ��A");
		}
	}
	//�ж��Ƿ�ش����
	public void wrong() {
		PrintScreen img = new PrintScreen(wrong_x,wrong_y,wrong_width,
				wrong_height);
		img.jietu();
		float similarity = compareImage(getData(img_path), getData(wrong_path));
		if(similarity > 80) {
			//���ƶȴ���45˵������������
			System.out.println("��ش��������ƶ�" + similarity);
			autoClick(wrong_ok_x,wrong_ok_y);
			autoClick(b_x,b_y);
			autoClick(ok_x,ok_y);
			System.out.println("ѡ��B");
		}
	}
	//��ʼ����
	public void start_look() {
		try {
			Thread.sleep(5000);
			autoClick(video_x,video_y);
			autoClick(center_x,center_y);
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	//�л�����һ�ڿ�
	public void nextCourse() {
		task_y += distance; 
		if(task_y < 1035) {
			autoClick(task_x,task_y);
			start_look();
		}
		else {
			task_y = 173;	//��ʼ��y����
			for(int i = 0;i < 21;i++)	//��ҳ
				autoClick(next_page_x,next_page_y);
			autoClick(task_x,task_y);
			start_look();
		}
	}
	//�ж��Ƿ��������
	public void completed() {
		PrintScreen img = new PrintScreen(complete_x,complete_y,complete_width,
				complete_height);
		img.jietu();
		float similarity = compareImage(getData(img_path), getData(complete_path));
		System.out.println("�������������ƶ�" + similarity);
		if(similarity > 59) {
			//���ƶȴ���90˵������������
			System.out.println("�������������ƶ�" + similarity);
			nextCourse();
			count++;
			System.out.println("��һ�Σ�����ˢ��" + count + "��");
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
