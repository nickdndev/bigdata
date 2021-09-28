### HW1

### Deploy HADOOP
Развернуть локальный кластер в конфигурации 1 NN, 3 DN + NM, 1 RM, 1 History server
#### Name Node
![image](https://user-images.githubusercontent.com/49230518/135066749-a7da9d7a-e57f-47ac-9f5c-fb0af93669ba.png)
#### Data Nodes
![image](https://user-images.githubusercontent.com/49230518/135066987-3d8c5343-2227-4c93-9121-e4b1f1f275ac.png)
#### Resource Manager 
![image](https://user-images.githubusercontent.com/49230518/135070477-b582da09-ae71-4a59-8603-dd2d53f5252e.png)

### Tasks with HDFS
1.  Создайте папку в корневой HDFS-папке
```
hdfs dfs -mkdir /airbnb
```
![image](https://user-images.githubusercontent.com/49230518/135089671-5c7a8180-b842-4a68-ac50-e6dfee84d162.png)

```
hdfs dfs -ls /
```
![image](https://user-images.githubusercontent.com/49230518/135089736-c8f44d9d-4fac-4333-9fb9-c522e93d115f.png)

2.  Создайте в созданной папке новую вложенную папку.
```
hdfs dfs -mkdir /airbnb/data
```
![image](https://user-images.githubusercontent.com/49230518/135089849-99dcc091-f37f-469a-9e33-d4a609e63852.png)

```
hdfs dfs -ls /airbnb
```
![image](https://user-images.githubusercontent.com/49230518/135089926-79bf2340-940f-49a4-8e81-d184ea74144c.png)

3. Что такое Trash в распределенной FS? Как сделать так, чтобы файлы удалялись сразу, минуя “Trash”?

Файлы, не удаляются сразу из HDFS.Вместо этого HDFS перемещает его в корзину. Файл можно быстро восстановить, если он остается в корзине.

```
hdfs dfs -rm -skipTrash <src>
```

4.  Создайте пустой файл в подпапке из пункта 2
```
hdfs dfs -touchz /airbnb/data/description
```
![image](https://user-images.githubusercontent.com/49230518/135095275-eb3c1bf7-d27f-4a00-a009-2fbdc41f2217.png)

5. Удалите созданный файл.

```
hdfs dfs -rm /airbnb/data/description
```
![image](https://user-images.githubusercontent.com/49230518/135095550-9fc22299-84b0-42ca-b191-faf08a51163c.png)

```
hdfs dfs -ls /airbnb/data/
```
![image](https://user-images.githubusercontent.com/49230518/135095784-14dd4074-65b4-4b48-8b70-d8829e6fea53.png)

6. Удалите созданные папки.

```
hdfs dfs -rm -r  /airbnb
```
![image](https://user-images.githubusercontent.com/49230518/135096075-30b5a156-0966-46d1-8857-b36e4aaf040e.png)

```
hdfs dfs -ls /
```
![image](https://user-images.githubusercontent.com/49230518/135096139-f0765775-bb7d-444d-ac36-ac5a62d782c4.png)

