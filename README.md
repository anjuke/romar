# Romar

A Simple Recommendation Web Service

[![Build Status](https://travis-ci.org/anjuke/romar.png)](https://travis-ci.org/anjuke/romar)

## Quick Start

### 下载、安装

下载地址: https://github.com/anjuke/romar/downloads

    $ wget https://github.com/anjuke/romar/downloads/romar-core-1.0.0.tar.gz
    $ tar xzf romar-core-1.0.0.tar.gz

### 下载测试数据

使用Movielens 1M数据，从 http://www.grouplens.org/node/12 下载并解压。然后将ratings.dat文件转换一下，参考如下命令。

    $ awk -F '::' '{printf "%s,%s,%s\n", $1, $2, $3}' ratings.dat > romar.log.0

将生成的文件`romar.log.0`放入`$ROMAR_HOME/data`目录里。

### 启动

    $ bin/start.sh

默认服务将监听8080端口处理http请求，修改端口请修改`conf/romar.yaml`

### 测试

然后执行

    $ curl "http://localhost:8080/users/1/recommendations"

>```javascript
[
	{"value":4.7291574, "item":3890},
	{"value":4.692892, "item":3530},
	{"value":4.662457, "item":989},
	{"value":4.6365013, "item":127},
	{"value":4.6365013, "item":3323}
]
```
如果看到类似上述返回信息，那么romar安装成功。

## API

详见: http://anjuke.github.com/romar/api/

## Config

 * **serverPort**  
   服务端口，缺省值`8080`

 * **recommendType**  
   允许`item`或`user`，表示Itembased或Userbased的协同过滤.

 * **itemSimilarityClass**  
   物品相似度算法。缺省值`TanimotoCoefficientSimilarity`

 * **userSimilarityClass**  
   用户相似度算法。缺省值`PearsonCorrelationSimilarity`

 * **userNeighborhoodClass**  
   相邻用户算法。缺省值`NearestNUserNeighborhood`

 * **userNeighborhoodNearestN**  
   缺省为`50`

 * **persistencePath**  
   持久化数据的存储路径，缺省为空表示不持久化

 * **allowUserStringID**, **allowItemStringID**  
   是否支持字符串形式的itemID或userID，缺省只允许整数形式的ID

具体查看`conf/romar.yaml`文件

## Build

    $ git clone git@github.com:anjuke/romar.git
    $ cd romar
    $ mvn

## Copyright & License

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

*Copyright 2012 Anjuke Inc.*
