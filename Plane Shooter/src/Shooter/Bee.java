package Shooter;

import java.awt.image.BufferedImage;
import java.util.Random;

public class Bee extends FlyingObject implements Award{

	private static BufferedImage[] images;

	static {
		images = new BufferedImage[5];
		images[0]=readImage("bee.png");
		for(int i=1;i<images.length;i++) {
			images[i]=readImage("bee_ember"+i+".png");
		}	
	}

	private int xStep;
	private int yStep;
	private int awardType;

	static int indicator=0;
	public Bee() {
		super(60,50);
		if(indicator%2==0) {
			xStep=2;
			indicator++;
		}else {
			xStep=-2;
			indicator++;
		}
		yStep=3;
		Random ran = new Random();
		awardType=ran.nextInt(2);
		//randomly generate 0 or 1 to grant award type
		//when the enemy plane is hit, one of two kinds of award is awarded
	}

	public void show() {
		super.show();
		System.out.println("xspeed:"+xStep+", yspeed:"+yStep);
		System.out.println("award type:"+awardType);
	}

	public void step() {
		x+=xStep;
		y+=yStep;
		//如果奖励机碰撞了左右两侧的边界
		if(x<=0||x>=World.WIDTH-this.width) {
			//修改它的移动放向
			xStep*=-1;
		}
	}

	int index=1;
	public BufferedImage getImage() {
		BufferedImage img=null;
		if(isLife()) {
			img=images[0];
		}else if(isDead()) {
			img=images[index];
			index++;
			if(index==images.length) {
				state=REMOVE;
			}
		}
		return img;
	}

	public int awardType() {
		//返回当前奖励机的奖励类型值
		return this.awardType;
	}

}