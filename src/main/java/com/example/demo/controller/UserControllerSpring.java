package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.SimpleFileUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器 - Spring Boot 版本
 * 提供RESTful风格的增删改查API接口
 */
@RestController
@RequestMapping("/api/users")
public class UserControllerSpring {

    @Autowired
    private SimpleFileUserService userService;

    /**
     * 获取所有用户
     * GET /api/users
     */
    @GetMapping("/getAllUsers")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(successResponse("获取用户列表成功", users));
    }

    /**
     * 根据ID获取用户
     * GET /api/users/{id}
     */
    @GetMapping("/getUserById/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorResponse("用户不存在，ID：" + id));
        }
        return ResponseEntity.ok(successResponse("获取用户成功", user));
    }

    /**
     * 创建用户
     * POST /api/users
     */
    @PostMapping("/createUser")
    public ResponseEntity<Map<String, Object>> createUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam Integer age) {
        try {
            User user = userService.createUser(username, email, age);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(successResponse("创建用户成功", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 更新用户
     * PUT /api/users/{id}
     */
    @PostMapping("/updateUser")
    public ResponseEntity<Map<String, Object>> updateUser(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer age) {
        try {
            User user = userService.updateUser(id, username, email, age);
            return ResponseEntity.ok(successResponse("更新用户成功", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 删除用户
     * DELETE /api/users/{id}
     */
    @PostMapping("/deleteUser")
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestParam(required = false) Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok(successResponse("删除用户成功",
                    Map.of("id", id)));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorResponse("用户不存在，ID：" + id));
        }
    }

    /**
     * 搜索用户
     * GET /api/users/search?q=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUsers(@RequestParam String q) {
        List<User> users = userService.searchUsers(q);
        return ResponseEntity.ok(successResponse("搜索成功，找到 " + users.size() + " 条记录", users));
    }

    /**
     * 获取用户统计信息
     * GET /api/users/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        long totalCount = userService.getUserCount();
        List<User> allUsers = userService.getAllUsers();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", totalCount);
        stats.put("storageInfo", userService.getStorageInfo());

        // 年龄统计
        if (!allUsers.isEmpty()) {
            double avgAge = allUsers.stream()
                    .mapToInt(User::getAge)
                    .average()
                    .orElse(0);
            stats.put("averageAge", String.format("%.1f", avgAge));

            int minAge = allUsers.stream()
                    .mapToInt(User::getAge)
                    .min()
                    .orElse(0);
            stats.put("minAge", minAge);

            int maxAge = allUsers.stream()
                    .mapToInt(User::getAge)
                    .max()
                    .orElse(0);
            stats.put("maxAge", maxAge);
        }

        return ResponseEntity.ok(successResponse("获取统计信息成功", stats));
    }

    /**
     * 批量创建用户
     * POST /api/users/batch
     */
    @PostMapping("/batchCreateUsers/batch")
    public ResponseEntity<Map<String, Object>> batchCreateUsers(@RequestBody List<User> users) {
        try {
            List<User> createdUsers = userService.createUsers(users);
            return ResponseEntity.ok(successResponse(
                    "批量创建成功，共 " + createdUsers.size() + " 条记录", createdUsers));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 批量删除用户
     * DELETE /api/users/batch
     */
    @PostMapping("/batchDeleteUsers/batch")
    public ResponseEntity<Map<String, Object>> batchDeleteUsers(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(errorResponse("ID列表不能为空"));
        }

        int successCount = 0;
        List<String> errors = new java.util.ArrayList<>();

        for (Long id : ids) {
            try {
                if (userService.deleteUser(id)) {
                    successCount++;
                } else {
                    errors.add("ID " + id + "：用户不存在");
                }
            } catch (Exception e) {
                errors.add("ID " + id + "：" + e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", ids.size() - successCount);
        result.put("errors", errors);

        return ResponseEntity.ok(successResponse("批量删除完成", result));
    }

    /**
     * 重新加载数据
     * POST /api/users/reload
     */
    @PostMapping("/reload")
    public ResponseEntity<Map<String, Object>> reloadData() {
        userService.reloadData();
        long count = userService.getUserCount();
        return ResponseEntity.ok(successResponse("数据重新加载成功，共 " + count + " 条记录",
                Map.of("userCount", count, "storageInfo", userService.getStorageInfo())));
    }

    /**
     * 构建成功响应
     */
    private Map<String, Object> successResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    /**
     * 构建错误响应
     */
    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", message);
        return response;
    }
}
