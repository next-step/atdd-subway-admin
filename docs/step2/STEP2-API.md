# 지하철노선 등록

### HTTP request
```
    POST /lines HTTP/1.1
    Content-Type: application/json
    Accept: application/json
    Host: localhost:8080

    {
        "name" : "신분당선",
        "color" : "bg-red",
        "upStationId" : 1,
        "downStationId" : 2,
        "distance" : 10
    }
```

### HTTP response
```
    HTTP/1.1 201 Created
    Vary: Origin
    Vary: Access-Control-Request-Method
    Vary: Access-Control-Request-Headers
    Location: /lines/1
    Content-Type: application/json
   
    
     {
        "id" :1,
        "name" : "신분당선",
        "color" : "bg-red",
        "staions":[{
            "id":1,
            "name" :지하철역
        },{
            "id":2,
            "name" :지하철역
          }
        ]
     }
```

# 지하철노선 목록
### HTTP request
```
    GET /lines HTTP/1.1
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

    
    [
        {
           "id" :1,
           "name" : "신분당선",
           "color" : "bg-red",
           "staions":[{
                    "id":1,
                    "name" :지하철역
                },{
                    "id":2,
                    "name" :지하철역
                  }
                ]
             }
    ]
```

# 지하철노선 조회
### HTTP request
```
    GET /lines/1 HTTP/1.1
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
```
```json

{
  "id" :1,
  "name" : "신분당선",
  "color" : "bg-red",
  "staions":[{
    "id":1,
    "name" :지하철역},
   {
    "id":2,
    "name" :지하철역}]
 }
```
# 지하철노선 수정
### HTTP request
```
    PUT /lines/1 HTTP/1.1
    Content-Type: application/json
    Host: localhost:8080
    
    {
        "name": "다른",
        "color": "bg-red"
    }
```

### HTTP response
```
    HTTP/1.1 200 OK
    Vary: Origin
    Vary: Access-Control-Request-Method
    Vary: Access-Control-Request-Headers
```
# 지하철노선 삭제
### HTTP request
```
    DELETE /lines/1 HTTP/1.1
 
    Host: localhost:8080
  
```

### HTTP response
```
    HTTP/1.1 204 No Content
    Vary: Origin
    Vary: Access-Control-Request-Method
    Vary: Access-Control-Request-Headers
```
