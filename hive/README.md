# Hive


### Развернуть локальный Hive в любой конфигурации (20 баллов)(done)
![image](https://user-images.githubusercontent.com/49230518/136997247-d8929d3c-e811-4beb-acef-d46a2b1f7b0a.png)

### Подключиться к развернутому Hive с помощью любого инструмента: Hue, Python Driver, Zeppelin, любая IDE итд (15 баллов за любой инструмент, максимум 30 баллов) (30 баллов) (done)
Сделать скриншоты поднятого Hive и подключений в выбранными вами
инструментах, добавить в репозиторий

- intellij idea
![image](https://user-images.githubusercontent.com/49230518/136997518-2a044572-1340-46d0-9b52-f35880f416ed.png)

- Hue
![image](https://user-images.githubusercontent.com/49230518/136997639-6e8e0db9-d4ed-42b6-bb0e-ffae9f9bd556.png)

### Сделать таблицу artists в Hive и вставить туда значения, используя датасет https://www.kaggle.com/pieca111/music-artists-popularity - (15 баллов) (done)

```
create table artists
(
    mbid             string,
    artist_mb        string,
    artist_lastfm    string,
    country_mb       string,
    country_lastfm   string,
    tags_mb          array<string>,
    tags_lastfm      array<string>,
    listeners_lastfm integer,
    scrobbles_lastfm integer,
    ambiguous_artist boolean
) ROW FORMAT DELIMITED
    FIELDS TERMINATED BY ','
    COLLECTION ITEMS TERMINATED BY ';';
    
 ```
    
 ![image](https://user-images.githubusercontent.com/49230518/136997930-e2f3b2f7-ea3d-401c-94e3-9fecd9003ad9.png)
 ```
     load data local inpath '/data.csv' overwrite into table artists;
```
 ![image](https://user-images.githubusercontent.com/49230518/136998875-2815d69d-b958-4c16-95a3-451389ffb600.png)

 ### Используя Hive найти (команды и результаты записать в файл и добавить в репозиторий):   
 - Исполнителя с максимальным числом скробблов ( 5 баллов) (done)
 ```
select artist_mb, scrobbles_lastfm
from artists sort by scrobbles_lastfm desc
limit 10;
```

![image](https://user-images.githubusercontent.com/49230518/136998642-6d7880d0-7200-4ab2-b0ee-5f7f1c39bb73.png)


- Самый популярный тэг на ластфм (10 баллов) (done)
```
with top_tags as (select tag as tag, count(tag) as tag_count
                  from (select trim(lower(tag)) as tag
                        from artists
                                 lateral VIEW explode(tags_lastfm) lv AS tag) tags
                  group by tag
                  order by tag_count desc)
select t.tag
from top_tags t
limit 10;
```
![image](https://user-images.githubusercontent.com/49230518/136998252-587f2870-6ca9-49fa-9fda-40ff231ea5ec.png)

- Самые популярные исполнители 10 самых популярных тегов ластфм (искал топ исполнителей, которые имеют тон теги) (10 баллов) (done)
```
with top_10_tags as (select tag as tag, count(tag) as tag_count
                     from (select trim(lower(tag)) as tag
                           from artists
                                    lateral VIEW explode(tags_lastfm) lv AS tag) tags
                     group by tag
                     order by tag_count desc
                     limit 10),
     artist_by_tag as (select artist_mb, scrobbles_lastfm, trim(lower(tag)) as tag
                       from artists
                                lateral VIEW explode(tags_lastfm) lv AS tag
                           sort by scrobbles_lastfm desc),
     artist_with_top_tags as (
         select artist_by_tag.artist_mb, scrobbles_lastfm
         from top_10_tags,
              artist_by_tag
         where artist_by_tag.tag == top_10_tags.tag
         group by artist_by_tag.artist_mb, scrobbles_lastfm
         order by scrobbles_lastfm desc)
select artist_mb
from artist_with_top_tags
limit 10;
```

![image](https://user-images.githubusercontent.com/49230518/136998456-c974b5b1-a1f3-4bec-9096-78373ba54cae.png)

- Топ стран откуда вышли самые популярные исполнители  (Любой другой инсайт на ваше усмотрение) (10 баллов) (done) 

```
with artist_by_country as (
    select country_lastfm as country, sum(scrobbles_lastfm) as scrobbles_count
    from artists
    where country_lastfm != ''
    group by country_lastfm
)
select *
from artist_by_country
order by scrobbles_count desc
limit 10;
```

![image](https://user-images.githubusercontent.com/49230518/136998542-b3800c27-8e81-4dd2-b4fd-d13a34b0c39a.png)

