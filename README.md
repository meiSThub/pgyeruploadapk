Apk打包完成后，上次Apk到蒲公英平台，并推送下载链接到企业微信。

## 使用

1. 在 `<project>/build.gradle` 文件中，增加如下代码：

   ```groovy
   buildscript {
       repositories {
           ...
           maven { url 'https://jitpack.io' }
       }
       dependencies {
           ....
           // 引入插件
           classpath 'com.github.meiSThub:pgyeruploadapk:v1.0.2'
       }
   }
   
   allprojects {
       repositories {
           ...
           maven { url 'https://jitpack.io' }
       }
   }
   ```

2. 在 `<project>/app-module/build.gradle` 文件中，使用插件

   ```groovy
   apply plugin: 'com.plum.pgyer.plugin' // 应用插件
   
   pgyer {
       apiKey = "xxx" // 蒲公英平台的: _api_key
       wechatHook {
           messageKey = "xxx" // 企业微信群中，添加机器人时生成的webhookurl中的key
           msgType = 4 // 推送消息的类型，0：templateCard，1：news，2：image，3：markdown，4：markdown+image
       }
   }
   ```

​	其他配置可以参考：[PgyerExtension](https://github.com/meiSThub/pgyeruploadapk/blob/master/pgyerplugin/src/main/groovy/com/plum/pgyer/plugin/bean/PgyerExtension.groovy)

​	经过上面两步，配置就完成了。