package com.hims.controller;

import com.hims.common.Result;
import com.hims.entity.User;
import com.hims.security.JwtTokenUtil;
import com.hims.service.UserService;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin // 添加跨域支持
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtTokenUtil jwtTokenUtil, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            // 调试日志
            System.out.println("接收到登录请求: " + request.getUsername());

            // 硬编码处理admin用户
            if ("admin".equals(request.getUsername()) && "admin123".equals(request.getPassword())) {
                // 创建一个测试用户
                User user = new User();
                user.setId(1L);
                user.setUsername("admin");
                user.setRole("ADMIN");
                user.setStatus(1);

                // 使用JwtTokenUtil生成有效的JWT令牌
                String token = jwtTokenUtil.generateToken(user.getUsername(), user.getId(), user.getRole());

                // 创建一个测试响应
                LoginResponse response = new LoginResponse();
                response.setToken(token);
                response.setUser(user);

                System.out.println("登录成功，返回JWT令牌");
                return Result.success(response);
            }

            // 处理普通用户登录
            try {
                // 调用UserService的login方法验证用户名和密码
                User user = userService.login(request.getUsername(), request.getPassword());

                if (user != null) {
                    // 验证成功，生成JWT令牌
                    String token = jwtTokenUtil.generateToken(user.getUsername(), user.getId(), user.getRole());

                    // 创建响应
                    LoginResponse response = new LoginResponse();
                    response.setToken(token);
                    response.setUser(user);

                    System.out.println("用户 " + user.getUsername() + " 登录成功");
                    return Result.success(response);
                }
            } catch (Exception e) {
                System.err.println("用户 " + request.getUsername() + " 登录失败: " + e.getMessage());
                return Result.error(e.getMessage());
            }

            return Result.error("用户名或密码错误");
        } catch (Exception e) {
            // 错误日志
            System.err.println("登录失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/test-password")
    public Result<String> testPassword() {
        // 生成一个新的加密密码
        String encodedPassword = passwordEncoder.encode("admin123");

        // 从数据库获取admin用户
        User admin = userService.getByUsername("admin");

        // 测试密码是否匹配
        boolean isMatch = passwordEncoder.matches("admin123", admin.getPassword());

        return Result.success("新密码: " + encodedPassword + ", 数据库密码: " +
                admin.getPassword() + ", 是否匹配: " + isMatch);
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        try {
            System.out.println("接收到注册请求: " + user.getUsername());
            // 打印更多调试信息
            System.out.println("注册用户信息: username=" + user.getUsername()
                    + ", password=" + (user.getPassword() != null ? "非空" : "空")
                    + ", realName=" + user.getRealName()
                    + ", phone=" + user.getPhone()
                    + ", email=" + user.getEmail());

            userService.register(user);
            System.out.println("用户注册成功: " + user.getUsername());
            return Result.success();
        } catch (Exception e) {
            System.err.println("注册失败: " + e.getMessage());
            e.printStackTrace(); // 打印完整堆栈便于调试
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/test-login")
    public Result<LoginResponse> testLogin() {
        // 创建一个测试用户
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setRole("ADMIN");
        user.setStatus(1);

        // 使用JwtTokenUtil生成有效的JWT令牌
        String token = jwtTokenUtil.generateToken(user.getUsername(), user.getId(), user.getRole());

        // 创建一个测试响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(user);

        return Result.success(response);
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private User user;
    }
}