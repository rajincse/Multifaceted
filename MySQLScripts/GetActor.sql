USE IMDB;
-- 437747 DiCaprio, Leonardo
SELECT   
	N.id,  
	N.`name`,  
	N.gender 
 FROM `name` AS N                        
WHERE   
N.gender IS NOT NULL AND   
N.`name` LIKE '%Dicaprio, Leonardo%' 
LIMIT 0,10 ;

SELECT 	 
		N.`name`, 
		N.gender, 
		C.role_id, 
		T.id, 
		T.title AS title,  
		COALESCE(T.production_year,0) AS `year` , 
		COALESCE(PI.info,'') AS biography 
FROM  
title AS T 
LEFT OUTER JOIN movie_info_idx AS MI ON MI.movie_id = T.id AND MI.info_type_id=101 
INNER JOIN cast_info AS C ON C.movie_id = T.id AND (C.role_id = 1 OR C.role_id=2 OR C.role_id=8) AND T.kind_id=1 
INNER JOIN `name` AS N ON N.id = C.person_id 
 LEFT OUTER JOIN person_info AS PI ON N.id = PI.person_id AND PI.info_type_id=19 
WHERE  
N.id = 437747 
ORDER BY COALESCE(MI.info,0) DESC ;

