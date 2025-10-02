CREATE TABLE IF NOT EXISTS book_reports
(
    id             BIGSERIAL PRIMARY KEY,
    generated_at   TIMESTAMP                DEFAULT NOW(),
    from_date_time TIMESTAMP,
    to_date_time   TIMESTAMP,
    created_at     TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS book_reports_generated_at_idx ON book_reports (generated_at);

CREATE TABLE IF NOT EXISTS book_report_isbns
(
    book_report_id BIGINT      NOT NULL REFERENCES book_reports (id) ON DELETE CASCADE,
    isbn           VARCHAR(20) NOT NULL
);

CREATE INDEX IF NOT EXISTS book_report_isbns_book_report_id_idx ON book_report_isbns (book_report_id);
