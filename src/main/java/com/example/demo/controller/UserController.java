package com.example.demo.controller;

import com.example.demo.dos.UserDO;
import com.example.demo.dto.req.LoginParam;
import com.example.demo.dto.req.RegisterParam;
import com.example.demo.dto.req.SendCodeParam;
import com.example.demo.dto.req.UserReqDTO;
import com.example.demo.dto.rsp.UserRspDTO;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户
     * GET /api/users
     */
    @GetMapping("/getAllUsers")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<UserDO> users = userService.getAllUsers();
        List<UserRspDTO> rspList = users.stream().map(this::toRspDTO).toList();
        return ResponseEntity.ok(successResponse("获取用户列表成功", rspList));
    }

    /**
     * 根据ID获取用户
     * GET /api/users/{id}
     */
    @GetMapping("/getUserById/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        UserDO user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorResponse("用户不存在，ID：" + id));
        }
        return ResponseEntity.ok(successResponse("获取用户成功", toRspDTO(user)));
    }

    /**
     * 创建用户
     * POST /api/users
     */
    @PostMapping("/createUser")
    public ResponseEntity<Map<String, Object>> createUser() {
        return ResponseEntity.status(HttpStatus.GONE)
                .body(errorResponse("原新增用户功能已下线，请使用 /register 完成注册"));
    }

    @PostMapping("/register/sendCode")
    public ResponseEntity<Map<String, Object>> sendRegisterCode(@RequestBody SendCodeParam param) {
        try {
            String code = userService.sendRegisterCode(param.getEmail());
            return ResponseEntity.ok(successResponse("验证码已发送（模拟）", Map.of(
                    "email", param.getEmail(),
                    "mockCode", code
            )));
        } catch (IllegalArgumentException e) {
            log.warn("发送注册验证码参数异常, email={}", param.getEmail(), e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("发送注册验证码状态异常, email={}", param.getEmail(), e);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 邮箱验证码注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterParam param) {
        try {
            UserDO user = userService.registerByEmail(
                    param.getUsername(),
                    param.getEmail(),
                    param.getPassword(),
                    param.getVerificationCode()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(successResponse("注册成功", toRspDTO(user)));
        } catch (IllegalArgumentException e) {
            log.warn("注册参数异常, email={}", param.getEmail(), e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("注册状态异常, email={}", param.getEmail(), e);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 邮箱密码登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginParam param) {
        try {
            UserDO user = userService.loginByEmail(param.getEmail(), param.getPassword());
            return ResponseEntity.ok(successResponse("登录成功", toRspDTO(user)));
        } catch (IllegalArgumentException e) {
            log.warn("登录参数异常, email={}", param.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("登录状态异常, email={}", param.getEmail(), e);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 获取个人主页信息
     */
    @GetMapping("/profile/{id}")
    public ResponseEntity<Map<String, Object>> getProfile(@PathVariable Long id) {
        UserDO user = userService.getUserById(id);
        return ResponseEntity.ok(successResponse("获取个人信息成功", toRspDTO(user)));
    }

    /**
     * 更新用户
     * PUT /api/users/{id}
     */
    @PostMapping("/updateUser")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody UserReqDTO request) {
        try {
            UserDO user = userService.updateUser(request.getId(), request.getUsername(), request.getEmail(), request.getAge());
            return ResponseEntity.ok(successResponse("更新用户成功", toRspDTO(user)));
        } catch (IllegalArgumentException e) {
            log.warn("更新用户参数异常, id={}", request.getId(), e);
            return ResponseEntity.badRequest()
                    .body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 删除用户
     * DELETE /api/users/{id}
     */
    @PostMapping("/deleteUser/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
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
        List<UserDO> users = userService.searchUsers(q);
        List<UserRspDTO> rspList = users.stream().map(this::toRspDTO).toList();
        return ResponseEntity.ok(successResponse("搜索成功，找到 " + rspList.size() + " 条记录", rspList));
    }


    /**
     * 批量创建用户
     * POST /api/users/batch
     */
    @PostMapping("/batchCreateUsers/batch")
    public ResponseEntity<Map<String, Object>> batchCreateUsers(@RequestBody List<UserDO> users) {
        try {
            List<UserDO> createdUsers = userService.createUsers(users);
            List<UserRspDTO> rspList = createdUsers.stream().map(this::toRspDTO).toList();
            return ResponseEntity.ok(successResponse(
                    "批量创建成功，共 " + rspList.size() + " 条记录", rspList));
        } catch (IllegalArgumentException e) {
            log.warn("批量创建参数异常, size={}", users == null ? 0 : users.size(), e);
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
                log.error("批量删除异常, id={}", id, e);
                errors.add("ID " + id + "：" + e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", ids.size() - successCount);
        result.put("errors", errors);

        return ResponseEntity.ok(successResponse("批量删除完成", result));
    }


    private UserRspDTO toRspDTO(UserDO user) {
        UserRspDTO dto = new UserRspDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        return dto;
    }
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
