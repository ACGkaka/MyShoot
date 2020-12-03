package com.my.shoot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 整个世界
 *
 * @author ACGkaka
 */
public class World extends JPanel {
    /**
     * 窗口的宽
     */
    static final int WIDTH = 400;
    /**
     * 窗口的高
     */
    static final int HEIGHT = 700;
    /**
     * 控制画面的绘制速度，每10毫秒画一次
     */
    private static final int PAINT_SPEED = 10;
    /**
     * 控制爆炸画面的切换速度，每(x*10)毫秒切换一次
     */
    static final int BANG_SPEED = 5;

    /**
     * 四种状态
     */
    //启动状态
    private static final int START = 0;
    //运行状态
    private static final int RUNNING = 1;
    //暂停状态
    private static final int PAUSE = 2;
    //游戏结束状态
    private static final int GAME_OVER = 3;

    /**
     * 当前状态（默认为启动状态）
     */
    private int state = START;

    //启动图
    private static BufferedImage start;
    //暂停图
    private static BufferedImage pause;
    //游戏结束图
    private static BufferedImage gameover;

    static {//加载图片
        start = com.my.shoot.AbstractFlyingObject.loadImage("start.png");
        pause = com.my.shoot.AbstractFlyingObject.loadImage("pause.png");
        gameover = com.my.shoot.AbstractFlyingObject.loadImage("gameover.png");
    }

    private boolean kup = false;
    private boolean kdown = false;
    private boolean kleft = false;
    private boolean kright = false;

    /**
     * 用于记录游戏开始运行的时间
     */
    private static long startTime;

    /**
     * 天空
     */
    private com.my.shoot.Sky sky = new com.my.shoot.Sky();
    /**
     * 英雄机
     */
    private com.my.shoot.Hero hero = new com.my.shoot.Hero();
    /**
     * 小敌机+大敌机+小蜜蜂
     */
    private ArrayList<com.my.shoot.AbstractFlyingObject> enemies = new ArrayList<>();
    /**
     * 子弹
     */
    private ArrayList<com.my.shoot.Bullet> bullets = new ArrayList<>();

    /**
     * 生成敌人（小敌机+大敌机+小蜜蜂）对象
     *
     * @return 敌人
     */
    private com.my.shoot.AbstractFlyingObject nextOne() {
        // 随机数对象
        Random rand = new Random();
        // 生成0到19之间的随机数
        int type = rand.nextInt(10);
        if (type < 1) {
            // 0到9，则返回小敌机对象
            return new com.my.shoot.Bee();
        } else if (type < level) {
            // 10到15，则返回大敌机对象
            return new com.my.shoot.BigAirplane(level + 1);
        } else {
            // 15到19，则返回小蜜蜂对象
            return new com.my.shoot.Airplane(level + 1);
        }
    }

    /**
     * 敌人入场计数
     */
    private int flyIndex = 0;

    /**
     * 敌机（小敌机+大敌机+小蜜蜂）入场
     */
    private void enterAction() {
        // 10毫秒走一次，每10毫秒增1
        flyIndex++;
        // 每400(40*10)毫秒走一次
        if (flyIndex % 40 == 0) {
            // 获取敌人对象
            com.my.shoot.AbstractFlyingObject obj = nextOne();
            enemies.add(obj);
        }
    }

    /**
     * 启动程序的执行
     */
    private void stepAction() {
        // 10毫秒走一次
        sky.step();
        // 遍历所有敌人
        for (com.my.shoot.AbstractFlyingObject enemy : enemies) {
            // 敌人移动
            enemy.step();
        }
        // 遍历所有子弹
        for (com.my.shoot.Bullet bullet : bullets) {
            // 子弹移动
            bullet.step();
        }
    }

    private int shootIndex = 0;

