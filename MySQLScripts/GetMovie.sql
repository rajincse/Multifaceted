USE imdb;

-- 2236590 Inception
-- 2144576 Forrest Gump
-- 2359532 Magnolia
-- 2666902 The GodFather
-- 2652712 The Dark Knight
-- Search 
SELECT 	T.id, 
		T.title,  
		COALESCE(T.production_year,0) AS `year` 
FROM title AS T 
WHERE   
 T.title LIKE '%The Dark Knight%' 
AND T.kind_id = 1 
LIMIT 0,10 ;

-- GetMovie

SELECT 	T.title AS title,  
		COALESCE(T.production_year,0) AS `year`,   
		COALESCE(MI.info,0) AS rating, 
		C.role_id, 
		N.id,  
		N.`name`, 
		COALESCE(N.gender,'m')  
		 
FROM  
title AS T 
LEFT OUTER JOIN movie_info_idx AS MI ON MI.movie_id = T.id AND MI.info_type_id=101 
INNER JOIN cast_info AS C ON C.movie_id = T.id 
 AND ( C.role_id=8 OR C.role_id = 1 OR C.role_id =2) 
INNER JOIN `name` AS N ON N.id = C.person_id 
WHERE  
T.id =  2652712
ORDER BY COALESCE(C.nr_order,1000);

-- Imdb small
USE imdb_small;

SELECT 	T.id, 
		T.title,  
		T.production_year 
FROM movie AS T 
WHERE   
 T.title LIKE '%The Dark Knight%' 
LIMIT 0,10 ;

SELECT 	T.title AS title,  
		T.production_year,   
		T.rating, 
		C.role_id, 
		N.id,  
		N.`name`, 
		N.gender 
		 
FROM  
movie AS T 
INNER JOIN cast_info AS C ON C.movie_id = T.id 
 AND ( C.role_id=8 OR C.role_id = 1 OR C.role_id =2) 
INNER JOIN person AS N ON N.id = C.person_id 
WHERE  
T.id =  2652712
ORDER BY C.nr_order;

