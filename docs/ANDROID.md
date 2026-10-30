# Android 测试壳（实验性）

Peach 当前没有独立 Android 后端。这个壳应用只提供一个 WebView 容器，用来连接你已部署的 Peach 服务（桌面端或 Docker/NAS）。

## 能做什么

- 启动后加载你配置的 Peach 地址（`http://` 或 `https://`）。
- 菜单可修改服务地址并立即重载。
- 菜单支持手动刷新，系统返回键优先回退网页历史。

## 构建要求

- Android Studio（最新稳定版）或 Android SDK + 命令行构建环境
- JDK 17
- Android SDK Platform 35

## 从源码构建 APK

```bash
cd /home/runner/work/peach/peach/android
./gradlew assembleDebug
```

产物路径：

`/home/runner/work/peach/peach/android/app/build/outputs/apk/debug/app-debug.apk`

## 首次使用

1. 安装并打开 APK。
2. 右上角菜单选择「设置服务器地址」。
3. 输入可访问的 Peach 地址（例如局域网地址 `http://192.168.1.5:18888`）。
4. 保存后即可进入 Peach 页面。

## 当前限制

- 这是 WebView 壳，不含本地刮削、托盘、后台服务与桌面打包能力。
- 默认允许明文 HTTP，适合局域网测试；公网建议反向代理 + HTTPS。