    /**
     * 子弹入场（英雄机发射子弹）
     */
    private void shootAction() {
        // 10毫秒走一次，每10毫秒增1
        shootIndex++;
        // 每300（30*10）毫秒走一次
        if (shootIndex % 30 == 0) {
            // 获取了子弹对象
            ArrayList<com.my.shoot.Bullet> bs = hero.shoot();
            bullets.addAll(bs);
        }
    }

    /**
     * 删除越界的飞行物（敌人+子弹）
     */
    private void outOfBoundsAction() {
        // 10毫秒走一次
        // 1）不越界敌人数组的下标；2）不越界敌人数组的长度
        int index = 0;
        ArrayList<com.my.shoot.AbstractFlyingObject> enemiesLives = new ArrayList<>();
        // 不越界敌人数组（enemies有几个，则不越界敌人数组长度为几）
        // 遍历数组
        for (com.my.shoot.AbstractFlyingObject f : enemies) {
            //若不越界则装起来（活着的，死了的，可以删除的）
            //若不越界，并且，可以删除为false的，才装起来
            if (!f.outOfBounds() && !f.isRemove()) {
                // 将不越界敌人对象添加到不越界敌人数组中
                enemiesLives.add(f);
                //1）不越界敌人数组下标增1；2）不越界敌人个数增1
                index++;
            }
        }
        enemies = enemiesLives;
        // 将不越界数组复制到enemies中，index为几则enemies中有几个元素
        // 将index清零，便于子弹中使用
        index = 0;
        // 1）不越界子弹数组的下标	2）不越界子弹数组的长度
        ArrayList<com.my.shoot.Bullet> bulletLives = new ArrayList<>();
        // 不越界子弹数组（bullets有几个，则不越界子弹数组长度为几）
        for (com.my.shoot.Bullet b : bullets) {
            if (!b.outOfBounds() && !b.isRemove()) {
                // 将不越界子弹对象添加到不越界子弹数组中
                bulletLives.add(b);
                // 1）不越界子弹数组下标增1	2）不越界子弹个数增1
                index++;
            }
        }
        bullets = bulletLives;
        // 将不越界数组复制到bullets中，index为几则bullets中有几个元素
    }

    /**
     * 玩家得分
     */
    private int score = 0;

    /**
     * 子弹与敌人（小敌机+大敌机+小蜜蜂）撞击
     */
    private void bulletBang() {//每10毫秒走一次
        for (com.my.shoot.Bullet b : bullets) {
            for (com.my.shoot.AbstractFlyingObject f : enemies) {
                if (b.isLife() && f.isLife() && f.hit(b)) {
                    // 判断是不是大敌机类型
                    if (f instanceof com.my.shoot.BigAirplane) {
                        // 如果是，则大敌机的生命减1
                        ((com.my.shoot.BigAirplane) f).subtractLife();
                        // 当大敌机的生命为零时
                        if (((com.my.shoot.BigAirplane) f).getLife() == 0) {
                            // 修改大敌机的状态为可以死了的
                            f.goDead();
                        }
                    } else {
                        // 若不是大敌机的话
                        // 直接修改状态为可以死了的
                        f.goDead();
                    }
                    // 子弹或英雄机的状态为死（修改状态为死了的）
                    b.goDead();
                    // 若被撞对象是敌人
                    if (f.isDead() && f instanceof com.my.shoot.Enemy) {
                        // 将被撞对象强转为敌人
                        com.my.shoot.Enemy e = (com.my.shoot.Enemy) f;
                        // 玩家得分
                        score += e.getScore();
                    }
                    // 若被撞对象是奖励
                    if (f instanceof com.my.shoot.Award) {
                        // 将被撞对象强转为奖励类型
                        com.my.shoot.Award a = (com.my.shoot.Award) f;
                        // 获取奖励类型
                        switch (a.getType()) {
                            // 奖励类型为火力
                            case com.my.shoot.Award.DOUBLE_FIRE:
                                // 则英雄机增加火力
                                hero.addDoubleFire();
                                break;
                            // 奖励类型为命
                            case com.my.shoot.Award.LIFE:
                                // 则英雄机增命
                                hero.addLife();
                                break;
                            default:
                        }
                    }
                }
            }
        }
    }

