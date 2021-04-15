package Shooter;

import java.awt.image.BufferedImage;

public class Bullet extends FlyingObject{

	private static BufferedImage image;
	static {
		image = readImage("bullet.png");
	}

	private int step;

	//the initial position of the bullet is unknown
	//so we have to use the the values from Hero
	public Bullet(int x,int y) {
		super(8,14,x,y);
		step=4;
	}

	public void show() {
		super.show();
		System.out.println("speed:"+step);
	}

	public void step() {
		//子弹是向上移动
		y-=step;
	}

	public BufferedImage getImage() {
		BufferedImage img=null;
		if(isLife()) {
			img=image;
		}else if(isDead()){
			//子弹不会爆炸
			//死了直接设置为消失
			state=REMOVE;
		}
		return img;
	}

	//子弹向上出界，重写父类方法
	public boolean isOutOfBounds() {
		return y<this.height;
	}
}
