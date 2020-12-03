package com.my.shoot;

import java.awt.Graphics;//代表画笔
import java.awt.image.BufferedImage;//专门装图片的
import javax.imageio.ImageIO;//图片的读和写
import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.Random;

/**
 * 飞行物
 * @author ACGkaka
 */
public abstract class AbstractFlyingObject {

    /**
     * 对象的生命周期 - 活着的
     */
    private static final int LIFE = 0;
    /**
     * 对象的生命周期 - 死了的（但没有删除呢）
     */
    private static final int DEAD = 1;
    /**
     * 对象的生命周期 - 可以删除的
     */
    static final int REMOVE = 2;
    /**
     * 对象的生命周期 - 当前状态（默认活着的）
     */
    int state = LIFE;

    /**
     * 宽
     */
    int width;
    /**
     * 高
     */
    int height;
    /**
     * x坐标1
     */
    int x;
    /**
     * y坐标
     */
    int y;

    /**
     * 读取图片资源
     *
     * @param fileName 图片名
     * @return 图片
     */
    static BufferedImage loadImage(String fileName) {
        try {
            //读取同包中的资源
            return ImageIO.read(Objects.requireNonNull(AbstractFlyingObject.class.getClassLoader().getResource(fileName)));
        } catch (Exception e) {
            // 跟踪异常并打印出来
            e.printStackTrace();
            // 抛出异常
            throw new RuntimeException();
        }
    }

    /**
     * 子类默认调用的构造函数
     */
    AbstractFlyingObject() {
    }

    /**
     * 专门给小敌机，大敌机，小蜜蜂提供的构造
     *
     * @param width  宽度
     * @param height 高度
     */
    AbstractFlyingObject(int width, int height) {
        this.width = width;
        this.height = height;
        Random rand = new Random();
        x = rand.nextInt(World.WIDTH - this.width);
        y = -this.height;
    }

    /**
     * 飞行物移动
     */
    public abstract void step();

    /**
     * 获取对象的图片
     *
     * @return 图片
     */
    public abstract BufferedImage getImage();

    /**
     * 判断飞行物是否越界
     *
     * @return 是否越界
     */
    public abstract boolean outOfBounds();

    /**
     * 画对象
     *
     * @param g 画笔
     */
    public void paint(Graphics g) {
        // 画具
        g.drawImage(getImage(), x, y, null);
    }

    /**
     * 判断对象是否活着
     *
     * @return 是否活着
     */
    boolean isLife() {
        return state == LIFE;
    }

    /**
     * 判断对象是否死了（但没删除）
     *
     * @return 是否死了
     */
    boolean isDead() {
        return state == DEAD;
    }

    /**
     * 判断对象是否可以被删除了
     *
     * @return s是否可以被删除了
     */
    boolean isRemove() {
        return state == REMOVE;
    }

    /**
     * 修改对象的生命周期已经死了
     */
    void goDead() {
        // 已经死了
        state = DEAD;
    }

    /**
     * 判断敌人飞行物（子弹或者英雄机）碰撞
     *
     * @param others 敌人飞行物
     * @return 是否碰撞
     */
    boolean hit(AbstractFlyingObject others) {
        // this:敌人 others:子弹或英雄机
        // x1:敌人的x坐标-子弹或英雄机的宽
        int x1 = this.x - others.width;
        // x2:敌人的x坐标+敌人的宽
        int x2 = this.x + this.width;
        // y1:敌人的y坐标-子弹或英雄机的高
        int y1 = this.y - others.height;
        // y2:敌人的y坐标+敌人的高
        int y2 = this.y + this.height;
        int x = others.x;
        int y = others.y;

        // x在x1与x2之间，y在y1与y2之间
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }

}
