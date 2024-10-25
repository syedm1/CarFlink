from confluent_kafka import Producer
import json
import random
import time
from concurrent.futures import ThreadPoolExecutor


def car_listings_producer():
    producer = Producer({'bootstrap.servers': 'localhost:29092'})
    car_makes = ['Toyota', 'Ford', 'BMW', 'Audi']

    while True:
        car_data = {
            'car_id': random.randint(1, 1000),
            'make': random.choice(car_makes),
            'model': 'Model-' + str(random.randint(1, 20)),
            'year': random.randint(2000, 2022),
            'price': random.randint(5000, 50000)
        }
        producer.produce('car-listings', value=json.dumps(car_data))
        producer.flush()
        time.sleep(5)


def car_enrichment_producer():
    producer = Producer({'bootstrap.servers': 'localhost:29092'})

    while True:
        enrichment_data = {
            'car_id': random.randint(1, 1000),
            'estimated_value': random.randint(6000, 55000),
            'accident_history': random.choice(['None', 'Minor', 'Major']),
            'service_records': random.randint(1, 10)
        }
        producer.produce('car-enrichment', value=json.dumps(enrichment_data))
        producer.flush()
        time.sleep(12.5)


def main():
    with ThreadPoolExecutor(max_workers=2) as executor:
        executor.submit(car_listings_producer)
        executor.submit(car_enrichment_producer)


if __name__ == "__main__":
    main()