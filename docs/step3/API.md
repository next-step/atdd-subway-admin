##API 명세
### Http request
```
POST /lines/1/sections HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8
host: localhost:52165

{
    "downStationId": "4",
    "upStationId": "2",
    "distance": 10
}
```
### Http response
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
