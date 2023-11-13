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

    cursor.execute(...)

    conn.commit()
