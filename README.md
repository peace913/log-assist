# log-assist
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