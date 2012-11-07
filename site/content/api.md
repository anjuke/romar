---
title: Romar v1.0 API
---

# Romar v1.0 API

推荐引擎的REST接口

* TOC
{:toc}


## 设置用户对物品的偏好

    PUT /preferences/:user/:item

### Input

value
: 偏好得分, **float**

<%= json :value=>3.0 %>

### Response

<%= headers 202 %>

如果需要批量添加或修改用户对物品的偏好，可以使用以下方法，而不需要循环调用上述接口

    PUT /preferences

### Input

输入内容为用户对物品偏好的列表，列表内的元素为`[:user, :item, :value]`，例如

<%= json [
      [173213, 23711131, 0.96],
      [173213, 17192329, 0.84],
      [173214, 19232931, 0.84]
    ]  %>


## 删除用户对物品的偏好

    DELETE /preferences/:user/:item

### Response

<%= headers 202 %>


## 获取或估计用户对物品的偏好

    GET /preferences/:user/:item

### Response

<%= headers 200 %>
<%= json :value=>0.94 %>


## 获取相似的物品

    GET /items/:item/similars

需要查找与多个物品相似的物品，可以使用以下方式，将多个物品以参数形式传入。

    GET /items/similars

### Paramaters

item
: 物品编号, 发送多个同名参数来获取多个物品的相似物品

limit
: 最多返回多少结果, **int**, 缺省值为5

### Response

返回最相似的物品列表，每个结果包含相似的物品编号与相似度得分。例如:

<%= headers 200 %>
<%= json [
      {"item"=>23711131, "value"=>0.96},
      {"item"=>17192329, "value"=>0.84}
    ] %>

## 删除物品

    DELETE /items/:item

当物品下线时可以删除所有与该物品关联的偏好设置

### Response

<%= headers 202 %>


## 个性化推荐

    GET /users/:user/recommendations

### Parameters

limit
: 最多返回多少推荐结果, **int**, 缺省值为5


### Response

返回推荐结果的列表，每个推荐结果包含物品编号与偏好得分。例如:

<%= headers 200 %>
<%= json [
      {"item"=>23711131, "value"=>0.96},
      {"item"=>17192329, "value"=>0.84}
    ] %>

## 获取相似的用户

    GET /users/:user/similars

### Paramaters

limit
: 最多返回多少推荐结果, **int**, 缺省值为5

### Response

返回最相似的用户列表，每个结果包含相似用户编号与相似度得分。例如:

<%= headers 200 %>
<%= json [
      {"user"=>173213, "value"=>0.96},
      {"user"=>173214, "value"=>0.84}
    ] %>

## 删除用户

    DELETE /users/:user

当用户不再使用时可以删除所有该用户的偏好设置


### Response

<%= headers 202 %>


## commit

    POST /commit

所有用户对货物偏好的改变不会立即生效，都需要调用**commit**之后才会影响到查询的结果。

### Response

<%= headers 202 %>


## optimize

    POST /optimize

### Response

<%= headers 202 %>

