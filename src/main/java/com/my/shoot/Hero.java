package com.my.shoot;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * 英雄机
 * @author ACGkaka
 */
public class Hero extends com.my.shoot.AbstractFlyingObject {
    private static BufferedImage[] images;

    static {
        images = new BufferedImage[6];
        for (int i = 0; i < images.length; i++) {
            images[i] = loadImage("hero" + i + ".png");
        }
    }

    /**
     * 生命值
     */
    private int life;
    /**
     * 火力值
     */
    private int doubleFire;

    /**
     * 构造方法
     */
    Hero() {
        width = images[0].getWidth();
        height = images[0].getHeight();
        x = World.WIDTH / 2 - width / 2;
        y = 2 * World.HEIGHT / 3 - height / 2;
        life = 3;
        doubleFire = 0;
    }

    /**
     * 英雄机随着鼠标动x，y：鼠标的x坐标和y坐标
     * @param x x坐标
     * @param y y坐标
     */
    void mouseMoveTo(int x, int y) {
        // 英雄机的x=鼠标的x-1/2英雄机的宽
        this.x = x - this.width / 2;
        // 英雄机的y=鼠标的y-1/2英雄机的高
        this.y = y - this.height / 2;
    }

    /**
     * 英雄机随着键盘的按下移动
     * @param x x坐标
     * @param y y坐标
     */
    void keyMoveTo(int x, int y) {
        // 英雄机的x+=侦听到的键盘方向
        this.x += x * 5;
        // 英雄机的y+=侦听到的键盘方向
        this.y += y * 5;
        if (this.x < -this.width / 2) {
            this.x = -this.width / 2;
        }
        if (this.x > World.WIDTH - this.width / 2) {
            this.x = World.WIDTH - this.width / 2;
        }
        if (this.y < 0) {
            this.y = 0;
        }
        if (this.y > World.HEIGHT - this.height) {
            this.y = World.HEIGHT - this.height;
        }
    }

    /**
     * 英雄机切换图片
     */
    @Override
    public void step() {
    }

    /**
     * 活着的起始下标
     */
    private int index = 0;
    /**
     * 死了的起始下标
     */
    private int deadIndex = 1;
    /**
     * 控制爆炸画面播放速度的初始下标
     */
    private int bangIndex = 0;

    /**
     * 重写getImage()获取图片
     * @return 图片
     */
    @Override
    public BufferedImage getImage() {
        if (isLife()) {
            // 如果活着呢，则返回两张图片相互切换的效果
            return images[index++ % 2];
        } else if (isDead()) {
            // 若死了的，则顺序切换爆炸效果图
            // 返回images[1]到images[4]之间的图片对象
            BufferedImage img = images[deadIndex];
            // 控制爆炸效果图片的切换速度
            if (bangIndex++ % World.BANG_SPEED == 0) {
                // 爆炸效果图片的下标增1
                deadIndex++;
            }
            // 若到最后的图片了
            if (deadIndex == images.length) {
                // 可以删除
                state = REMOVE;
            }
            // 返回获取到的图片
            return img;
        }
        return null;
    }

    /**
     * 英雄机发射子弹（生成子弹对象）
     * @return 子弹
     */
    public ArrayList<com.my.shoot.Bullet> shoot() {
        int xStep = this.width / 4;
        int yStep = 20;

        if (this.doubleFire >= 80) {
            // 三倍火力
            ArrayList<com.my.shoot.Bullet> bs = new ArrayList<>();
            // x:英雄机的x+1/4，英雄机的宽y:英雄机的y-固定的20
            bs.add(new com.my.shoot.Bullet(this.x + xStep, this.y - yStep));
            // x:英雄机的x+2/4，英雄机的宽y:英雄机的y-固定的20
            bs.add(new com.my.shoot.Bullet(this.x + 2 * xStep, this.y - yStep));
            // x:英雄机的x+3/4，英雄机的宽y:英雄机的y-固定的20
            bs.add(new com.my.shoot.Bullet(this.x + 3 * xStep, this.y - yStep));
            // 发射一次双倍火力，则火力值-2
            doubleFire -= 3;
            return bs;

        } else if (this.doubleFire >= 40) {
            // 双倍火力
            ArrayList<com.my.shoot.Bullet> bs = new ArrayList<>();
            // x:英雄机的x+1/4，英雄机的宽y:英雄机的y-固定的20
            bs.add(new com.my.shoot.Bullet(this.x + xStep, this.y - yStep));
            // x:英雄机的x+3/4，英雄机的宽y:英雄机的y-固定的20
            bs.add(new com.my.shoot.Bullet(this.x + 3 * xStep, this.y - yStep));
            // 发射一次双倍火力，则火力值-2
            doubleFire -= 2;
            return bs;
        } else {
            // 单倍火力
            ArrayList<com.my.shoot.Bullet> bs = new ArrayList<>();
            // x:英雄机的x+2/4，英雄机的宽y:英雄机的y-固定的20
            bs.add(new com.my.shoot.Bullet(this.x + 2 * xStep, this.y - yStep));
            return bs;
        }
    }

    /**
     * 重写OutOfBounds()判断是否越界
     *
     * @return 是否越界
     */
    @Override
    public boolean outOfBounds() {
        // 永不越界
        return false;
    }

    /**
     * 英雄机增命
     */
    void addLife() {
        life++;
    }

    /**
     * 获取英雄机的生命值
     * @return 生命值
     */
    int getLife() {
        return this.life;
    }

    /**
     * 英雄机减命
     */
    void subtractLife() {
        // 命数减1
        life--;
    }

    /**
     * 清空火力值
     */
    void clearDoubleFire() {
        // 火力值归零
        doubleFire = 0;
    }

    /**
     * 英雄机增火力
     */
    void addDoubleFire() {
        doubleFire += 40;
    }

}
