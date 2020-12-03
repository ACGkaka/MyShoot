package com.my.shoot;

import java.awt.image.BufferedImage;

/**
 * 大敌机
 *
 * @author ACGkaka
 */
public class BigAirplane extends AbstractFlyingObject implements Enemy {
    private static BufferedImage[] images;

    static {
        images = new BufferedImage[5];
        for (int i = 0; i < images.length; i++) {
            images[i] = loadImage("bigplane" + i + ".png");
        }
    }

    /**
     * 移动速度
     */
    private int step;
    /**
     * 大敌机的生命值
     */
    private int life;

    /**
     * 构造方法
     *
     * @param step 移动速度
     */
    BigAirplane(int step) {
        super(images[0].getWidth(), images[0].getHeight());
        this.step = step;
        life = 3;
    }

    /**
     * 大敌机移动
     */
    @Override
    public void step() {
        y += step;
    }

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
     *
     * @return 图片
     */
    @Override
    public BufferedImage getImage() {
        // 若活着呢
        if (isLife()) {
            // 则直接返回images[0]
            return images[0];
            // 若死了呢
        } else if (isDead()) {
            // 获取images[1]到images[4]之间的图片
            BufferedImage img = images[deadIndex];
            // 控制爆炸效果图片的切换速度
            if (bangIndex++ % World.BANG_SPEED == 0) {
                // 爆炸效果图片的下标增1
                deadIndex++;
            }
            // 如果到最后一张图片了
            if (deadIndex == images.length) {
                // 修改状态为可以删除了的
                state = REMOVE;
            }
            // 返回获取到的图片
            return img;
        }
        return null;
    }

    /**
     * 重写OutOfBounds()判断是否越界
     *
     * @return 是否越界
     */
    @Override
    public boolean outOfBounds() {
        // 大敌机的y>=窗口的高，即为越界了
        return this.y >= World.HEIGHT;
    }

    /**
     * 重写getScore()得分
     *
     * @return 分数
     */
    @Override
    public int getScore() {
        // 让大敌机停止前进
        step = 0;
        // 打掉一个小敌机，得3分
        return 3;
    }

    /**
     * 大敌机的生命减1
     */
    void subtractLife() {
        this.life--;
    }

    /**
     * 返回大敌机的生命值
     * @return 生命值
     */
    int getLife() {
        return this.life;
    }

}
