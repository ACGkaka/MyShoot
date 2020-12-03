package com.my.shoot;

/**
 * 奖励接口
 *
 * @author ACGkaka
 */
public interface Award {
    int DOUBLE_FIRE = 0;
    int LIFE = 1;

    /**
     * 获取奖励类型（0或1）
     *
     * @return 0/1
     */
    int getType();
}
