package com.example.demo.dao;

import com.example.demo.entity.User;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

/**
 * 基于JSON文件的User数据访问对象（DAO）- 纯Java实现
 * 不依赖第三方JSON库，使用简单的JSON格式进行持久化
 */
@Repository
public class JsonFileUserDao {
    private static final String DATA_DIR = "data";
    private static final String USER_DATA_FILE = DATA_DIR + "/users.json";
    private static final String BACKUP_DIR = DATA_DIR + "/backup";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Map<Long, User> userMap = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public JsonFileUserDao() {
        initialize();
    }

    /**
     * 初始化数据
     */
    private void initialize() {
        try {
            // 创建数据目录
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }

            // 创建备份目录
            Path backupPath = Paths.get(BACKUP_DIR);
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
            }

            // 尝试从文件加载数据
            File dataFile = new File(USER_DATA_FILE);
            if (dataFile.exists()) {
                loadDataFromFile();
                updateIdGenerator();
                System.out.println("数据已从文件加载：" + userMap.size() + " 条记录");
            } else {
                // 文件不存在，创建初始数据
                createInitialData();
                saveDataToFile();
                System.out.println("初始数据已创建并保存");
            }
        } catch (IOException e) {
            System.err.println("初始化失败：" + e.getMessage());
            userMap.clear();
        }
    }

    /**
     * 创建初始测试数据
     */
    private void createInitialData() {
        saveInternal(new User("张三", "zhangsan@example.com", 25));
        saveInternal(new User("李四", "lisi@example.com", 30));
        saveInternal(new User("王五", "wangwu@example.com", 28));
    }

    /**
     * 从文件加载数据
     */
    private void loadDataFromFile() {
        try {
            File dataFile = new File(USER_DATA_FILE);
            if (dataFile.length() == 0) {
                userMap.clear();
                return;
            }

            String content = new String(Files.readAllBytes(dataFile.toPath()), "UTF-8");
            if (content.trim().isEmpty()) {
                userMap.clear();
                return;
            }

            // 解析JSON数组
            if (content.startsWith("[") && content.endsWith("]")) {
                String arrayContent = content.substring(1, content.length() - 1).trim();
                if (arrayContent.isEmpty()) {
                    return;
                }

                // 简单的JSON解析器
                String[] userJsons = splitJsonObjects(arrayContent);
                System.out.println("解析到 " + userJsons.length + " 个用户JSON对象");
                for (String userJson : userJsons) {
                    User user = parseUserFromJson(userJson.trim());
                    if (user != null) {
                        userMap.put(user.getId(), user);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("加载数据失败：" + e.getMessage());
            e.printStackTrace();
            userMap.clear();
        }
    }

    /**
     * 分割JSON对象数组
     */
    private String[] splitJsonObjects(String arrayContent) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int start = 0;

        for (int i = 0; i < arrayContent.length(); i++) {
            char c = arrayContent.charAt(i);
            if (c == '{') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    objects.add(arrayContent.substring(start, i + 1));
                }
            }
        }

        return objects.toArray(new String[0]);
    }

    /**
     * 从JSON字符串解析用户对象
     */
    private User parseUserFromJson(String json) {
        try {
            User user = new User();

            // 简化解析逻辑：直接使用字符串匹配来提取字段值
            String idValue = extractValueAsString(json, "id");
            String usernameValue = extractValueAsString(json, "username");
            String emailValue = extractValueAsString(json, "email");
            String ageValue = extractValueAsString(json, "age");
            String createTimeValue = extractValueAsString(json, "createTime");
            String updateTimeValue = extractValueAsString(json, "updateTime");

            if (idValue != null) {
                user.setId(parseLong(idValue));
            }
            if (usernameValue != null) {
                user.setUsername(removeQuotes(usernameValue));
            }
            if (emailValue != null) {
                user.setEmail(removeQuotes(emailValue));
            }
            if (ageValue != null) {
                user.setAge(parseInt(ageValue));
            }
            if (createTimeValue != null) {
                user.setCreateTime(parseDateTime(removeQuotes(createTimeValue)));
            }
            if (updateTimeValue != null) {
                user.setUpdateTime(parseDateTime(removeQuotes(updateTimeValue)));
            }

            if (user.getId() != null) {
                return user;
            } else {
                System.err.println("解析用户JSON失败，缺少id字段：" + json);
                return null;
            }
        } catch (Exception e) {
            System.err.println("解析用户JSON失败：" + json + ", 错误：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从JSON字符串中提取字段值（不包含引号）
     */
    private String extractValueAsString(String json, String fieldName) {
        String pattern = "\"" + fieldName + "\":";
        int index = json.indexOf(pattern);
        if (index == -1) {
            return null;
        }

        int valueStart = index + pattern.length();
        String valuePart = json.substring(valueStart).trim();

        // 如果是数字类型（如id, age），直接找到逗号或结束
        if (fieldName.equals("id") || fieldName.equals("age")) {
            int commaIndex = valuePart.indexOf(",");
            int braceIndex = valuePart.indexOf("}");
            int endIndex = Math.min(commaIndex == -1 ? Integer.MAX_VALUE : commaIndex,
                                  braceIndex == -1 ? Integer.MAX_VALUE : braceIndex);
            if (endIndex != Integer.MAX_VALUE) {
                return valuePart.substring(0, endIndex).trim();
            }
            return valuePart.trim();
        }

        // 对于字符串类型，找到下一个引号
        if (valuePart.startsWith("\"")) {
            int closingQuoteIndex = -1;
            int i = 1; // 跳过开始的引号
            while (i < valuePart.length()) {
                char c = valuePart.charAt(i);
                if (c == '\\' && i + 1 < valuePart.length()) {
                    i += 2; // 跳过转义字符
                    continue;
                }
                if (c == '"') {
                    closingQuoteIndex = i;
                    break;
                }
                i++;
            }
            if (closingQuoteIndex != -1) {
                return valuePart.substring(0, closingQuoteIndex + 1);
            }
        }

        return valuePart;
    }

    /**
     * 移除引号
     */
    private String removeQuotes(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    /**
     * 解析长整型
     */
    private Long parseLong(String value) {
        try {
            return Long.parseLong(value.replace(",", "").trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 解析整数
     */
    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value.replace(",", "").trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 解析日期时间
     */
    private LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value, DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 保存数据到文件
     */
    private void saveDataToFile() {
        try {
            // 先备份
            backupData();

            // 保存新数据
            StringBuilder json = new StringBuilder("[\n");
            List<User> users = new ArrayList<>(userMap.values());
            for (int i = 0; i < users.size(); i++) {
                json.append("  ").append(userToJson(users.get(i)));
                if (i < users.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            json.append("]");

            Files.write(Paths.get(USER_DATA_FILE), json.toString().getBytes("UTF-8"));
        } catch (IOException e) {
            System.err.println("保存数据失败：" + e.getMessage());
            throw new RuntimeException("数据持久化失败", e);
        }
    }

    /**
     * 将用户对象转换为JSON字符串
     */
    private String userToJson(User user) {
        return String.format(
                "{\"id\":%d,\"username\":\"%s\",\"email\":\"%s\",\"age\":%d,\"createTime\":\"%s\",\"updateTime\":\"%s\"}",
                user.getId(),
                escapeJson(user.getUsername()),
                escapeJson(user.getEmail()),
                user.getAge(),
                user.getCreateTime().format(DATE_FORMATTER),
                user.getUpdateTime().format(DATE_FORMATTER)
        );
    }

    /**
     * 转义JSON特殊字符
     */
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                     .replace("\"", "\\\"")
                     .replace("\n", "\\n")
                     .replace("\r", "\\r")
                     .replace("\t", "\\t");
    }

    /**
     * 备份数据
     */
    private void backupData() throws IOException {
        File dataFile = new File(USER_DATA_FILE);
        if (dataFile.exists()) {
            String timestamp = LocalDateTime.now()
                    .toString()
                    .replace(":", "-")
                    .replace(".", "-");
            File backupFile = new File(BACKUP_DIR + "/users_backup_" + timestamp + ".json");
            Files.copy(dataFile.toPath(), backupFile.toPath());
            cleanOldBackups();
        }
    }

    /**
     * 清理旧备份文件
     */
    private void cleanOldBackups() {
        try {
            File backupDir = new File(BACKUP_DIR);
            File[] backups = backupDir.listFiles((dir, name) -> name.startsWith("users_backup_"));
            if (backups != null && backups.length > 10) {
                Arrays.sort(backups, Comparator.comparingLong(File::lastModified));
                for (int i = 0; i < backups.length - 10; i++) {
                    backups[i].delete();
                }
            }
        } catch (Exception e) {
            System.err.println("清理备份失败：" + e.getMessage());
        }
    }

    /**
     * 更新ID生成器
     */
    private void updateIdGenerator() {
        OptionalLong maxId = userMap.values().stream()
                .mapToLong(User::getId)
                .max();
        if (maxId.isPresent()) {
            idGenerator.set(maxId.getAsLong() + 1);
        }
    }

    /**
     * 内部保存方法（不触发持久化）
     */
    private User saveInternal(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        if (user.getCreateTime() == null) {
            user.setCreateTime(LocalDateTime.now());
        }
        if (user.getUpdateTime() == null) {
            user.setUpdateTime(LocalDateTime.now());
        }
        userMap.put(user.getId(), user);
        return user;
    }

    /**
     * 创建用户
     */
    public User save(User user) {
        saveInternal(user);
        saveDataToFile();
        return user;
    }

    /**
     * 根据ID查询用户
     */
    public User findById(Long id) {
        return userMap.get(id);
    }

    /**
     * 查询所有用户
     */
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    /**
     * 更新用户
     */
    public User update(User user) {
        if (!userMap.containsKey(user.getId())) {
            return null;
        }
        user.setUpdateTime(LocalDateTime.now());
        userMap.put(user.getId(), user);
        saveDataToFile();
        return user;
    }

    /**
     * 根据ID删除用户
     */
    public boolean deleteById(Long id) {
        User removed = userMap.remove(id);
        if (removed != null) {
            saveDataToFile();
            return true;
        }
        return false;
    }

    /**
     * 根据用户名查询
     */
    public List<User> findByUsername(String username) {
        return userMap.values().stream()
                .filter(user -> user.getUsername() != null && user.getUsername().contains(username))
                .collect(Collectors.toList());
    }

    /**
     * 根据邮箱查询
     */
    public User findByEmail(String email) {
        return userMap.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 统计用户数量
     */
    public long count() {
        return userMap.size();
    }

    /**
     * 判断用户是否存在
     */
    public boolean existsById(Long id) {
        return userMap.containsKey(id);
    }

    /**
     * 批量保存用户
     */
    public List<User> saveAll(List<User> users) {
        for (User user : users) {
            saveInternal(user);
        }
        saveDataToFile();
        return users;
    }

    /**
     * 删除所有用户
     */
    public void deleteAll() {
        userMap.clear();
        saveDataToFile();
    }

    /**
     * 重新加载数据
     */
    public void reload() {
        loadDataFromFile();
        updateIdGenerator();
    }

    /**
     * 获取数据文件大小（字节）
     */
    public long getDataFileSize() {
        File dataFile = new File(USER_DATA_FILE);
        return dataFile.exists() ? dataFile.length() : 0;
    }

    /**
     * 获取备份文件数量
     */
    public int getBackupCount() {
        File backupDir = new File(BACKUP_DIR);
        File[] backups = backupDir.listFiles((dir, name) -> name.startsWith("users_backup_"));
        return backups != null ? backups.length : 0;
    }
}
