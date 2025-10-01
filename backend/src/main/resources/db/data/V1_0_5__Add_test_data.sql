insert
into authors (name,
              date_of_birth,
              created_at)
select 'Author' || floor(random() * 1000000)::text,
       now() - (random() * interval '100 years'),
       now() - (random() * interval '365 days')
from
    generate_series(1, 1000);

-- Books
insert
into books (title,
            isbn,
            genre,
            creation_date_time,
            publishing_date_time)
select 'Book ' || floor(random() * 1000000)::text,
       'ISBN-' || floor(random() * 1000000000)::text,
       (array ['Romance',
           'Thriller',
           'Comedy',
           'Fantasy',
           'History',
           'Sci-Fi'])[floor(random() * 6 + 1)],
       now() - (random() * interval '100 years'),
       now() - (random() * interval '100 years')
from
    generate_series(1, 10000);

insert
into books_authors (book_id,
                    author_id)
select b.id,
       a.id
from books b
         join authors a on
    random() < 0.2;
