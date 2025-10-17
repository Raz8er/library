insert
into authors (name,
              date_of_birth,
              created_at)
select 'Author' || floor(random() * 1000000)::text,
       now() - (random() * interval '100 years'),
       now() - (random() * interval '365 days')
from
    generate_series(1, 1000);

insert
into books (title,
            isbn,
            genre,
            creation_date_time,
            publishing_date_time)
select 'Book' || floor(random() * 1000000)::text,
       LPAD(floor(random() * 1000)::text, 3, '0') || '-' ||
       LPAD(floor(random() * 100)::text, 2, '0') || '-' ||
       LPAD(floor(random() * 100000)::text, 5, '0') || '-' ||
       LPAD(floor(random() * 100)::text, 2, '0') || '-' ||
       LPAD(floor(random() * 10)::text, 1, '0'),
       (array ['Romance',
           'Thriller',
           'Comedy',
           'Fantasy',
           'History',
           'Sci-Fi'])[floor(random() * 6 + 1)],
       sub.creation_date_time,
       least(
               now() - (random() * interval '365 days'),
               sub.creation_date_time + (random() * interval '10 years')
       )
from (select now() - (random() * interval '100 years') as creation_date_time
      from generate_series(1, 10000)) as sub;

insert
into books_authors (book_id,
                    author_id)
select b.id,
       a.id
from books b
         join authors a on
    random() < 0.05;
