package com.hims.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具类
 */
public class PasswordUtil {
    
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    /**
     * 加密密码
     */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    /**
     * 验证密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * 生成密码
     */
    public static void main(String[] args) {
        String password = "admin123";
        String encodedPassword = encode(password);
        System.out.println("密码: " + password);
        System.out.println("加密后: " + encodedPassword);
        System.out.println("验证: " + matches(password, encodedPassword));
        
        // 检查特定密码是否匹配数据库密码
        String dbPassword = "$10$X/hX6JZV9U91dHjRMJJuxe6.eSZvEz7bHcNREcIHvhHEpVnGNnKXi";
        System.out.println("与数据库密码匹配: " + matches(password, dbPassword));
    }
} 