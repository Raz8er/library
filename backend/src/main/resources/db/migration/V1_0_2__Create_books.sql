CREATE TABLE IF NOT EXISTS books
(
    id                   BIGSERIAL PRIMARY KEY,
    title                VARCHAR(256),
    isbn                 VARCHAR(20),
    genre                VARCHAR(256),
    creation_date_time   TIMESTAMP DEFAULT NOW(),
    publishing_date_time TIMESTAMP DEFAULT NOW(),
    created_at           TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    updated_at           TIMESTAMP WITH TIME ZONE,
    CONSTRAINT books_isbn_uq UNIQUE (isbn)
);

CREATE INDEX IF NOT EXISTS books_title_idx ON books (lower((title)));
CREATE INDEX IF NOT EXISTS books_genre_idx ON books (lower((genre)));
CREATE INDEX IF NOT EXISTS books_publishing_date_time_idx ON books (publishing_date_time);