    /**
     * 英雄机与敌人（小敌机+大敌机+小蜜蜂）的碰撞
     */
    private void heroBang() {
        // 10毫秒走一次
        // 遍历敌人数组
        for (com.my.shoot.AbstractFlyingObject f : enemies) {
            // 如果敌人活着并且敌人与英雄机撞上了
            if (f.isLife() && f.hit(hero)) {
                // 敌人去死
                f.goDead();
                // 英雄机减命
                hero.subtractLife();
                // 英雄级清空火力值
                hero.clearDoubleFire();
            }
        }
    }

    /**
     * 检查游戏结束
     */
    private void checkGameOverAction() {
        // 10毫秒走一次
        if (hero.getLife() <= 0) {
            // 游戏结束了
            // 修改当前状态为游戏结束状态
            state = GAME_OVER;
        }
    }

    /**
     * 设置关卡，控制敌机的移动速度
     */
    private int level = 1;

    /**
     * 达到一定分数后给敌人增速
     */
    private void addStepAction() {
        if (score != 0 && time > 5 * level) {
            // 设置关卡随着时间而增加
            level++;
        }
    }

    /**
     * 键盘控制
     */
    private void keyControl() {
        // 创建键盘侦听对象
        KeyListener l = new KeyAdapter() {
            /**
             * 重写keyReleased()键盘弹起事件
             * @param e 键盘事件
             */
            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                switch (key) {
                    // case KeyEvent.VK_SPACE; -- 控制空格键
                    case KeyEvent.VK_UP:
                        // 控制英雄机往上移动
                        kup = false;
                        break;
                    case KeyEvent.VK_LEFT:
                        // 控制英雄机往左移动
                        kleft = false;
                        break;
                    case KeyEvent.VK_RIGHT:
                        // 控制英雄机往右移动
                        kright = false;
                        break;
                    case KeyEvent.VK_DOWN:
                        // 控制英雄机往下移动
                        kdown = false;
                        break;
                    default:
                }
            }

            /*	重写keyPressed()键盘按下事件	*/
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                switch (key) {
                    // case KeyEvent.VK_SPACE; -- 控制空格键
                    case KeyEvent.VK_UP:
                        // 控制英雄机往上移动
                        kup = true;
                        break;
                    case KeyEvent.VK_LEFT:
                        // 控制英雄机往左移动
                        kleft = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        // 控制英雄机往右移动
                        kright = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        // 控制英雄机往下移动
                        kdown = true;
                        break;
                    case KeyEvent.VK_F1:
                        // 控制游戏暂停
                        if (state == RUNNING) {
                            // 如果游戏正在运行
                            // 则修改状态为暂停状态
                            state = PAUSE;
                        } else if (state == PAUSE) {
                            // 如果游戏已经暂停
                            // 则修改状态为运行状态
                            state = RUNNING;
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        // 控制游戏的开始和结束
                        if (state == START) {
                            // 如果游戏状态为开始
                            // 初始化游戏的开始事件
                            startTime = System.currentTimeMillis();
                            // 修改游戏的状态为运行状态
                            state = RUNNING;
                        } else if (state == GAME_OVER) {
                            // 如果游戏状态为游戏结束状态
                            // 清理现场
                            score = 0;
                            // 清空时间数（秒）
                            time = 0;
                            // 清空旧时间
                            oldTime = 0;
                            // 清空关卡
                            level = 1;
                            // 天空
                            sky = new com.my.shoot.Sky();
                            // 英雄机
                            hero = new com.my.shoot.Hero();
                            // 小敌机+大敌机+小蜜蜂
                            enemies = new ArrayList<>();
                            // 子弹
                            bullets = new ArrayList<>();
                            // 修改为启动状态
                            state = START;
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        System.exit(0);
                    default:
                }
            }
        };
        // 处理键盘操作事件
        this.addKeyListener(l);
        // 将控件设置成可获取焦点的状态，默认false，
        // 即默认控件不能获取焦点，只有设置为true才能获取控件的点击事件
        this.setFocusable(true);
        // 把焦点转移到键盘上来
        this.requestFocus();

    }

