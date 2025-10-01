CREATE TABLE IF NOT EXISTS books_authors
(
    book_id   BIGINT NOT NULL REFERENCES books (id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES authors (id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, author_id)
);

CREATE INDEX IF NOT EXISTS books_authors_book_id_idx ON books_authors(book_id);
CREATE INDEX IF NOT EXISTS books_authors_author_id_idx ON books_authors(author_id);
