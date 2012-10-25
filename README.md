###使用方法

bin/start.sh 8080

服务将监听8080端口处理http请求

url path

推荐
/recommend?userId=1
可选参数：format=text,json,xml

更新
/update?userId=1&itemId=1&value=1

删除
/remove?userId=1&itemId=1

提交更新
/commit

根据多个Item推荐
/item/recommend?itemId=1&itemId=2

需要注意的是在更新和删除操作之后，必须调用/commit才会对recommend的结果产生影响


样例在script/test.sh中

相关配置，修改romar.yaml


### License
```
/**
 * Copyright 2012 Anjuke Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```