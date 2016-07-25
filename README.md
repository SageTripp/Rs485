# Rs485
[![Release](https://jitpack.io/v/SageTripp/Rs485.svg)](https://jitpack.io/#SageTripp/Rs485)

Rs485通信

### 使用方法
* `gradle`方式
    - ① 在项目的`build.gradle`文件中添加如下代码
    
        ```groovy
        allprojects {
            repositories {
                ...
                maven { url "https://jitpack.io" }
            }
        }
        ```

    - ② 在`dependency`添加
            
        ```groovy 
        dependencies {
            compile 'com.github.SageTripp:Rs485:0.1.2'
            }
        ```
        
* `maven`方式
    - ① 在项目的`build.gradle`文件中添加如下代码
    
        ```xml
        <repositories>
        	<repository>
        		<id>jitpack.io</id>
        		<url>https://jitpack.io</url>
        	</repository>
        </repositories>
        ```

    - ② 在dependency添加
            
        ```xml
        <dependency>
        	<groupId>com.github.SageTripp</groupId>
        	<artifactId>Rs485</artifactId>
        	<version>0.1.2</version>
        </dependency>
        ```
        
### 里程
| 里程 | 版本 | 时间 |
| ----- | :---- | :-----: |
| 对其中Protocol__8的协议进行修改 | **___`0.2.0`___** | 2016-07-25 
| 将项目中的Rs485的module进行提取封装,做成第一版 | **___`0.1.2`___** | 2016-06-21 ||