    /**
     * 鼠标控制
     */
    private void mouseControl() {
        //创建鼠标侦听对象
        MouseAdapter l = new MouseAdapter() {
            /**
             * 重写mouseMoved()鼠标移动事件
             * @param e 鼠标事件
             */
            @Override
            public void mouseMoved(MouseEvent e) {
                if (state == RUNNING) {
                    // 获取鼠标的x坐标
                    int x = e.getX();
                    // 获取鼠标的y坐标
                    int y = e.getY();
                    // 英雄机随着鼠标移动
                    hero.mouseMoveTo(x, y);
                }
            }

            /**
             * 重写mouseClicked()鼠标点击事件
             * @param e 鼠标事件
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (state) {
                    //根据当前状态做不同的处理
                    case START:
                        //启动状态时
                        state = RUNNING;
                        //修改为运行状态
                        startTime = System.currentTimeMillis();
                        break;
                    case GAME_OVER:
                        // 游戏结束状态时
                        // 清理现场
                        score = 0;
                        // 清空时间数（秒）
                        time = 0;
                        // 清空旧时间
                        oldTime = 0;
                        // 清空关卡
                        level = 1;
                        // 天空
                        sky = new com.my.shoot.Sky();
                        // 英雄机
                        hero = new com.my.shoot.Hero();
                        // 小敌机+大敌机+小蜜蜂
                        enemies = new ArrayList<>();
                        // 子弹
                        bullets = new ArrayList<>();
                        // 修改为启动状态
                        state = START;
                        break;
                    default:
                }
            }

            /**
             * 重写mouseExited()鼠标移出窗口事件
             * @param e 鼠标事件
             */
            @Override
            public void mouseExited(MouseEvent e) {
                if (state == RUNNING) {//运行状态时
                    state = PAUSE;        //变为暂停状态
                }
            }

            /**
             * 重写mouseEntered()鼠标移入窗口事件
             * @param e 鼠标事件
             */
            @Override
            public void mouseEntered(MouseEvent e) {
                if (state == PAUSE) {    //暂停状态时
                    state = RUNNING;    //变为运行状态
                }
            }
        };
        //处理鼠标操作事件
        this.addMouseListener(l);
        //处理鼠标滑动事件
        this.addMouseMotionListener(l);
    }

    private void startAction() {
        //声明操作方式选项
        Object[] options = {"鼠标可以", "键盘不错", "不玩了"};
        //弹出对话框来选择操作方式
        control = JOptionPane.showOptionDialog(this,
                //对话框内容，对话框标题
                "使用哪种操作方式？", "操作选择",
                //对话框有两个选项
                JOptionPane.YES_NO_CANCEL_OPTION,
                //用于问题
                JOptionPane.QUESTION_MESSAGE,
                //图标，选项变量名，第一个选项
                null, options, options[0]);
    }

