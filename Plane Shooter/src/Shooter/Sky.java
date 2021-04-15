package Shooter;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Sky extends FlyingObject{

	private static BufferedImage image;
	static {
		image = readImage("background.png");
	}

//	private int step;
//	int y1;

	public Sky() {
		super(480,852,0,0);
//		y1=-700;
//		step=1;
	}

	public void show() {
		super.show();
//		System.out.println("speed:"+step);
//		System.out.println("y1:"+y1);
	}

	public void step() {
//		//两张图都要移动
//		y+=step;
//		y1+=step;
//		//当任何一张图片移动出窗体时，
//		//都要重置到窗体上方
//		if(y>=World.HEIGHT) {
//			y=-World.HEIGHT;
//		}
//		if(y1>=World.HEIGHT) {
//			y1=-World.HEIGHT;
//		}
	}

	public BufferedImage getImage() {
		return image;
	}

	public void paintObject(Graphics g) {
		g.drawImage(getImage(),x,y,null);
//		g.drawImage(getImage(),x,y1,null);

	}

}
