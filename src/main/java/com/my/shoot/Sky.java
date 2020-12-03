package com.my.shoot;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * 天空
 * @author ACGkaka
 */
public class Sky extends AbstractFlyingObject {
    private static BufferedImage image;

    static {
        image = loadImage("background.png");
    }

    /**
     * 移动速度
     */
    private int step;
    /**
     * 用于天空图片的切换
     */
    private int y1;

    /**
     * 构造方法
     */
    Sky() {
        width = image.getWidth();
        height = image.getHeight();
        x = 0;
        y = 0;
        y1 = -height;
        step = 1;
    }

    /**
     * 天空移动
     */
    @Override
    public void step() {
        y += step;
        y1 += step;
        if (y >= this.height) {
            y = -this.height;
        }
        if (y1 >= this.height) {
            y1 = -this.height;
        }
    }

    /**
     * 重写getImage()获取图片
     * @return 图片
     */
    @Override
    public BufferedImage getImage() {
        // 直接返回image图片
        return image;
    }

    /**
     * 画对象
     * @param g 画笔
     */
    @Override
    public void paint(Graphics g) {
        // 天空需要画两次，所以要调用两次方法
        g.drawImage(getImage(), x, y, null);
        g.drawImage(getImage(), x, y1, null);
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

}
