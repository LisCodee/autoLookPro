package project;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
 
public class PrintScreen {
	private int x_t,y_t,x_b,y_b;
	
	protected PrintScreen(int x_t, int y_t, int x_b, int y_b) {
		super();
		this.x_t = x_t;
		this.y_t = y_t;
		this.x_b = x_b;
		this.y_b = y_b;
	}
	public void jietu() {
		try {
			Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
			Rectangle screenRectangle = new Rectangle(dimension);
			Robot robot = new Robot();
			BufferedImage image = robot.createScreenCapture(screenRectangle);
			File file = new File("G:\\workspace\\autoLook\\src\\image\\image.jpg");
			// 指定屏幕区域，参数为截图左上角坐标(100,100)+右下角坐标(500,500)
			BufferedImage subimage = image.getSubimage(x_t, y_t, x_b, y_b);
			ImageIO.write(subimage, "jpg", file);
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		PrintScreen ps = new PrintScreen(734,66,450,132);
		ps.jietu();
	}
}
