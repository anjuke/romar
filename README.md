###使用方法

bin/start.sh 8080

服务将监听8080端口处理http请求

修改start.sh中的 -Dsimilarity-classname 可以选择不同的similarity实现

url path

推荐
/recommend?userId=1
可选参数：format=text,json,xml

更新
/update?userId=1&itemId=1&value=1

删除
/remove?userId=1&itemId=1

重加载
/reload

根据多个Item推荐
/item/recommend?itemId=1&itemId=2

需要注意的是在更新和删除操作之后，必须调用/reload才会对recommend的结果产生影响


样例在script/test.sh中

相关配置，修改romar.yaml
