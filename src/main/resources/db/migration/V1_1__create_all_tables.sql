CREATE TABLE  if not exists  Person
(
    id          BIGSERIAL       NOT NULL,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    CONSTRAINT pk_person PRIMARY KEY (id)
);