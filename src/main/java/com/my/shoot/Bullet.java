package com.my.shoot;

import java.awt.image.BufferedImage;//专门装图片的

/**
 * 子弹：是飞行物
 *
 * @author ACGkaka
 */
public class Bullet extends AbstractFlyingObject {

    /**
     * 图片
     */
    private static BufferedImage image;

    static {
        image = loadImage("bullet.png");
    }

    /**
     * 移动速度
     */
    private int step;

    /**
     * 构造方法
     *
     * @param x x坐标
     * @param y y坐标
     */
    Bullet(int x, int y) {
        width = image.getWidth();
        height = image.getHeight();
        this.x = x - this.width / 2;
        this.y = y;
        step = 3;
    }

    /**
     * 子弹移动
     */
    @Override
    public void step() {
        //y-向上
        y -= step;
    }

    /**
     * 重写getImage()获取图片
     *
     * @return 图片
     */
    @Override
    public BufferedImage getImage() {
        // 若活着呢，则返回image
        if (isLife()) {
            return image;
            // 若死了，则修改状态为删除
        } else {
            state = REMOVE;
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
        return this.y <= 0;
    }

}
