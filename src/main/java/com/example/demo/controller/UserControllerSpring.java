package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.controller.UserCreateRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理API接口
 */
@RestController
@RequestMapping("/api/users")
public class UserControllerSpring {

    private final UserService userService;

    @Autowired
    public UserControllerSpring(UserService userService) {
        this.userService = userService;
    }

    // Helper method for consistent success responses
    private Map<String, Object> successResponse(String message, Object data) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", 200);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    /**
     * 创建用户
     * POST /api/users/createUser
     */
    @PostMapping("/createUser")
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = userService.createUser(request.getUsername(), request.getEmail(), request.getAge());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(successResponse("创建用户成功", user));
    }

    /**
     * 根据ID获取用户
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(successResponse("获取用户成功", user));
    }

    /**
     * 获取所有用户
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(successResponse("获取所有用户成功", users));
    }

    /**
     * 更新用户
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @Valid @RequestBody UserCreateRequest request) {
        User updatedUser = userService.updateUser(id, request.getUsername(), request.getEmail(), request.getAge());
        return ResponseEntity.ok(successResponse("用户更新成功", updatedUser));
    }

    /**
     * 删除用户
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok(successResponse("用户删除成功", null));
        } else {
            // This case might be better handled by UserNotFoundException in UserService
            // For now, return a 404 if not found
            throw new com.example.demo.exception.UserNotFoundException("用户不存在，ID：" + id);
        }
    }
}
