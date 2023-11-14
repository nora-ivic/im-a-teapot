import psycopg2
import settings


def init_populate(database: str):
    conn = psycopg2.connect(
        database=database,
        user=settings.POSTGRES_USER,
        password=settings.POSTGRES_PASSWORD,
        host=settings.POSTGRES_HOST,
        port=settings.POSTGRES_PORT
    )

    cursor = conn.cursor()

    cursor.execute("INSERT INTO user_custom (username, first_name, last_name, email, phone_number)"
                   "    SELECT 'perica', 'Pero', 'Perić', 'pero@gmail.com', '0914657881'"
                   "    WHERE NOT EXISTS (SELECT * FROM user_custom"
                   "                        WHERE username = 'perica' AND first_name = 'Pero'"
                   "                            AND last_name = 'Perić' AND email = 'pero@gmail.com'"
                   "                            AND phone_number = '0914657881')")

    cursor.execute("INSERT INTO user_auth (username, password)"
                   "    SELECT 'perica', '$2b$12$.UitvHYCdilaKzzAD.4xmOo7FuAUNGOmQmU6iDxHv8.aPsJONJrya'"
                   "    WHERE NOT EXISTS (SELECT * FROM user_auth"
                   "                        WHERE username = 'perica'"
                   "                            AND password = '$2b$12$.UitvHYCdilaKzzAD.4xmOo7FuAUNGOmQmU6iDxHv8.aPsJONJrya')")

    cursor.execute("INSERT INTO user_custom (username, is_shelter, shelter_name, email, phone_number)"
                   "    SELECT 'sapice', TRUE, 'Šapice', 'sapice@yahoo.com', '021313313'"
                   "    WHERE NOT EXISTS (SELECT * FROM user_custom"
                   "                        WHERE username = 'sapice' AND is_shelter = TRUE"
                   "                            AND shelter_name = 'Šapice' AND email = 'sapice@yahoo.com'"
                   "                            AND phone_number = '021313313')")

    cursor.execute("INSERT INTO user_auth (username, password)"
                   "    SELECT 'sapice', '$2b$12$2treiQzaesvIRv5o8ePBbOEy5qGHG30Y8tr6tAiJqtKLHbc1lMhxS'"
                   "    WHERE NOT EXISTS (SELECT * FROM user_auth"
                   "                        WHERE username = 'sapice'"
                   "                            AND password = '$2b$12$2treiQzaesvIRv5o8ePBbOEy5qGHG30Y8tr6tAiJqtKLHbc1lMhxS')")

    cursor.execute("INSERT INTO pet (species, name, color, age, date_time_lost, description)"
                   "    SELECT 'dog', 'Aska', 'bijela', 6, '2023-11-14 10:06:09.916916', 'Krzno ima primjese crne.'"
                   "    WHERE NOT EXISTS (SELECT * FROM pet"
                   "                        WHERE species = 'dog' AND name = 'Aska' AND color = 'bijela'"
                   "                            AND age = 6 AND date_time_lost = '2023-11-14 10:06:09.916916'"
                   "                            AND description = 'Krzno ima primjese crne.')")

    cursor.execute("INSERT INTO pet (species, color, description)"
                   "    SELECT 'cat', 'siva', 'Nije čipirana, šepa.'"
                   "    WHERE NOT EXISTS (SELECT * FROM pet"
                   "                        WHERE species = 'cat' AND color = 'siva'"
                   "                            AND description = 'Nije čipirana, šepa.')")

    cursor.execute("INSERT INTO advertisement (user_id, pet_id, date_time_adv)"
                   "    SELECT 1, 1, '2023-11-14 10:28:10.834651'"
                   "    WHERE NOT EXISTS (SELECT * FROM advertisement"
                   "                        WHERE user_id = 1 AND pet_id = 1"
                   "                            AND date_time_adv = '2023-11-14 10:28:10.834651')")

    cursor.execute("INSERT INTO advertisement (category, user_id, pet_id, date_time_adv, is_in_shelter, shelter_id)"
                   "    SELECT 'sheltered', 2, 2, '2023-11-14 10:40:07.527194', TRUE, 2"
                   "    WHERE NOT EXISTS (SELECT * FROM advertisement"
                   "                        WHERE category = 'sheltered' AND user_id = 2 AND pet_id = 2"
                   "                            AND date_time_adv = '2023-11-14 10:40:07.527194'"
                   "                            AND is_in_shelter = TRUE AND shelter_id = 2)")

    cursor.execute("INSERT INTO picture (advert_id, link)"
                   "    SELECT 1, 'https://images.pexels.com/photos/3812207/pexels-photo-3812207.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1'"
                   "    WHERE NOT EXISTS (SELECT * FROM picture"
                   "                        WHERE advert_id = 1 AND link = 'https://images.pexels.com/photos/3812207/pexels-photo-3812207.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1')")

    cursor.execute("INSERT INTO picture (advert_id, link)"
                   "    SELECT 2, 'https://images.pexels.com/photos/2261538/pexels-photo-2261538.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1'"
                   "    WHERE NOT EXISTS (SELECT * FROM picture"
                   "                        WHERE advert_id = 2 AND link = 'https://images.pexels.com/photos/2261538/pexels-photo-2261538.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1')")

    conn.commit()
