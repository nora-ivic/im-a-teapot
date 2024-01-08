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

    cursor.execute("DO $$ BEGIN"
                   "    IF to_regtype('category') IS NULL THEN"
                   "        CREATE TYPE category AS ENUM ('lost', 'found', 'abandoned', 'sheltered', 'dead');"
                   "    END IF;"
                   "END $$;")

    cursor.execute("DO $$ BEGIN"
                   "    IF to_regtype('pet_species') IS NULL THEN"
                   "        CREATE TYPE pet_species AS ENUM ('bird', 'cat', 'dog', 'lizard', 'other', 'rabbit', 'rodent', 'snake');"
                   "    END IF;"
                   "END $$;")

    cursor.execute("DO $$ BEGIN"
                   "    IF to_regtype('mail') IS NULL THEN"
                   "        CREATE DOMAIN mail AS TEXT CHECK (VALUE ~* '^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$');"
                   "    END IF;"
                   "END $$;")

    cursor.execute("CREATE TABLE IF NOT EXISTS user_custom ("
                   "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
                   "username VARCHAR(50) NOT NULL UNIQUE,"
                   "is_shelter BOOLEAN DEFAULT FALSE,"
                   "first_name VARCHAR(50) DEFAULT NULL,"
                   "last_name VARCHAR(50) DEFAULT NULL,"
                   "shelter_name VARCHAR(100) DEFAULT NULL,"
                   "email mail NOT NULL,"
                   "phone_number VARCHAR(15) NOT NULL,"
                   "CONSTRAINT chk_username CHECK (LENGTH(username) BETWEEN 5 AND 50),"
                   "CONSTRAINT chk_shelter CHECK ((is_shelter = TRUE AND shelter_name IS NOT NULL"
                   "                                AND first_name IS NULL AND last_name IS NULL)"
                   " 								OR (is_shelter = FALSE AND shelter_name IS NULL AND first_name IS NOT NULL))"
                   ")")

    cursor.execute("CREATE TABLE IF NOT EXISTS user_auth ("
                   "username VARCHAR(50) PRIMARY KEY,"
                   "password VARCHAR(128),"
                   "FOREIGN KEY (username) REFERENCES user_custom(username)"
                   ")")

    cursor.execute("CREATE TABLE IF NOT EXISTS pet ("
                   "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
                   "species pet_species,"
                   "name VARCHAR(100),"
                   "color VARCHAR(100),"
                   "age INTEGER,"
                   "date_time_lost TIMESTAMP,"
                   "location_lost VARCHAR(256),"
                   "description TEXT,"
                   "CONSTRAINT chk_is_not_empty CHECK (species IS NOT NULL OR name IS NOT NULL"
                   "									  OR color IS NOT NULL OR age IS NOT NULL"
                   "									  OR date_time_lost IS NOT NULL"
                   "									  OR location_lost IS NOT NULL"
                   "									  OR description IS NOT NULL)"
                   ")")

    cursor.execute("CREATE TABLE IF NOT EXISTS advertisement ("
                   "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
                   "category category DEFAULT 'lost',"
                   "deleted BOOLEAN DEFAULT FALSE,"
                   "user_id INTEGER NOT NULL,"
                   "pet_id INTEGER NOT NULL,"
                   "date_time_adv TIMESTAMP DEFAULT NOW()::TIMESTAMP,"
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
                   "user_id INTEGER NOT NULL,"
                   "advert_id INTEGER NOT NULL,"
                   "text TEXT,"
                   "location VARCHAR(256),"
                   "date_time_mess TIMESTAMP DEFAULT NOW()::TIMESTAMP,"
                   "FOREIGN KEY (user_id) REFERENCES user_custom(id),"
                   "FOREIGN KEY (advert_id) REFERENCES advertisement(id)"
                   ")")

    cursor.execute("CREATE TABLE IF NOT EXISTS picture ("
                   "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
                   "advert_id INTEGER,"
                   "message_id INTEGER,"
                   "link VARCHAR(256) NOT NULL,"
                   "FOREIGN KEY (advert_id) REFERENCES advertisement(id),"
                   "FOREIGN KEY (message_id) REFERENCES message(id),"
                   "CONSTRAINT chk_ids CHECK ((advert_id IS NULL AND message_id IS NOT NULL)"
                   "                            OR (advert_id IS NOT NULL AND message_id IS NULL))"
                   ")")

    cursor.execute("ALTER TABLE IF EXISTS message "
                   "ALTER COLUMN user_id SET NOT NULL,"
                   "ALTER COLUMN advert_id SET NOT NULL,"
                   "DROP CONSTRAINT IF EXISTS chk_content_not_empty;")

    conn.commit()
