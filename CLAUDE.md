# Claude 提示词配置文件

#### 代码风格
- 严格参考当前项目风格

#### 开发规范
### 严格按照阿里巴巴Java开发规范
- 本项目 不允许中文日志 中文常量描述
- 主要代码块、方法、设计模式用法等必须添加中文注释
- 不要重复造轮子，实现前先检查当前项目是否已有可复用的功能或设计模式

### 1.1 Service 层规范
- Service 层只负责业务流程编排，不包含具体业务逻辑。
- Service 层的实现根据业务复杂度分为两类：
    - **简单操作**（如单表 CRUD）：直接调用 `Dao` 层完成。
    - **复杂操作**（如注册、登录等）：必须采用**责任链模式**进行编排。
- 复杂业务的执行模式：
    - 初始化业务相关的 `Context`（继承自 `BaseContext`）
    - 调用 `ChainExecutor` 传入 `Context` 和对应的 `Handler` 列表
    - `Handler` 的编排顺序一般遵循：
        1. **加锁防重**（如 `XXXLockHandler`）
        2. **参数与业务校验**（如 `XXXValidationHandler`）
        3. **核心业务逻辑处理**（如 `XXXBizHandler`）
        4. **数据持久化**（如 `XXXProcessHandler` 或 `XXXPersistenceHandler`）
- **注意**：事务范围应尽可能小，例如将 `@Transactional` 加在最终的 `ProcessHandler` 或 `PersistenceHandler` 上，而不是整个 Service 方法，以缩短锁的持有时间。

### 1.2 Biz 层规范
- Biz 层负责具体的业务计算、细粒度业务规则校验、数据转换、缓存管理等核心逻辑。
- Biz 方法专注于单一职责（如 `validateEmail`, `generateVerificationCode`）。
- **定位**：作为底层的可复用业务组件，主要供 `Handler` 层调用，不参与流程编排，也不直接接收 `Context` 参数。
- 所有复杂计算、数据转换、业务规则验证都应在此层实现，以保证逻辑在不同业务线中的高复用性。

### 1.3 Handler 层规范
- Handler 层是设计模式的具体执行单元，每个 Handler 只负责流程中的一个明确步骤。
- 必须实现统一的 `Handler` 接口（如 `AuthHandler<T extends BaseContext>`）。
- 步骤间的数据传递必须通过 `Context` 对象进行，Handler 内部调用 `Biz` 层和 `Dao` 层完成实际动作。

### 1.4 Dao 层规范
- Dao 层是对 `Mapper` 的一层封装，负责基础的数据装配和简单逻辑处理。
- 插入或更新操作时，公共字段（如 `createTime`、`updateTime`）的处理应在 Dao 层完成。
- 调用链应为：`Service/Handler` -> `Dao` -> `Mapper` -> 数据库。

## 9. 业务逻辑规范
### 9.1 幂等性处理
- 所有核心业务方法必须考虑幂等性

### 9.2 事务处理
- 事务边界应清晰明确
- 事务方法命名应体现其业务含义
- 避免长时间持有事务锁
- 事务方法中不能嵌套事务
### 事务规范
- 所有rpc操作都必须在事务外执行
- 除了数据库操作 其他涉及到网络io的操作都必须在事务外执行
- 所有异步的操作都采用最终一致性实现
- 分布式锁要套在事务外层，不可在事务内使用分布式锁


## 3. 项目结构目录规范
- **`biz`**: 存放底层的业务计算、公共的校验方法和业务缓存逻辑等，属于高复用性的核心逻辑层。
- **`constant`**: 存放系统中所有的常量定义，杜绝代码中的魔法值。
- **`context`**: 存放业务编排上下文对象（继承 `BaseContext`），用于在责任链中流转数据。
- **`controller`**: 控制层，处理 HTTP 请求，进行简单的参数绑定与结果封装。
- **`dao`**: 数据访问层，封装 `Mapper` 提供基础的数据持久化方法，并统一维护 `createTime`、`updateTime` 等公共字段。
- **`dos`**: 数据库实体对象（Data Object）定义层，存放与数据库表结构一一对应的实体类。
- **`dto`**: 接口数据传输对象层，分为 `req`（请求参数）和 `rsp`（响应结果）。
- **`bo`** (可选): 业务对象（Business Object）定义层，用于在DTO和DO之间传递包含复杂业务逻辑的数据。
- **`enums`**: 存放系统中的枚举类，管理业务状态、类型等。
- **`exception`**: 存放自定义业务异常（如 `UserNotFoundException`）以及全局异常处理器。
- **`handler`**: 责任链模式的具体处理者实现类，按业务模块划分目录（如 `auth/login`、`auth/register`）。
- **`service`**: 业务编排层，仅负责初始化上下文对象并调度 `ChainExecutor` 或调用 `Dao`，不做具体计算。
- **`strategy`** (可选): 存放策略模式相关接口和实现类。

