CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       role VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       login VARCHAR(64) UNIQUE NOT NULL,
                       password_hash TEXT NOT NULL
);

CREATE TABLE user_roles (
                            user_id INT NOT NULL,
                            role_id INT NOT NULL,

                            CONSTRAINT fk_user
                                FOREIGN KEY (user_id)
                                    REFERENCES users(id)
                                    ON DELETE CASCADE,

                            CONSTRAINT fk_role
                                FOREIGN KEY (role_id)
                                    REFERENCES roles(id)
                                    ON DELETE CASCADE,

                            PRIMARY KEY (user_id, role_id)
);