USE IMDB;
-- 2071485
SELECT * FROM title 
WHERE title LIKE '%Dilwale%'
LIMIT 0,10;


SELECT P.* FROM 
cast_info AS c
INNER JOIN 
person_info AS P
ON c.person_id = P.person_id
WHERE 
c.movie_id = 2071484;

SELECT * FROM info_type;
SELECT * FROM role_type;
SELECT * FROM `name` WHERE `name` LIKE '%Spielberg%' LIMIT 0,10;
SELECT * FROM char_name LIMIT 0,10;

SELECT I.info, P.info, N.`name`  FROM 
person_info AS P
INNER JOIN info_type AS I ON P.info_type_id = I.id
INNER JOIN `name` AS N ON N.id = P.person_id
WHERE 
person_id =895196;



