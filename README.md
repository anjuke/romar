# Romar

A Simple Recommendation Web Service

## Quick Start

### Download and install

    $ curl https://github.com/anjuke/romar/downloads/...
    $ tar xzf ...

### Running

    $ cd romar
    $ bin/start.sh

默认服务将监听8080端口处理http请求，修改端口请修改`conf/romar.yaml`

### API

http://anjuke.github.com/romar/api/

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

 * **allowStringID**
   是否支持字符串形式的itemID或userID，缺省只允许整数形式的ID

具体查看`conf/romar.yaml`文件

## Build

    $ git clone git@github.com:anjuke/romar.git
    $ cd romar
    $ mvn

## Copyright & License

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

*Copyright 2012 Anjuke Inc.*
