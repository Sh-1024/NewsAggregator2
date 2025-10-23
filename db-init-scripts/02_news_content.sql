CREATE TABLE sources (
    id SERIAL PRIMARY KEY,
    api_source_id TEXT NOT NULL,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE news (
    id SERIAL PRIMARY KEY,
    source_id INT NOT NULL,
    header TEXT NOT NULL,
    language TEXT,
    link TEXT UNIQUE,
    published_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_source
        FOREIGN KEY (source_id)
        REFERENCES sources(id)
        ON DELETE CASCADE
);

CREATE TABLE key_words (
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);