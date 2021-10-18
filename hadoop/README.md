### Data exploration 
![image](https://user-images.githubusercontent.com/49230518/135260569-0aa3e765-f461-4421-89e7-3255a1cc74e1.png)


### Hadoop MapReduce
1. execute map reduce job
```
hadoop jar hadoop-assembly-0.1.jar com.made.JobRunner /airbnb/data/AB_NYC_2019.csv /airbnb/data/price_airbnb
```

![image](https://user-images.githubusercontent.com/49230518/135260923-c21afa8a-ef55-478a-9413-75cd63e61c46.png)

2. Check job result 
```
hdfs dfs -cat /airbnb/data/price_airbnb/part-r-00000
```

![image](https://user-images.githubusercontent.com/49230518/135261105-23cee1bf-8b4a-49ac-ad25-2626dd691cd6.png)
