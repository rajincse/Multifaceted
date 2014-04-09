USE imdb;
-- 642265
-- 835306
SELECT * FROM title
WHERE title LIKE '%Titanic%';

SELECT 
T.title, K.kind, R.role, N.`name`, CH.`name`
FROM 
title AS T 
LEFT OUTER JOIN 
kind_type AS K ON K.id = T.kind_id
LEFT OUTER  JOIN 
cast_info AS C ON T.id = C.movie_id
LEFT OUTER  JOIN 
char_name AS CH ON C.person_role_id = CH.id
LEFT OUTER  JOIN 
role_type AS R ON C.role_id = R.id
LEFT OUTER  JOIN 
`name` AS N ON C.person_id = N.id
WHERE 
T.id = 642265;