## 4. 对象模型命名与使用规范

### 4.1 DTO 规范
- 查询参数 DTO：`XXXQueryParam`
- 响应结果 DTO：`XXXResult` 或 `XXXResp`
- 业务数据 DTO：`XXXDTO`
- 业务参数 DTO：`XXXParam`

### 4.2 DTO 字段规范
- 所有 DTO 必须使用 `@Data` 注解
- DTO 中不包含业务方法，只包含数据字段

### 4.3 BO 规范（Business Object）
- **定义**：业务对象（BO）封装了业务逻辑及其所需的数据。
- **使用场景**：如果业务逻辑复杂，Biz 层或 Handler 层需要聚合多个数据源的数据进行复杂的内部处理时，应使用 BO，而非直接使用 DO（DO 仅映射数据库）或 DTO（DTO 仅负责网络传输）。
- **命名规则**：统一以 `BO` 结尾，如 `UserBO`、`OrderContextBO`。
- **特征**：可以包含基础的业务方法（充血模型），或者仅作为跨多个 Biz 模块调用的复杂数据载体。

## 5. 异常处理规范

### 5.1 异常类型
- 统一使用自定义异常类（继承自 `RuntimeException`，如 `UserNotFoundException`）进行业务异常处理。
- 异常码与状态码统一定义在 `ErrorCode` 枚举类中，包含 HTTP Status 和 Message。
- 不得直接抛出原生的 `RuntimeException` 或 `Exception`，必须抛出具体的业务异常。

### 5.2 异常拦截与响应
- 必须使用 `@RestControllerAdvice` 配合 `@ExceptionHandler` 进行全局异常拦截（如 `GlobalExceptionHandler`）。
- 全局异常拦截器需统一封装响应对象为 `ErrorResponse`，确保接口返回格式一致。
- 对参数校验异常（`MethodArgumentNotValidException`）需专门拦截，提取并封装具体的字段错误信息。

## 2. 数据类型规范

### 2.1 金额字段
- 金额相关字段必须使用 `BigDecimal` 类型
- 不得使用 `double`、`float` 或 `long` 等类型处理金额

## 6. 常量规范

### 6.1 魔法值处理
- 代码中不得出现任何魔法值（如字符串字面量、数字字面量等）
- 所有魔法值必须定义为常量并放置在相应的业务常量类中

### 6.2 状态枚举
- 状态字段必须使用枚举类进行维护
- 枚举类:XXXEnum.java


## 7. 数据库设计规范

### 7.1 表结构与 DO 实体类设计
- 数据库表名使用下划线命名法（snake_case）
- 字段名使用下划线命名法（snake_case）
- 实体类统一命名为 `XXXDO`（如 `UserDO`），并放置在 `dos` 目录中，因为 `do` 是 Java 关键字。
- 实体类属性名使用驼峰命名法（camelCase），使用 `@Data` 注解
- 实体类推荐使用 Builder 模式，可以在 `build()` 方法中提供基础的判空或前置校验逻辑
- 必须包含审计字段：`createTime`、`updateTime`，类型统一使用 `LocalDateTime`，在无参构造时可赋予默认值 `LocalDateTime.now()`
- XML 映射文件中字段映射必须与数据库表结构一致

### 7.2 MyBatis 使用规范
- SQL 映射文件放在 `src/main/resources/mapper` 目录
- XML 文件中引用的字段必须在数据库表中存在
- ResultMap 中的 column 必须与数据库表字段名一致
- 不得在 XML 中引用数据库表中不存在的字段

## 11. 常见错误避免

### 11.1 数据库字段不一致
- 确保实体类字段与数据库表字段完全对应
- XML 映射文件中的字段必须在数据库表中存在
- 修改数据库表结构后，同步更新实体类和 XML 映射

### 11.3 状态管理
- 业务状态和审批状态必须使用枚举
- 状态转换逻辑应有明确的校验和控制
- 避免使用字符串直接比较状态值

### 11.4 缓存管理
- 缓存数据必须与数据库数据保持一致




