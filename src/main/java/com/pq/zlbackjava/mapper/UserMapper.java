package com.pq.zlbackjava.mapper;

import com.pq.zlbackjava.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据用户名和密码查询用户
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    /**
     * 插入用户
     * @param user 用户信息
     * @return 影响行数
     */
    int insert(User user);

    /**
     * 更新用户密码
     * @param username 用户名
     * @param password 新密码
     * @return 影响行数
     */
    int updatePassword(@Param("username") String username, @Param("password") String password);

    /**
     * 根据用户名查询安全问题
     * @param username 用户名
     * @return 用户信息（包含question和answer）
     */
    User findSecurityQuestionByUsername(@Param("username") String username);

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    User findById(@Param("id") Integer id);
}