    /**
     * 控制英雄机移动
     */
    private void heroAction() {
        //英雄机向上移动
        //如果方向上被按下并且英雄没有出上界
        if (kup && hero.y > 0) {
            hero.keyMoveTo(0, -1);
        }
        //英雄机向下方移动
        //如果方向下被按下,并且英雄没有出下界
        if (kdown && hero.y + hero.height < World.HEIGHT) {
            hero.keyMoveTo(0, 1);
        }
        //英雄机向左移动
        //如果方向左被按下，并且英雄没有出左界
        if (kleft && hero.x > 0) {
            hero.keyMoveTo(-1, 0);
        }
        //英雄机向右移动
        //如果方向右被按下，并且英雄没有出右界
        if (kright && hero.x + hero.width < World.WIDTH) {
            hero.keyMoveTo(1, 0);
        }
        //英雄机向左上方移动
        //如果方向左和方向上被按下，并且英雄没有出左界
        if (kleft && hero.x > 0 && kup && hero.y > 0) {
            hero.keyMoveTo(-1, -1);
        }
        //英雄机向右上方移动
        //如果方向右和方向上被按下，并且英雄没有出右界
        if (kright && hero.x + hero.width < World.WIDTH && kup && hero.y > 0) {
            hero.keyMoveTo(1, -1);
        }
        //英雄机向右下方移动
        //如果方向右和方向下被按下，并且英雄没有出右界
        if (kright && hero.x + hero.width < World.WIDTH && kdown && hero.y + hero.height < World.HEIGHT) {
            hero.keyMoveTo(1, 1);
        }
        //英雄机向左下方移动
        //如果方向左被按下，并且英雄没有出左界
        if (kleft && hero.x > 0 && kdown && hero.y + hero.height < World.HEIGHT) {
            hero.keyMoveTo(-1, 1);
        }
    }

    /**
     * 控制游戏操作方式
     */
    private int control = 1;

    /**
     * 启动程序的执行
     */
    private void action() {
        startAction();
        if (control == 0) {
        //如果是鼠标操作
            mouseControl();
        } else if (control == 1) {
        //如果是键盘操作
            keyControl();
        } else {
            //如果是其他情况，直接闪退
            System.exit(0);
        }
        //创建时间侦听对象
        //定时器对象
        Timer timer = new Timer();
        //时间间隔（以毫秒为单位）
        int intervel = World.PAINT_SPEED;
        //定时计划
        timer.schedule(new TimerTask() {
            //定时干的那个事儿（每10毫秒走一次）
            @Override
            public void run() {
                if (state == RUNNING) {
                    //敌机（小敌机+大敌机+小蜜蜂）入场
                    enterAction();
                    //飞行物移动
                    stepAction();
                    //英雄机移动
                    heroAction();
                    //子弹入场（英雄机发射子弹）
                    shootAction();
                    //删除越界的飞行物
                    outOfBoundsAction();
                    // 用于测试删除越界的飞行物是否正常
                    //System.out.println(enemies.length+","+bullets.length);
                    //子弹与敌人（小敌机+大敌机+小蜜蜂）撞击
                    bulletBang();
                    //英雄机与敌人（小敌机+大敌机+小蜜蜂）的碰撞
                    heroBang();
                    //	达到一定分数后给敌人增速
                    addStepAction();
                    //检查游戏结束
                    checkGameOverAction();
                }
                //重画（重新调用paint()）
                repaint();
            }
        }, intervel, intervel);
    }

    /**
     * 用于记录开始游戏后到现在为止的时间数（秒）
     */
    private long time = 0;
    /**
     * 用于记录暂停前的游戏时间（秒）
     */
    private long oldTime = 0;

