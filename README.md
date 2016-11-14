# Rs485

Rs485通信

### 使用方法
* `gradle`方式

    - ① 在`dependency`添加
            
        ```groovy 
        dependencies {
            compile 'com.okq.RS485:protocolLib:0.1.0'
            }
        ```
        
* `maven`方式

    - ① 在dependency添加
            
        ```xml
        <dependency>
            <groupId>com.okq.RS485</groupId>
            <artifactId>protocolLib</artifactId>
            <version>0.1.0</version>
            <type>pom</type>
        </dependency>
        ```
        
### 里程
| 里程 | 版本 | 时间 |
| ----- | :---- | :-----: |
| 对其中Protocol__8的协议进行修改 | **___`0.2.0`___** | 2016-07-25 
| 将项目中的Rs485的module进行提取封装,做成第一版 | **___`0.1.2`___** | 2016-06-21 ||
| 将项目放在了bintray上,增加了RS485串口通信 | **__`0.1.0`__** | 2016-11-14 ||
