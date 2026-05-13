# src/composables

组合式函数（Composables）目录，按功能域分类。

## 目录结构

```
composables/
├── useAuth.js       认证相关（登录态、权限）
├── useRequest.js    请求封装（加载、错误、重试）
├── usePagination.js 分页逻辑
├── useForm.js       表单逻辑
├── useTheme.js      主题切换
└── useStorage.js    本地存储封装
```

## 使用规范

- 文件名以 `use` 开头，驼峰命名
- 函数返回 ref / reactive 响应式数据
- 优先使用 @vueuse/core 提供的基础 composable
