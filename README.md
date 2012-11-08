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

## Ingestion
使用Movielens 1M数据，从 http://www.grouplens.org/node/12 下载并解压


    $ cat ratings.dat | sed s/::/,/g | cut -d, -f1,2,3 > romar.log.0


将文件放入$ROMAR_HOME/data目录里启动romar，请求

    $ curl -H "Accept: application/json" 'localhost:8080/users/1/recommendations'


返回
>```javascript
[
	{"value":4.7291574,"item":3890},
	{"value":4.692892,"item":3530},
	{"value":4.662457,"item":989},
	{"value":4.6365013,"item":127},
	{"value":4.6365013,"item":3323}
]
```

## Copyright & License

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

*Copyright 2012 Anjuke Inc.*
