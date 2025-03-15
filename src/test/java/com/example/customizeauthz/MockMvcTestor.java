package com.example.customizeauthz;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;

// 使用AutoConfigureMockMvc注解来自动配置MockMvc
// 使用SpringBootTest注解来启动Spring Boot测试环境，并指定要加载的应用类以及Web环境设置为随机端口
@AutoConfigureMockMvc
@SpringBootTest(classes = {CustomizeAuthzApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockMvcTestor {
    @Resource
    protected MockMvc mockMvc;

    // 使用Test注解来标记测试方法，DisplayName注解来设置测试方法的显示名称
    @Test
    @DisplayName("文本响应测试用例")
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testMock1() throws Exception {
        mockMvc.perform(
                        // 使用MockMvcRequestBuilders来构建HTTP GET请求，并指定请求的URL为"/logger"
                        MockMvcRequestBuilders.get("/aa/logger")
                )
                // 使用andExpect方法来添加对响应状态的期望，这里我们期望状态码为200
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 使用andExpect方法来添加对响应内容的期望，这里我们期望响应的内容为"SUCCESS"
                .andExpect(MockMvcResultMatchers.content().string("SUCCESS"))
                // 使用andDo方法来添加额外的处理器，这里我们添加了一个打印响应结果的处理器
                .andDo(MockMvcResultHandlers.print());
    }

    // 使用Test注解来标记测试方法，DisplayName注解来设置测试方法的显示名称
    @Test
    @DisplayName("JSON响应测试用例")
    @WithMockUser(username = "testuser", roles = {"USER"}) // emulate running with a mocked user.
    public void testMock2() throws Exception {
        // 调用mockMvc的perform方法来执行HTTP请求，并获取MvcResult实例
        MvcResult mrcresult = mockMvc.perform(
                        // 使用MockMvcRequestBuilders来构建HTTP GET请求，并指定请求的URL为"/json"
                        MockMvcRequestBuilders.get("/aa/json")
                                // 使用param方法来添加请求参数，这里我们添加了两个参数：phone和pwd
                                .param("phone", "875-991872")
                                .param("pwd", "123456")
                                // 设置请求的内容类型为application/x-www-form-urlencoded，这是发送表单数据时的常用内容类型
                                .content(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                )
                // 使用andExpect方法来添加对响应状态的期望，这里我们期望状态码为200
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 使用andDo方法来添加额外的处理器，这里我们添加了一个打印响应结果的处理器
                .andDo(MockMvcResultHandlers.print())
                // 使用andExpect方法来添加对响应JSON的期望，这里我们期望JSON中的pwd字段的值为"123456"
                .andExpect(MockMvcResultMatchers.jsonPath("pwd").value("123456"))
                // 使用andReturn方法来返回MvcResult实例，这里我们将其保存在mrcresult变量中，但在此测试案例中并未使用到该变量
                .andReturn();
        String responseContent = mrcresult.getResponse().getContentAsString();
        System.out.println("响应内容: " + responseContent);
        int status = mrcresult.getResponse().getStatus();
        System.out.println("响应状态码: " + status);
    }
}