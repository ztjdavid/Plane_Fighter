package Shooter;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel; 

public class World extends JPanel{
	//继承JPanel表示world类是一个窗口

	//声明两个常量
	//游戏窗口的宽和高
	public static final int WIDTH=480;
	public static final int HEIGHT=852;

	//定义游戏的分数
	private int score=0;
	
	private int level;

	//定义游戏状态常量
	public static final int START=0;//开始
	public static final int RUNNING=1;//运行
	public static final int PAUSE=2;//暂停
	public static final int GAME_OVER=3;//结束

	//设置当前游戏状态
	private int state=START;

	//声明三个状态对应的三个图片
	private static BufferedImage startImg;
	private static BufferedImage pauseImg;
	private static BufferedImage gameOverImg;
	static {
		startImg=FlyingObject.readImage("start01.jpg");
		pauseImg=FlyingObject.readImage("pause.png");
		gameOverImg=FlyingObject.readImage("gameover.png");
	}

	Hero hero = new Hero();
	Sky sky = new Sky();
	Airplane[] ap=new Airplane[3];
	BigPlane[] bap=new BigPlane[3];
	Bee[] bs = new Bee[2];
	Bullet[] bullets= {
	};
	FlyingObject[] enemy= {
	};

	public void start() {
		//编写鼠标的监听器
		MouseAdapter l = new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if(state==RUNNING) {
					hero.moveTo(e.getX(), e.getY());
				}
			}
			//鼠标点击时的状态切换
			public void mouseClicked(MouseEvent e) {
				switch(state) {
				case START://如果是开始状态
					state=RUNNING;//进入运行状态
					break;
				case GAME_OVER://如果是结束状态
					state=START;//进入开始状态
					//重制游戏
					score=0;
					hero=new Hero();
					sky=new Sky();
					bullets = new Bullet[0];
					enemy = new FlyingObject[0];
					break;
				}
			}
			//鼠标移入时状态切换
			public void mouseEntered(MouseEvent e) {
				if(state==PAUSE) {//如果是暂停
					state=RUNNING;//进入运行
				}
			}
			//鼠标移出时状态切换
			public void mouseExited(MouseEvent e) {
				if(state==RUNNING) {//如果是运行
					state=PAUSE;//进入暂停
				}
			}
		};
		//绑定鼠标的移动和滑动事件
		this.addMouseListener(l);
		this.addMouseMotionListener(l);

		//定义计时器的时间间隔
		int interval = 20;
		//声明计时器
		Timer timer=new Timer();
		//声明计时器任务
		TimerTask task = new TimerTask() {
			//匿名内部类
			public void run() {
				if(state==RUNNING) {
					//周期运行的内容
					//调用飞行物移动方法
					moveAction();
					enterAction();
					shootAction();
					outOfBoundsAction();
					bulletHitAction();
					heroHitAction();
					gameOverAction();
					//				System.out.println(enemy.length);
					//				System.out.println(bullets.length);
					//重绘所有对象
				}
				repaint();
			}
		};
		//启动计时器周期运行
		timer.schedule(task, interval, interval);
	}

	//判断游戏结束的方法
	public void gameOverAction() {
		//如果生命值小于等于0
		if (hero.getLife()<=0) {
			//游戏进入结束状态
			state=GAME_OVER;
		}
	}

	//判断英雄机和敌机碰撞的方法
	public void heroHitAction() {
		//遍历所有敌机
		for(int i=0;i<enemy.length;i++) {
			FlyingObject f = enemy[i];
			// 判断英雄机是否和该敌机相撞
			if(f.isLife() && hero.hit(f)) {
				//撞死敌机
				f.goDead();
				//英雄机减命，清火力
				hero.subLife();
				hero.clearDoubleFire();			
			}
		}
	}

	//判断子弹和敌机碰撞的方法
	public void bulletHitAction() {
		//遍历所有子弹
		for(int i=0;i<bullets.length;i++) {
			Bullet b=bullets[i];//提取子弹
			//遍历所有敌机
			for(int j=0;j<enemy.length;j++) {
				FlyingObject f = enemy[j];//提取敌机
				//判断子弹是否击中敌机
				if(b.isLife() && f.isLife()
						&& b.hit(f)) {
					b.goDead();//子弹死
					if(f instanceof BigPlane) {
						if(f.isLife()) {
							((BigPlane) f).subLife();
						}
						if(((BigPlane) f).getLife()<=0) {
							f.goDead();
							Score s = (Score)f;
							score+=s.getScore();		
						}
					}else if(f instanceof Airplane) {
						if(f.isLife()) {
							((Airplane) f).subLife();
						}
						if(((Airplane) f).getLife()<=0) {
							f.goDead();
							Score s = (Score)f;
							score+=s.getScore();		
						}
					}
					else {
						f.goDead();//敌机死
						//判断敌机是不是得分的
						if(f instanceof Score) {
							Score s = (Score)f;
							score+=s.getScore();
						}
						//判断敌机是不是有奖励
						if(f instanceof Award) {
							Award a = (Award)f;
							//获得这架奖励机的奖励数值
							int num=a.awardType();
							switch(num) {
							case Award.LIFE://如果是0
								hero.addLife();//加命
								break;
							case Award.DOUBLE_FIRE://如果是1
								hero.addDoubleFire();//加火力
								break;
							}
						}
					}
				}
			}
		}
	}

	//检测出界的方法
	public void outOfBoundsAction() {
		int index=0;//没有出界的元素下标
		//同时保存没出界元素的数量
		//定义保存未出界元素的数组
		FlyingObject[] fs = new FlyingObject[enemy.length];
		//遍历所有敌机，检测是否出界
		for(int i=0; i<enemy.length;i++) {
			//提取当前元素
			FlyingObject f=enemy[i];
			//			判断是否出界，并且没移除
			if(!f.isOutOfBOunds() && !f.isRemove()) {
				fs[index]=f;//放入新数组
				index++;//下标加1
			}
		}
		//数组缩容
		enemy=Arrays.copyOf(fs, index);
		//处理子弹出界
		index=0;
		Bullet[] bs = new Bullet[bullets.length];
		for(int i = 0;i<bullets.length;i++) {
			Bullet b = bullets[i];
			if(!b.isOutOfBounds() && !b.isRemove()) {
				bs[index]=b;
				index++;
			}
		}
		bullets = Arrays.copyOf(bs, index);
	}


	//子弹进场方法
	int shootIndex=1;
	public void shootAction() {
		if(shootIndex%9==0) {
			// 调用英雄机的开炮方法
			Bullet[] bs = hero.shoot();
			//对bullets数组进行扩容
			bullets=Arrays.copyOf(bullets, 
					bullets.length+bs.length);
			//将英雄机发射的新炮弹放到扩容后数组对最后位置
			System.arraycopy(bs, 0, bullets, 
					bullets.length-bs.length, bs.length);
		}
		shootIndex++;
	}

	//敌机进场方法
	int enterIndex = 1;
	public void enterAction() {
		if(score<=100) {
			this.level=1;
		if(enterIndex%25==0) {
			//生成一架敌机
			FlyingObject fo=nextEnemy();
			//扩容当前敌机数组
			enemy=Arrays.copyOf(enemy, enemy.length+1);
			//将生成的敌机放入扩容后数组的最后
			enemy[enemy.length-1]=fo;
		}
		enterIndex++;
		}else if(score<=250) {
			this.level=2;
		if(enterIndex%15==0) {
			//生成一架敌机
			FlyingObject fo=nextEnemy();
			//扩容当前敌机数组
			enemy=Arrays.copyOf(enemy, enemy.length+1);
			//将生成的敌机放入扩容后数组的最后
			enemy[enemy.length-1]=fo;
		}
		enterIndex++;
		}else if(score<=600) {
			this.level=3;
		if(enterIndex%11==0) {
			//生成一架敌机
			FlyingObject fo=nextEnemy();
			//扩容当前敌机数组
			enemy=Arrays.copyOf(enemy, enemy.length+1);
			//将生成的敌机放入扩容后数组的最后
			enemy[enemy.length-1]=fo;
		}
		enterIndex++;
		}else if(score<=900) {
			this.level=4;
		if(enterIndex%9==0) {
			//生成一架敌机
			FlyingObject fo=nextEnemy();
			//扩容当前敌机数组
			enemy=Arrays.copyOf(enemy, enemy.length+1);
			//将生成的敌机放入扩容后数组的最后
			enemy[enemy.length-1]=fo;
		}
		enterIndex++;
		}
		else if(score<=1200) {
			this.level=5;
		if(enterIndex%7==0) {
			//生成一架敌机
			FlyingObject fo=nextEnemy();
			//扩容当前敌机数组
			enemy=Arrays.copyOf(enemy, enemy.length+1);
			//将生成的敌机放入扩容后数组的最后
			enemy[enemy.length-1]=fo;
		}
		enterIndex++;
		}else if(score<=1500){
			this.level=6;
		if(enterIndex%5==0) {
			//生成一架敌机
			FlyingObject fo=nextEnemy();
			//扩容当前敌机数组
			enemy=Arrays.copyOf(enemy, enemy.length+1);
			//将生成的敌机放入扩容后数组的最后
			enemy[enemy.length-1]=fo;
		}
		enterIndex++;
		}else {
			this.level=7;
		if(enterIndex%2==0) {
			//生成一架敌机
			FlyingObject fo=nextEnemy();
			//扩容当前敌机数组
			enemy=Arrays.copyOf(enemy, enemy.length+1);
			//将生成的敌机放入扩容后数组的最后
			enemy[enemy.length-1]=fo;
		}
		enterIndex++;
		}
	}

	//飞行物移动的方法
	public void moveAction() {
		sky.step();//天空移动
		//敌机移动
		for(int i=0;i<enemy.length;i++) {
			enemy[i].step();
		}
		//子弹移动
		for(int i=0;i<bullets.length;i++) {
			bullets[i].step();
		}
	}

	//随机产生敌机的方法
	public FlyingObject nextEnemy() {
		Random ran = new Random();
		FlyingObject fo = null;
		int num=ran.nextInt(100);
		if(num<75) {
			//75%几率生成小敌机
			fo=new Airplane();
		}else if (num<95) {
			//20%几率生成大敌机
			fo = new BigPlane();
		}else
  			//5%几率生成奖励机
			fo=new Bee();
		return fo;
	}

	//重写了JPanel中的方法
	//方法名必须叫paint
	public void paint(Graphics g) {
		//先画背景，再画其他的
		sky.paintObject(g);
		hero.paintObject(g);
		for(int i = 0; i<enemy.length;i++) {
			enemy[i].paintObject(g);
		}
		for(int i = 0;i<bullets.length;i++) {
			bullets[i].paintObject(g);
		}
		//显示分数和生命值
		g.drawString("分数:"+score, 20, 20);
		g.drawString("生命值:"+hero.getLife(), 20, 45);
		g.drawString("难度:"+level,20,70);
		g.drawString("开发者:赵庭基",390,20);

		//根据游戏状态绘制状态图片
		switch(state) {
		case START:
			g.drawImage(startImg,0,0,null);
			break;
		case PAUSE:
			g.drawImage(pauseImg,0,0,null);
			break;
		case GAME_OVER:
			g.drawImage(gameOverImg,0,0,null);
			break;
		}
	}

	public static void main(String[] args) {
		World w = new World();
		//实例化一个窗口
		JFrame f = new JFrame("飞机大战");
		//将w设置到窗口中
		f.add(w);
		//设置窗口的宽和高
		f.setSize(480,852);
		//设置窗口关闭时，程序结束
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//设置窗口居中
		f.setLocationRelativeTo(null);
		//显示窗口，自动调用上面的paint
		f.setVisible(true);
		w.start();	

	}
}
