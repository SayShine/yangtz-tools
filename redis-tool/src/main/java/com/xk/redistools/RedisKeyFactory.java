package com.xk.redistools;

/**
 * @author xiongkai
 * @version 1.0
 * @date 2021-03-26 10:15
 * redis获取键值工具类
 */
public enum RedisKeyFactory {

    /**
     * 测试key值
     */
    TEST_KEY("测试"),
    ;

    /**
     * 应用前缀
     */
    private static final String SPACE = "XkService";

    /**
     * 键值连接符
     */
    private static final String SEPARATOR = "_";

    /**
     * 描述
     */
    String desc;

    RedisKeyFactory(String desc) {
        this.desc = desc;
    }

    public String join(Object... args) {
        StringBuilder key = new StringBuilder(SPACE).append(SEPARATOR).append(super.toString());
        for (Object arg :
                args) {
            key.append(SEPARATOR).append(arg);
        }
        return key.toString();
    }

    @Override
    public String toString() {
        return SPACE + SEPARATOR + this.name();
    }
}
