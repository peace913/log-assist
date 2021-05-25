# log-assist
log-assist is a tool to help developers to find where output the logs. Like
```text
2021-05-25 19:52:44.951 5561-5561/com.peace.log.assist.app W/tag: w (scope: PROJECT, name: app, class: com.peace.log.assist.MainActivity.java, method: onCreate, line: 31)
```

### Use
The first, add the plugin in your project root build.gradle.add
```groovy
buildscript {
    repositories {
        jcenter()
        maven { url 'https://jitpack.io'}
    }
    dependencies {
        classpath 'com.github.peace913.log-assist:plugin:1.1.0'
    }
}
```
Then, apply the plugin in your app module.add
```groovy
apply plugin: 'com.peace.log.assist'
```
Last, add the sdk dependence in your app module. If you have many modules, only need add dependence in your app module.add
```groovy
implementation 'com.github.peace913.log-assist:assist:1.1.0'
```
### Config
If you don't need to show sdk info (show default), then
```java
LogAssist.getInstance().showArtifactInfo(false);
```
If you want to control whether show the log, then
```java
LogAssist.getInstance().setLogListener(new LogListener() {
    @Override
    public boolean onLogArrived(int level, String tag, String msg, Throwable throwable, String scope, String sdkName, String className, String methodName, int line) {
        if (level < Log.WARN) {
            return true;
        }
        return false;
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