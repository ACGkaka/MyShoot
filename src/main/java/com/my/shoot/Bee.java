package com.my.shoot;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 蜜蜂
 *
 * @author ACGkaka
 */
public class Bee extends AbstractFlyingObject implements Award {
    /**
     * 图片
     */
    private static BufferedImage[] images;

    static {
        // 4张图片
        images = new BufferedImage[4];
        // 遍历图片数组
        for (int i = 0; i < images.length; i++) {
            // 读取图片
            images[i] = loadImage("bee" + i + ".png");
        }
    }

    /**
     * x坐标移动速度
     */
    private int xStep;
    /**
     * y坐标移动速度
     */
    private int yStep;
    /**
     * 奖励类型(0和1)
     */
    private int awardType;

    /**
     * 构造方法
     */
    Bee() {
        super(images[0].getWidth(), images[0].getHeight());
        Random rand = new Random();
        xStep = 1;
        yStep = 2;
        // 0到1之间的随机数
        awardType = rand.nextInt(2);
    }

    /**
     * 小蜜蜂移动
     */
    @Override
    public void step() {
        // x+（向左或向右）
        x += xStep;
        // y+（向下）
        y += yStep;
        if (x <= 0 || x >= World.WIDTH - this.width) {
            // 修改xStep的正负值来实现向左或向右
            xStep *= -1;
        }
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
        // 小蜜蜂的y>=窗口的高，即为越界了
        return this.y >= World.HEIGHT;
    }

    /**
     * 重写getType()获取奖励类型
     *
     * @return 奖励类型
     */
    @Override
    public int getType() {
        // 让小蜜蜂停止前进
        yStep = 0;
        // 返回奖励类型（0到1之间的随机数）
        return awardType;
    }

}
