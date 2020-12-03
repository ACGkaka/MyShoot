package com.my.shoot;

import java.awt.image.BufferedImage;//专门装图片的

/**
 * 小敌机
 *
 * @author ACGkaka
 */
public class Airplane extends AbstractFlyingObject implements Enemy {
    /**
     * 图片
     */
    private static BufferedImage[] images;

    static {
        // 5张图片
        images = new BufferedImage[5];
        // 遍历图片数组
        for (int i = 0; i < images.length; i++) {
            // 读取图片
            images[i] = loadImage("airplane" + i + ".png");
        }
    }

    /**
     * 控制小敌机的移动速度，值越大越快
     */
    private int step;

    /**
     * 构造方法
     * @param step  移动速度
     */
    Airplane(int step) {
        super(images[0].getWidth(), images[0].getHeight());
        this.step = step;
    }

    /**
     * 小敌机移动
     */
    @Override
    public void step() {
        // y+向上
        y += step;
    }

    /**
     * 死了的初始起始下标
     */
    private int deadIndex = 1;
    /**
     * 控制爆炸画面播放速度的初始下标
     */
    private int bangIndex = 0;

    /**
     * 重写getImage()获取图片
     * @return  图片
     */
    @Override
    public BufferedImage getImage() {
        if (isLife()) {
            return images[0];//若活着呢，直接返回images[0]
        } else if (isDead()) {//若死了呢，则顺序切换爆炸效果图片
            BufferedImage img = images[deadIndex];//返回images[1]到images[4]之间的图片对象
            if (bangIndex++ % World.BANG_SPEED == 0) {//控制爆炸效果的图片切换速度
                deadIndex++;//爆炸效果图片的下标增1
            }
            //获取images[2]到images[5]之间图片
            //到最后一张图片了
            if (deadIndex == images.length) {
                //修改状态为可以删除了的
                state = REMOVE;
            }
            return img;
        }
        return null;
    }

    /**
     * 重写OutOfBounds()判断是否越界
     * @return  是否越界
     */
    @Override
    public boolean outOfBounds() {
        // 小敌机的y>=窗口的高，即为越界了
        return this.y >= World.HEIGHT;
    }

    /**
     * 重写getScore()得分
     * @return  分数
     */
    @Override
    public int getScore() {
        //让小敌机停止前进
        step = 0;
        //打掉一个小敌机，得1分
        return 1;
    }

}
