package Shooter;

import java.awt.image.BufferedImage;

public class BigPlane extends FlyingObject implements Score{

	private static BufferedImage[] images;

	static {
		images = new BufferedImage[5];
		images[0]=readImage("bigplane.png");
		for(int i=1;i<images.length;i++) {
			images[i]=readImage("bigplane_ember"+i+".png");
		}	
	}

	private int step;
	private int life;

	public BigPlane() {
		super(66,99);
		life=15;
		step=2;
	}

	public void show() {
		super.show();
		System.out.println("life:"+life);
		System.out.println("speed:"+step);
	}

	public void step() {
		y+=step;
	}

	int index = 1;
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

	public int getLife() {
		return this.life;
	}

	public void subLife() {
		this.life--;
	}

	public int getScore() {
		return 5;
	}

	public int awardType() {
		return 0;
	}
}
