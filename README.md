# Start

Запуск через docker
```
docker-compose up
```

# Microservices

- **Authentication service** - security через JWT
- **User service** - управление юзерами
- **ApiGateway** - маршрутизатор запросов
- **Discovery service** - обнаружение сервисов
- **Messenger service** - мессенджер для обмена сообщениями, файлами и проч.
- **File storage service** - управление и хранение файлов
- **Notification service** - управление уведомлениями

# API Documentation

Для документации API используется **OpenAPI** совместно с **Swagger UI**

***/v3/api-docs*** - документация в формате json 

***/swagger-ui/index.html*** - документация с UI

Пример:
- */messenger/api/v3/api-docs*
- */messenger/api/swagger-ui/index.html*

# Stack
- **Spring boot 3**
- **Spring data** : JPA, Redis
- **Spring cloud** : circuit breaker, gateway, netflix, 
- **Spring security**
- **PostgreSQL**
- **Prometheus**
- **Redis**
- **Kafka**
- **S3 Minio**
    