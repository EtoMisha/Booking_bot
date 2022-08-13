CREATE TABLE IF NOT EXISTS clients (
    id serial primary key ,
    name text not null ,
    email text
);

CREATE TABLE IF NOT EXISTS slots (
    time timestamp primary key not null ,
    client int references clients(id)
)