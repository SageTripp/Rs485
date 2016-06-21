# Rs485
[![Release](https://jitpack.io/v/SageTripp/Rs485.svg)](https://jitpack.io/#SageTripp/Rs485)

Rs485通信

### 使用方法
* gradle方式
    - ① 在项目的build.gradle文件中添加如下代码
    
        ```groovy
        allprojects {
            repositories {
                ...
                maven { url "https://jitpack.io" }
            }
        }
        ```

    - ② 在dependency添加
            
        ```groovy 
        dependencies {
            compile 'com.github.SageTripp:Rs485:0.1.2'
            }
        ```
        
* maven方式
    - ① 在项目的build.gradle文件中添加如下代码
    
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
        