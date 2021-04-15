package Shooter;

import java.awt.image.BufferedImage;
import Shooter.World;

public class Hero extends FlyingObject{

	private static BufferedImage[] images;
	static {
		images = new BufferedImage[2];
		images[0]=readImage("hero0.png");
		images[1]=readImage("hero1.png");
//		for(int i=2;i<images.length;i++) {
//			images[i]=readImage("hero_ember"+i+".png");
//		}	
	}

	private int life;
	private int doubleFire;

	public Hero() {
		//152=400/2-97/2
		super(97,124,192,470);
		life=3;
		doubleFire=0;
	}

	public void show() {
		super.show();
		System.out.println("life:"+life);
		System.out.println("firing mode:"+doubleFire);
	}

	public void step() {
	}



	//	int index=0;
	//	public BufferedImage getImage() {
	//		BufferedImage img=null;
	//		//任何数值取余2之后只可能得0或1
	//		img=images[index%2];
	//		index++;
	//		//index++ 后下次获得另一张图
	//		return img;
	//	}

	int index=0;
	public BufferedImage getImage() {
		BufferedImage img=null;
			img=images[index%2];
			index++;
			return img;
	}

	//英雄机开炮方法
	public Bullet[] shoot() {
		Bullet[] bs=null;
		//为了方便我们使用英雄机宽度的四分之一
		//定义一个变量保存四分之一宽度
		int len01=this.width/4;
		int len02=this.width/5;
		//英雄机开炮分单排和双排
		if(doubleFire>0) {
			//双
			bs=new Bullet[7];
			bs[0]=new Bullet(this.x+len01,this.y-20);
			bs[1]=new Bullet(this.x+2*len01,this.y-20);
			bs[2]=new Bullet(this.x+3*len01,this.y-20);
			bs[3]=new Bullet(this.x-2*len02,this.y+10);
			bs[4]=new Bullet(this.x+7*len02,this.y+10);
			bs[5]=new Bullet(this.x-1*len02,this.y+10);
			bs[6]=new Bullet(this.x+6*len02,this.y+10);
			doubleFire--;//减火力值
		}else {
			//单
			bs=new Bullet[3];
			bs[0]=new Bullet(this.x+len01,this.y-20);
			bs[1]=new Bullet(this.x+2*len01,this.y-20);
			bs[2]=new Bullet(this.x+3*len01,this.y-20);
		}
		return bs; 
	}
	//英雄机移动方法
	public void moveTo(int x, int y) {
		//		英雄机中心y轴到鼠标y轴
		this.x=x-this.width/2;
		this.y=y-this.height/2;
	}

	//获得英雄机的生命值
	public int getLife() {
		return this.life;
	}

	//增加英雄机的生命值
	public void addLife() {
		this.life++;
	}

	//增加英雄机的火力值
	public void addDoubleFire() {
		this.doubleFire+=120;
	}

	//英雄机减命
	public void subLife() {
		this.life--;
	}

	//清空火力值
	public void clearDoubleFire() {
		this.doubleFire=0;
	}

}




