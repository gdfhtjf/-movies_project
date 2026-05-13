# src/utils

通用工具函数目录。

## 目录结构

```
utils/
├── format.js    格式化工具（日期、金额、文本截断等）
├── validate.js  校验工具（表单、正则等）
├── storage.js   本地存储封装（localStorage / sessionStorage）
├── dom.js       DOM 操作工具
└── constant.js  常量定义
```

## 使用规范

- 纯函数，无副作用
- 使用 dayjs 进行日期处理
- 错误处理统一格式
