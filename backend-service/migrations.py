import psycopg2
import settings


def run_migrations(database: str):
    conn = psycopg2.connect(
        database=database,
        user=settings.POSTGRES_USER,
        password=settings.POSTGRES_PASSWORD,
        host=settings.POSTGRES_HOST,
        port=settings.POSTGRES_PORT
    )

    cursor = conn.cursor()

    cursor.execute("CREATE TYPE category AS ENUM ('lost', 'found', 'abandoned', 'sheltered', 'dead')")

    cursor.execute("CREATE DOMAIN mail AS TEXT CHECK (VALUE ~* '^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')")

    cursor.execute("CREATE TABLE IF NOT EXISTS user_custom ("
                   "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
                   "username VARCHAR(50) NOT NULL UNIQUE,"
                   "is_shelter BOOLEAN DEFAULT FALSE,"
                   "first_name VARCHAR(50),"
                   "last_name VARCHAR(50),"
                   "shelter_name VARCHAR(100),"
                   "email VARCHAR(50) NOT NULL,"
                   "phone_number VARCHAR(15) NOT NULL,"
                   "CONSTRAINT chk_username CHECK (LENGTH(username) BETWEEN 5 AND 50),"
                   "CONSTRAINT chk_shelter CHECK ((is_shelter = TRUE AND shelter_name IS NOT NULL"
                   "                                AND first_name IS NULL AND last_name IS NULL)"
                   " 								OR (is_shelter = FALSE AND shelter_name IS NULL AND first_name IS NOT NULL))"
                   ")")

    cursor.execute("CREATE TABLE IF NOT EXISTS user_auth ("
                   "username VARCHAR(50) PRIMARY KEY,"
                   "password VARCHAR(20),"
                   "FOREIGN KEY (username) REFERENCES user_custom(username)"
                   ")")

    cursor.execute("CREATE TABLE IF NOT EXISTS pet ("
                   "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
                   "species VARCHAR(100),"
                   "name VARCHAR(100),"
                   "color VARCHAR(100),"
                   "age INTEGER,"
                   "date_time_lost TIMESTAMP,"
                   "location_lost VARCHAR(100),"
                   "description VARCHAR(256)"
                   ")")

    cursor.execute("CREATE TABLE IF NOT EXISTS advertisement ("
                   "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
                   "category category,"
                   "deleted BOOLEAN DEFAULT FALSE,"
                   "user_id INTEGER,"
                   "pet_id INTEGER,"
                   "date_time_adv TIMESTAMP,"
                   "is_in_shelter BOOLEAN DEFAULT FALSE,"
                   "shelter_id INTEGER,"
                   "FOREIGN KEY (user_id) REFERENCES user_custom(id),"
                   "FOREIGN KEY (pet_id) REFERENCES pet(id),"
                   "FOREIGN KEY (shelter_id) REFERENCES user_custom(id),"
                   "CONSTRAINT chk_is_in_shelter CHECK ((is_in_shelter = TRUE AND shelter_id IS NOT NULL)"
                   "                                        OR (is_in_shelter = FALSE AND shelter_id IS NULL))"
                   ")")

    cursor.execute("CREATE TABLE IF NOT EXISTS message ("
                   "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
                   "user_id INTEGER,"
                   "advert_id INTEGER,"
                   "text TEXT,"
                   "location TEXT,"
                   "date_time_mess TIMESTAMP,"
                   "FOREIGN KEY (user_id) REFERENCES user_custom(id),"
                   "FOREIGN KEY (advert_id) REFERENCES advertisement(id),"
                   "CONSTRAINT chk_content_not_empty CHECK (text IS NOT NULL OR location IS NOT NULL)"
                   ")")

    cursor.execute("CREATE TABLE IF NOT EXISTS picture ("
                   "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
                   "advert_id INTEGER,"
                   "message_id INTEGER,"
                   "link TEXT NOT NULL,"
                   "FOREIGN KEY (advert_id) REFERENCES advertisement(id),"
                   "FOREIGN KEY (message_id) REFERENCES message(id),"
                   "CONSTRAINT chk_ids CHECK ((advert_id IS NULL AND message_id IS NOT NULL)"
                   "                            OR (advert_id IS NOT NULL AND message_id IS NULL))"
                   ")")

    conn.commit()
