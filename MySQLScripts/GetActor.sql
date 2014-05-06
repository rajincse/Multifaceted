USE IMDB;
-- 437747 Leonardo DiCaprio
-- 700661 Tom Hanks
-- 369071 Tom Cruise
-- 1304615 Al Pacino
-- 95992 Christian Bale


SELECT   
	N.id,  
	N.`name`,  
	N.gender 
 FROM `name` AS N                        
WHERE   
N.gender IS NOT NULL AND   
N.`name` LIKE '%Bale, Christian%' 
LIMIT 0,10 ;

SELECT 	 
		N.`name`, 
		N.gender, 
		C.role_id, 
		T.id, 
		T.title AS title,  
		COALESCE(T.production_year,0) AS `year` 
	 	,COALESCE(PI.info,'') AS biography 
FROM  
title AS T 
LEFT OUTER JOIN movie_info_idx AS MI ON MI.movie_id = T.id AND MI.info_type_id=101 
INNER JOIN cast_info AS C ON C.movie_id = T.id AND (C.role_id = 1 OR C.role_id=2 OR C.role_id=8) AND T.kind_id=1 
INNER JOIN `name` AS N ON N.id = C.person_id 
LEFT OUTER JOIN person_info AS PI ON N.id = PI.person_id AND PI.info_type_id=19 
WHERE  
N.id = 95992 
ORDER BY COALESCE(MI.info,0) DESC ;

-- Imdb small
USE imdb_small;

SELECT   
	N.id,  
	`name`,  
	N.gender 
 FROM person AS N                        
WHERE   
`name` LIKE '%Christian Bale%' 
LIMIT 0,10 ;

SELECT 	 
		N.`name`,
		N.gender, 
		C.role_id, 
		T.id, 
		T.title AS title,  
		T.production_year AS `year` 
		 ,B.biography AS biography 
FROM  
movie AS T 
INNER JOIN cast_info AS C ON C.movie_id = T.id  AND (C.role_id = 1 OR C.role_id=2 OR C.role_id=8) 
INNER JOIN person AS N ON N.id = C.person_id 
  INNER JOIN biography AS B ON B.person_id = N.id
WHERE  
N.id = 95992 
ORDER BY T.rating DESC ;

