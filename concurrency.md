# 高并发checkList

## 一、Java 代码层
### 1. 缓存相关
- [ ] 所有热点数据（商品/部门/用户/权限/配置/字典）先查 Caffeine → Redis → DB
- [ ] Caffeine 必须设置 TTL（3~10 分钟）+ maximumSize（1~10 万）
- [ ] Redis 缓存穿透返回空对象占位（TTL 30 秒~5 分钟）
- [ ] Redis 缓存雪崩加随机过期时间（baseTTL + Random(0~300)秒）
- [ ] 写完 DB 立即删 Redis + 延迟双删（延迟 1~2 秒再删一次）

### 2. 接口&并发控制
- [ ] 所有接口加 @SentinelResource 限流熔断（单机限流 15000~20000）
- [ ] 核心写接口加 Redisson 分布式锁（库存/订单/重复提交）
- [ ] 热点 key 加本地锁（Caffeine.get(key, k -> loadFromDb) 天然防穿透）
- [ ] 接口完全无状态（禁止 static/成员变量存业务数据）
- [ ] 所有写操作异步化（写库/发MQ/写日志 全 @Async("ioExecutor")）
- [ ] 自定义 IO 线程池（核心100，最大500，队列2000，CallerRunsPolicy）

### 3. 调用&返回
- [ ] 所有外部调用强制超时（Feign/RestTemplate/Redis/DB ≤ 500ms/1000ms）
- [ ] 返回对象只包含必须字段（禁止返回实体全字段、大 List）
- [ ] 统一异常处理（熔断/超时/异常返回业务码 + 降级数据）

### 4. 线程安全&资源清理
- [ ] 登录用户用 ThreadLocal 存（拦截器 finally 必须 remove()）
- [ ] 禁止日志打印大对象（禁止 log.info("result:{}", bigList)）
- [ ] Logback 必须用 AsyncAppender
- [ ] 线程池队列满时用 CallerRunsPolicy（防止 OOM）

### 5. 启动&预热
- [ ] 核心接口加 @PostConstruct 缓存预热（顶级部门/配置/权限）
- [ ] 管理接口（如刷新缓存）加独立线程池

## 二、MySQL & SQL 层
### 1. 查询类（读性能决定上限）
- [ ] 禁止 select *
- [ ] 所有 where/order by/group by 字段必须有索引
- [ ] 联合索引严格遵守左前缀原则（最频繁条件放最左）
- [ ] 禁止在索引列使用函数（DATE(create_time)、SUBSTRING(name,1,3)）
- [ ] 禁止隐式类型转换（varchar 字段用数字比较）
- [ ] 禁止 or 条件（改用 union 或 in）
- [ ] 禁止 != / not in / is null / like '%xx%' / like '%xx'
- [ ] 大分页强制改成“id > lastId + limit”（前端必须传 lastId）
- [ ] 禁止 order by rand()
- [ ] 禁止 count(*) 无 where（必须加状态条件）

### 2. 写操作类（写慢会拖垮整个服务）
- [ ] 批量 insert 必须用 <foreach>（一次 500~1000 条）
- [ ] 批量 update 必须用 CASE WHEN（一次改 500~1000 条）
- [ ] 禁止循环单条 insert/update/delete
- [ ] 写操作必须加唯一约束防重复（业务关键字段）
- [ ] 事务范围极小（只包必要的 SQL）
- [ ] 禁止跨库 join、复杂多表 join
- [ ] 热点表该分库分表的提前提（日千万级订单必须分）
- [ ] 写完 DB 立即删除相关 Redis 缓存（同步 + 延迟双删）