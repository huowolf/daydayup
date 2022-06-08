https://xinchen.blog.csdn.net/article/details/119705402

gateway-dynamic-by-nacos:

server:
  port: 8086

# 暴露端点
management:
  endpoints:
    web:
      exposure:
        include: '*'
  endpoint:
    health:
      show-details: always


gateway-json-routes:

[
    {
        "id": "path_route_addr",
        "uri": "http://127.0.0.1:8082",
        "predicates":[
            {
                "name": "Path",
                "args": {
                    "pattern": "/hello/**"
                }
            }
        ]
    }
]


