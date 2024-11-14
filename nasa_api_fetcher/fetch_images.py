import os
import requests
from datetime import datetime
from EPIC_Image import EPIC_Image

API_KEY = os.environ.get('NASA_API_KEY')
NASA_EPIC_URL = 'https://api.nasa.gov/EPIC/api/natural/images'


def get_images_on_date(date):
    url_endpoint = f'{NASA_EPIC_URL}?api_key={API_KEY}&date={date}'
    try:
        response = requests.get(url_endpoint)
        response.raise_for_status()  # Raises an error for bad responses
        data = response.json()
        print(data)
        # images = [EPIC_Image(item['image'], item['date']) for item in data]
        images = []
        return images
    except requests.exceptions.RequestException as e:
        print(f"Error fetching images for date {date}: {e}")
        return []
    

def get_date_input():
    date_input = input('Enter a date (YYYY-MM-DD): ')
    try:
        date_input = datetime.strptime(date_input, '%Y-%m-%d')
    except ValueError:
        print('Invalid date format. Please enter a date in the format YYYY-MM-DD.')
        return get_date_input()
    return date_input

if __name__ == '__main__':
    date = get_date_input()


    images = get_images_on_date(date.strftime('%Y-%m-%d'))
    for image in images:
        print(image)