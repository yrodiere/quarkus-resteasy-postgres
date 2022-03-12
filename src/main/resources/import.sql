set search_path = "public";

CREATE TABLE Download
(
    id          BIGSERIAL       NOT NULL,
    idProperty1 VARCHAR(255) NOT NULL,
    idProperty2 VARCHAR(255) NOT NULL,
    finished    BOOLEAN,
    CONSTRAINT pk_download PRIMARY KEY (id)
);


CREATE TABLE FilePart
(
    id               BIGSERIAL NOT NULL,
    idProperty1      VARCHAR(255),
    idProperty2      VARCHAR(255),
    filePartFilePath VARCHAR(255),
    download_id      BIGINT,
    CONSTRAINT pk_filepart PRIMARY KEY (id),
    CONSTRAINT FK_FILEPART_ON_DOWNLOAD FOREIGN KEY (download_id) REFERENCES Download (id)
);