    /**
     * 重写paint()方法
     * @param g 画笔
     */
    @Override
    public void paint(Graphics g) {
        //画天空对象
        sky.paint(g);
        //画英雄机对象
        hero.paint(g);
        for (com.my.shoot.AbstractFlyingObject enemy : enemies) {
            //画敌人对象
            enemy.paint(g);
        }
        for (com.my.shoot.Bullet bullet : bullets) {
            //画子弹对象
            bullet.paint(g);
        }

        //如果游戏状态为运行
        if (state == World.RUNNING) {
            //记录游戏开始后的时间
            time = oldTime + (System.currentTimeMillis() - startTime) / 1000;
        }
        //如果游戏状态为暂停
        if (state == World.PAUSE) {
            //重新给游戏开始时间赋值
            startTime = System.currentTimeMillis();
            //并存储之前的游戏时间
            oldTime = time;
        }

        //根据当前状态画不同的图
        switch (state) {
            //启动状态画启动图
            case START:
                g.drawImage(start, 0, 0, null);
                g.setColor(Color.white);
                //自定义格式并应用
                g.setFont(new Font("Cambria", Font.ITALIC + Font.BOLD, 20));
                if (control == 0) {
                    //如果是鼠标操作
                    g.drawString("(Click To Start)", 140, 350);
                } else {
                    //如果是键盘操作
                    g.drawString("(Press \"Enter\" To Start)", 95, 350);
                }
                break;
            case RUNNING:
                //设置“生存”字体格式，格式为“Indie Flower"，黑体，50磅字
                Font drawFont1 = new Font("Indie Flower", Font.BOLD, 50);
                //设置“时间”字体格式，格式为“Indie Flower"，黑体，20磅字
                Font drawFont2 = new Font("Indie Flower", Font.BOLD, 20);
                //设置“分数”字体格式，格式为“Indie Flower"，黑体，40磅字
                Font drawFont3 = new Font("Indie Flower", Font.BOLD, 40);
                //应用第一种格式
                g.setFont(drawFont1);
                //设置颜色为GRB(142,16,28)
                g.setColor(new Color(142, 16, 28));
                //打印SURVIVE！
                g.drawString("SURVIVE!", 120, 70);
                //应用第三种字体
                g.setFont(drawFont3);
                //打印存活时间
                g.drawString("" + time, 40, 110);
                //设置字体颜色为黑色
                g.setColor(Color.black);
                //应用第二种字体格式
                g.setFont(drawFont2);
                // 画分
                g.drawString("SCORE: "+score, 10, 35);
                //画命
                g.drawString("LIFE : " + hero.getLife(), 10, 55);
                //打印TIME：
                g.drawString("TIME : ", 10, 75);
                //打印单位：s
                g.drawString("s", 80, 110);
                g.setColor(Color.WHITE);
                if (control == 1) {
                    //如果是键盘操作
                    g.drawString("F1->Pause", 10, 140);
                    g.drawString("Esc->Exit", 10, 160);
                }
                break;
            case PAUSE:
                //暂停状态画暂停图
                g.drawImage(pause, 0, 0, null);
                break;
            case GAME_OVER:
                //游戏结束状态画游戏结束图
                g.clearRect(0, 0, World.WIDTH, World.HEIGHT);
                g.drawImage(gameover, 0, 0, null);
                //自定义格式并应用
                g.setFont(new Font("Cambria", Font.ITALIC + Font.BOLD, 50));
                g.drawString("TIME:", 100, 180);
                //自定义格式并应用
                g.setFont(new Font("Cambria", Font.ITALIC + Font.BOLD, 20));
                if (control == 0) {
                    g.drawString("(Click To Restart)", 125, 330);
                } else {
                    g.drawString("(Press \"Enter\" To Restart)", 85, 330);
                    g.drawString("(\"Esc\" To Exit)", 130, 370);
                }
                //自定义格式并应用
                g.setFont(new Font("Cambria", Font.ITALIC + Font.BOLD, 50));
                //设置颜色为GRB(142,16,28)
                g.setColor(new Color(142, 16, 28));
                g.drawString(time + "s", 240, 180);
            default:
        }
    }

    public static void main(String[] args) {
        //创建窗口对象
        JFrame frame = new JFrame("FlyingShoot");
        //创建面板对象
        World world = new World();
        //将面板添加到窗口中
        frame.add(world);
        //设置窗口的宽和高
        frame.setSize(WIDTH, HEIGHT);
        //设置窗口为不可调整大小
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置窗口关闭的时候退出程序
        frame.setLocationRelativeTo(null);
        //设置居中显示
        //1）设置窗口可见	2）尽快调用paint()方法
        frame.setVisible(true);

        world.action();
    }
}
