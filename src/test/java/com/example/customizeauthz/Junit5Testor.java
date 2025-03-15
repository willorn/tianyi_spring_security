package com.example.customizeauthz;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

// 使用@SpringBootTest注解，并排除数据库相关自动配置
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Junit5Testor {
    @BeforeAll
    public void initDB() {
        System.out.println("正在创建数据库");
    }

    @BeforeEach
    public void initData() {
        System.out.println("正在初始化Test表数据");
    }

    @Test
    @DisplayName("第一次测试")
    public void firstTest() {
        System.out.println("我是第一个测试用例");
    }

    @Test
    @DisplayName("第二次测试")
    public void secondTest() {
        System.out.println("我是第二个测试用例");
    }

    @AfterEach
    public void destoryData() {
        System.out.println("正在销毁Test表数据");
    }

    @AfterAll
    public void destroyDB() {
        System.out.println("正在销毁数据库");
    }
}