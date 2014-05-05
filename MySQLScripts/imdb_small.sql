-- DROP DATABASE imdb_small;
CREATE DATABASE imdb_small;
USE imdb_small;


-- Movie Table

-- DROP TABLE movie;
CREATE TABLE movie
(
	id INT PRIMARY KEY,
	title TEXT,
	production_year INT,
	rating DECIMAL(10,2)
);

INSERT INTO movie ( id, title, production_year, rating)
SELECT 
	T.id,
	T.title,
	COALESCE(T.production_year,0) AS `year`,
	CAST(COALESCE(MI.info,0) AS DECIMAL(10,2)) AS rating

FROM imdb.title AS T
LEFT OUTER JOIN imdb.movie_info_idx AS MI ON MI.movie_id = T.id AND MI.info_type_id=101
WHERE
T.kind_id = 1;

CREATE INDEX idx_title ON movie(title(242));

-- Genre Table

CREATE TABLE genre
(
	id INT AUTO_INCREMENT PRIMARY KEY, 
	genre TEXT
);
CREATE INDEX idx_genre ON genre(genre(44));

INSERT INTO genre ( genre)
SELECT
	DISTINCT(info )   

FROM imdb.movie_info AS MI
WHERE MI.info_type_id =3;

CREATE TABLE movie_genre
(
	id INT AUTO_INCREMENT PRIMARY KEY,
	movie_id INT,
	genre_id INT
);
INSERT INTO movie_genre( movie_id, genre_id)
SELECT 
	T.id, 
	G.id
FROM 
imdb_small.movie AS T
INNER JOIN 
imdb.movie_info AS MI ON MI.movie_id = T.id AND MI.info_type_id=3
INNER JOIN imdb_small.genre AS G ON G.genre = COALESCE(MI.info,'');

-- person table
CREATE TABLE person
(
	id INT PRIMARY KEY,
	first_name TEXT,
	last_name TEXT,
	gender VARCHAR(1)
);
CREATE INDEX idx_first_name ON person(first_name(106));
CREATE INDEX idx_last_name ON person(last_name(106));

INSERT INTO  person(id, first_name, last_name, gender)
SELECT   
	N.id,  
	SUBSTRING(N.`name`,INSTR(N.`name`,',')+1, LENGTH(N.`name`)) AS first_name, 
	SUBSTRING(N.`name`,1, INSTR(N.`name`,',')-1) AS last_name, 
	 gender
 FROM imdb.`name` AS N 
WHERE   
N.gender IS NOT NULL;

-- cast info
-- mysqldump -u root -prajin imdb cast_info > C:\Users\rajin\Documents\dumps\cast_info.sql
-- mysql -u root -p imdb_small < C:\Users\rajin\Documents\dumps\cast_info.sql


-- biography
CREATE TABLE biography
(
	id INT PRIMARY KEY,
	person_id INT, 
	biography TEXT
);
CREATE INDEX idx_bio_person_id ON biography(person_id);

INSERT INTO biography ( id, person_id, biography)
SELECT 
	PI.id,
	PI.person_id,
	PI.info

FROM 
imdb.person_info AS PI 
INNER JOIN 
person AS P ON P.id = PI.person_id
WHERE 
PI.info_type_id =19;

