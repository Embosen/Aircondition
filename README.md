# Android空调控制应用 - Android 14移植版

## 📱 项目简介

这是一个基于Android平台的空调控制应用，通过串口通信与空调硬件进行数据交互。本项目从Android 6.0成功移植到Android 14，支持现代Android设备的运行。

## 🔧 硬件要求

### 串口设备
- **默认串口路径**: `/dev/ttyS3`
- **波特率**: 9600 bps
- **数据位**: 8位
- **停止位**: 1位
- **校验位**: 无校验
- **权限要求**: 需要root权限修改设备权限

### 支持的设备类型
- 嵌入式Android设备（工控板、车载终端等）
- 支持串口通信的Android设备
- 架构支持：ARM32/ARM64, x86/x86_64

## 📋 移植记录

### 从Android 6.0到Android 14的升级过程

#### 1. 依赖库迁移
- **Android Support库 → AndroidX**
  - `android.support.v7.app.AppCompatActivity` → `androidx.appcompat.app.AppCompatActivity`
  - `android.support.annotation.Nullable` → `androidx.annotation.Nullable`
  - 更新所有相关依赖库到最新版本

#### 2. AndroidManifest.xml更新
- **目标SDK版本**: 从API 23升级到API 34
- **最低SDK版本**: 从API 12升级到API 21
- **权限更新**:
  - 添加Android 13+媒体权限
  - 添加Android 14通知权限
  - 配置存储权限的maxSdkVersion
- **组件配置**:
  - 添加`android:exported`属性
  - 移除已弃用的`package`属性
  - 添加数据提取和备份规则

#### 3. Gradle配置更新
- **Gradle版本**: 8.13
- **Android Gradle Plugin**: 8.12.2
- **Java版本**: 17
- **编译SDK**: API 34
- **目标SDK**: API 34

#### 4. 代码兼容性修复
- 修复switch语句常量表达式问题
- 更新导入语句适配AndroidX
- 修复AndroidManifest.xml中的命名空间问题

## 🛠️ 编译环境要求

### 必需软件
- **Android Studio**: 2023.1.1或更高版本
- **JDK**: 17或更高版本
- **Android SDK**: API 34
- **NDK**: 27.0.12077973或更高版本
- **Gradle**: 8.13

### 环境配置
```properties
# gradle.properties配置
org.gradle.java.home=C:\\Program Files\\Java\\jdk-17
android.sdk.dir=C:\\Users\\[用户名]\\AppData\\Local\\Android\\Sdk
android.ndk.dir=C:\\Users\\[用户名]\\AppData\\Local\\Android\\Sdk\\ndk\\27.0.12077973
```

### 依赖库版本
```gradle
// 核心AndroidX库
implementation 'androidx.appcompat:appcompat:1.7.0'
implementation 'com.google.android.material:material:1.12.0'
implementation 'androidx.core:core:1.13.1'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

// 权限处理
implementation 'androidx.activity:activity:1.9.2'
implementation 'androidx.fragment:fragment:1.8.5'

// 通知支持
implementation 'androidx.work:work-runtime:2.9.1'
```

## 📦 编译步骤

### 1. 环境准备
```bash
# 确保Android SDK和NDK已安装
# 配置local.properties文件
sdk.dir=C:\\Users\\[用户名]\\AppData\\Local\\Android\\Sdk
```

### 2. 清理项目
```bash
./gradlew clean
```

### 3. 编译Debug版本
```bash
./gradlew assembleDebug
```

### 4. 编译Release版本
```bash
./gradlew assembleRelease
```

## 📁 项目结构

```
Aircondition/
├── app/                          # 主应用模块
│   ├── src/main/
│   │   ├── java/                 # Java源码
│   │   │   ├── com/example/chence/Aircondition/
│   │   │   │   ├── Activity/     # 活动类
│   │   │   │   ├── Service/      # 服务类
│   │   │   │   ├── DataUI/       # 数据界面类
│   │   │   │   └── util/         # 工具类
│   │   │   └── android_serialport_api/  # 串口API
│   │   ├── jni/                  # JNI原生代码
│   │   │   ├── SerialPort.c      # 串口C实现
│   │   │   ├── SerialPort.h      # 串口头文件
│   │   │   └── Android.mk        # NDK构建文件
│   │   ├── jniLibs/              # 预编译的.so库
│   │   └── res/                  # 资源文件
│   └── build.gradle              # 应用构建配置
├── controllib/                   # 控制库模块
│   ├── src/main/java/kiyun/
│   │   ├── controllib/           # 控制组件
│   │   └── dataprocess/          # 数据处理
│   └── build.gradle              # 库构建配置
└── build.gradle                  # 项目构建配置
```

## 🔌 串口通信协议

### 数据帧格式
```
帧头: 0xAA 0x55 [长度] 0xFB
数据: [空调控制数据]
帧尾: 0xAA 0x55
```

### 数据包结构
- **帧头**: 4字节（0xAA, 0x55, 长度, 0xFB）
- **数据长度**: 可变（由第3字节指定）
- **数据内容**: 空调状态和控制信息
- **帧尾**: 2字节（0xAA, 0x55）

### 空调数据解析
```java
// 数据解析顺序（从第4字节开始）:
- 开关状态 (1字节)
- 制冷/制热模式 (1字节) 
- 功能数据 (1字节)
- 设定温度 (1字节)
- 当前温度 (4字节，IEEE 754浮点)
- 压缩机运行状态 (1字节)
- 风扇运行状态 (1字节)
- 覆盖数据 (1字节)
```

## ⚙️ 配置说明

### 串口设备配置
应用支持通过配置文件动态修改串口设备：
- **配置文件位置**: `/sdcard/DCIM/SerialPortDev.txt`
- **默认设备**: `/dev/ttyS3`
- **如果配置文件不存在，会自动创建默认配置**

### 权限配置
- 需要root权限来修改串口设备权限
- 使用`chmod 666`设置设备读写权限
- 支持Android 14的新权限模型

## 🚀 安装和使用

### 安装APK
```bash
adb install app-debug.apk
```

### 权限设置
1. 确保设备已root
2. 应用会自动尝试设置串口设备权限
3. 如需要，可手动设置：`chmod 666 /dev/ttyS3`

### 设备配置
1. 在SD卡DCIM目录下创建`SerialPortDev.txt`文件
2. 写入串口设备路径，如：`/dev/ttyS3`
3. 重启应用

## 📊 版本信息

- **应用版本**: 1.0
- **目标Android版本**: 14 (API 34)
- **最低Android版本**: 5.0 (API 21)
- **编译时间**: 2025年10月20日
- **APK大小**: 约6.87MB

## 🔍 故障排除

### 常见问题
1. **串口设备无法打开**
   - 检查设备路径是否正确
   - 确认设备权限设置
   - 验证设备是否被其他进程占用

2. **编译错误**
   - 检查Android SDK和NDK版本
   - 确认Java版本为17
   - 清理项目后重新编译

3. **运行时崩溃**
   - 检查Android版本兼容性
   - 确认权限配置正确
   - 查看logcat日志

## 📝 更新日志

### v1.0 (2025-10-20)
- ✅ 从Android 6.0成功移植到Android 14
- ✅ 迁移Android Support库到AndroidX
- ✅ 更新所有依赖库到最新版本
- ✅ 修复编译错误和兼容性问题
- ✅ 更新权限配置以符合Android 14要求
- ✅ 修改串口设备路径为`/dev/ttyS3`


**注意**: 本应用专为嵌入式Android设备设计，需要串口硬件支持。在普通Android手机上可能无法正常运行。
