package com.redis.common.exception;

/**
 * Created by https://github.com/kuangcp on 17-6-12  上午10:24
 * 记录操作的放在这里
 */
public interface NoticeInfo {
    public final String ALREADY_EXIST_POOL = "连接池集合中已经存在，可直接调用：";
    public final String CRETE_POOL = "配置文件中创建连接池:";
    public final String DELETE_POOL_SUCCESS = "清空时删除该连接池配置成功：";
    public final String DELETE_POOL_FAILED = "清空时删除该连接池配置失败：";
}
