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

7. Скопируйте любой в новую папку на HDFS

```
hdfs dfs -put AB_NYC_2019.csv /airbnb/data/
```
![image](https://user-images.githubusercontent.com/49230518/135100796-b280f99e-86a7-4f4d-aa45-31da2debc459.png)

```
hdfs dfs -ls /airbnb/data/
```
![image](https://user-images.githubusercontent.com/49230518/135100915-c7c9c8cf-4256-46a2-9482-c74f6f40088e.png)

8. Выведите содержимое HDFS-файла на экран.

```
hdfs dfs -cat /airbnb/data/AB_NYC_2019.csv
```
![image](https://user-images.githubusercontent.com/49230518/135101372-0047a6b5-72d4-47a6-a1b6-4452119d4b11.png)

 9. Выведите содержимое нескольких последних строчек HDFS-файла на экран.
 ```
 hdfs dfs -tail /airbnb/data/AB_NYC_2019.csv
 ```
 
 ![image](https://user-images.githubusercontent.com/49230518/135101579-358eb1da-7abd-42d2-bbaf-1a67413b9889.png)

10. Выведите содержимое нескольких первых строчек HDFS-файла на экран.

```
hdfs dfs -head /airbnb/data/AB_NYC_2019.csv
```
![image](https://user-images.githubusercontent.com/49230518/135101924-a653a7ac-6a90-479a-b08c-0e379f149be9.png)

11. Переместите копию файла в HDFS на новую локацию.
```
hdfs dfs -cp /airbnb/data/AB_NYC_2019.csv /airbnb/AB_NYC_2019_copy.csv
```
![image](https://user-images.githubusercontent.com/49230518/135102317-dbe01bee-a867-45d8-a8ca-e45f739d4a12.png)

```
hdfs dfs -ls /airbnb/ 
```
![image](https://user-images.githubusercontent.com/49230518/135102415-e3a4e6dc-678c-4000-aa13-1f3c6a25a732.png)
