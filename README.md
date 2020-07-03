# library-scala-play-demo

Запуск mysql в контейнере
```
docker run --name traktrain-mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=traktrain -e MYSQL_USER=traktrain -e MYSQL_PASSWORD=traktrain -p 3306:3306 -d mysql:latest
```

Запуск
```
sbt run
```
