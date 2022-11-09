# 지하철역 목록

### HTTP request
```
    GET /station HTTP/1.1
    Accept: application/json
    Host: localhost:8080
```

### HTTP response
```
    HTTP/1.1 200 OK
    Vary: Origin
    Vary: Access-Control-Request-Method
    Vary: Access-Control-Request-Headers
    Content-Type: application/json
    Content-Length: 167
    
    [{
        "id" : 1,
        "name":"지하철역이용"
    }, {
        "id" : 2,
        "name":"새로운지하철역이용"
    },{
        "id" : 3,
        "name":"또다른지하철역이용"
    }]
```

# 지하철역 삭제

### HTTP request
```
    DELETE /station/1 HTTP/1.1
    Host: localhost:8080
```

### HTTP response
```
    HTTP/1.1 204 No Content
    Vary: Origin
    Vary: Access-Control-Request-Method
    Vary: Access-Control-Request-Headers
```
