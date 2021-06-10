# log-assist
log-assist通过对App输出的每条日志增加一些辅助信息，让开发者可以更方便的定位到是哪里输出的日志. 输出的日志格式如下：
```text
2021-05-25 19:52:44.951 5561-5561/com.peace.log.assist.app W/tag: w (scope: PROJECT, name: app, class: com.peace.log.assist.MainActivity.java, method: onCreate, line: 31)
```
其中各字段含义如下：
+ scope: 输出日志的范围，包含三类，PROJECT（app）、MODULE（module）、JAR（第三方SDK）
+ name: 对应的module名字或者sdk的名字
+ class: 输出日志的类名
+ method: 输出日志的方法名
+ line: 输出日志对应的代码行号

### 使用
首先，在工程的根目录下的build.gradle文件中增加插件的依赖。
```groovy
buildscript {
    repositories {
        jcenter()
        maven { url 'https://jitpack.io'} //需要增加maven库
    }
    dependencies {
        classpath 'com.github.peace913.log-assist:plugin:1.2.0' //增加assist的依赖
    }
}

allprojects {
    repositories {
        maven { url 'https://jitpack.io'} //需要增加maven库
        google()
        jcenter()
    }
}
```
然后，在app目录下的build.gradle中应用插件。
```groovy
apply plugin: 'com.peace.log.assist'
```
最后，在app目录下的build.gradle中增加assist sdk的依赖。如果你的工程有很多module，仅需要在app目录下增加依赖。
```groovy
implementation 'com.github.peace913.log-assist:assist:1.2.0'
```
### 设置
如果你不需要现实scope信息(默认显示), 需要下面接口来开启/关闭。
```java
LogAssist.getInstance().showArtifactInfo(false);
```
如果你需要控制增加辅助信息的日志级别(默认是Log.VERBOSE), 需要下面接口来设置。
```java
LogAssist.getInstance().setAssistLogLevel(Log.WARN); //仅WARN及以上日志会增加辅助信息
```
sdk还提供了LogListener接口来方便您控制日志的输出。
如果你需要控制日志按日志级别显示，可以参考如下代码，仅输出WARN及以上级别的日志。
```java
LogAssist.getInstance().setLogListener(new LogListener() {
    @Override
    public boolean onLogArrived(int level, String tag, String msg, Throwable throwable, String scope, String sdkName, String className, String methodName, int line) {
        //返回true表示开发者自己消费了这条日志，返回false表示由sdk消费这条日志。
        if (level < Log.WARN) {
            return true;
        }
        return false;
    }
});
```
如果你需要关闭第三方SDK输出的所有日志，可以参考如下代码。
```java
LogAssist.getInstance().setLogListener(new LogListener() {
    @Override
    public boolean onLogArrived(int level, String tag, String msg, Throwable throwable, String scope, String sdkName, String className, String methodName, int line) {
        //返回true表示开发者自己消费了这条日志，返回false表示由sdk消费这条日志。
        return scope == Constants.SCOPE_JAR;
    }
});
```

## License
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.