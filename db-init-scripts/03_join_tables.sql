CREATE TABLE news_keywords (
                               news_id INT NOT NULL,
                               keyword_id INT NOT NULL,

                               CONSTRAINT fk_news
                                   FOREIGN KEY (news_id)
                                       REFERENCES news(id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_keyword
                                   FOREIGN KEY (keyword_id)
                                       REFERENCES key_words(id)
                                       ON DELETE CASCADE,

                               PRIMARY KEY (news_id, keyword_id)
);

CREATE TABLE user_keywords (
                               user_id INT NOT NULL,
                               keyword_id INT NOT NULL,

                               CONSTRAINT fk_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_keyword
                                   FOREIGN KEY (keyword_id)
                                       REFERENCES key_words(id)
                                       ON DELETE CASCADE,

                               PRIMARY KEY (user_id, keyword_id)
);

CREATE TABLE user_sources (
                              user_id INT NOT NULL,
                              source_id INT NOT NULL,

                              CONSTRAINT fk_user
                                  FOREIGN KEY (user_id)
                                      REFERENCES users(id)
                                      ON DELETE CASCADE,

                              CONSTRAINT fk_source
                                  FOREIGN KEY (source_id)
                                      REFERENCES sources(id)
                                      ON DELETE CASCADE,

                              PRIMARY KEY (user_id, source_id)
);

CREATE TABLE source_keywords (
                                 source_id INT NOT NULL,
                                 keyword_id INT NOT NULL,

                                 CONSTRAINT fk_source
                                     FOREIGN KEY (source_id)
                                         REFERENCES sources(id)
                                         ON DELETE CASCADE,

                                 CONSTRAINT fk_keyword
                                     FOREIGN KEY (keyword_id)
                                         REFERENCES key_words(id)
                                         ON DELETE CASCADE,

                                 PRIMARY KEY (source_id, keyword_id)
);