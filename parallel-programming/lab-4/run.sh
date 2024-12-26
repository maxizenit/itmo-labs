#!/bin/bash

#Ввод параметров
echo -n "Enter reducers count: "
read reducerCount

echo -n "Enter data block size (kb): "
read dataBlockSize

#Сборка приложения
./gradlew jar

#Удаление из папки /tmp в namenode всех jar и csv файлов
docker exec -it namenode bash -c "find /tmp -type f \( -name "*.csv" -o -name "*.jar" \) -exec rm -f {} \;"

#Копирование приложения в namenode
docker cp build/libs/lab4-maxizenit-1.0-SNAPSHOT.jar namenode:/tmp

#Копирование всех csv-файлов в /tmp в namenode
for file in csv/*.csv; do docker cp "$file" namenode:/tmp; done

#Создание папки input
docker exec -it namenode bash -c "hdfs dfs -mkdir -p /user/root"
docker exec -it namenode bash -c "hdfs dfs -mkdir /user/root/input"

#Очистка папок input и output (если это вдруг не было сделано при предыдущих запусках)
docker exec -it namenode bash -c "hdfs dfs -rm -r /user/root/input/*"
docker exec -it namenode bash -c "hdfs dfs -rm -r /user/root/output-temp/*"
docker exec -it namenode bash -c "hdfs dfs -rm -r /user/root/output/*"

#Копирование всех csv-файлов из /tmp в input
docker exec -it namenode bash -c "hdfs dfs -put -f /tmp/*.csv /user/root/input"

#Запуск приложения
docker exec -it namenode bash -c  "hadoop jar /tmp/lab4-maxizenit-1.0-SNAPSHOT.jar \
  org.itmo.salescalculator.SalesCalculatorApp input output $reducerCount $dataBlockSize"

#Вывод результата в файл в репозитории
docker exec -it namenode bash -c "hdfs dfs -cat /user/root/output/part-*" > result.txt

#Очистка папок input и output
docker exec -it namenode bash -c "hdfs dfs -rm -r /user/root/input"
docker exec -it namenode bash -c "hdfs dfs -rm -r /user/root/output-temp"
docker exec -it namenode bash -c "hdfs dfs -rm -r /user/root/output"

read