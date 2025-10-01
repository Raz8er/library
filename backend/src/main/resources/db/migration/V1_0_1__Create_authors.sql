CREATE TABLE IF NOT EXISTS authors
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(256),
    date_of_birth DATE,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS authors_name_idx ON authors (lower((name)));
CREATE INDEX IF NOT EXISTS authors_created_at_idx ON authors (created_at);
