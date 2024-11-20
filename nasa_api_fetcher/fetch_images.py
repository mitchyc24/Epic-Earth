import os
import requests
import logging
from datetime import datetime
from EPIC_Image import EPIC_Image

API_KEY = os.environ.get('NASA_API_KEY')
NASA_EPIC_URL = 'https://api.nasa.gov/EPIC/api/natural/images'

# Configure logging
logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

def get_images_on_date(date):
    url_endpoint = f'{NASA_EPIC_URL}?api_key={API_KEY}&date={date}'
    try:
        response = requests.get(url_endpoint)
        response.raise_for_status()  # Raises an error for bad responses
        data = response.json()
        
        logger.debug(f"First data item: {data[0]}")

        images = [EPIC_Image(image_data) for image_data in data]

        logger.info(f"Found {len(images)} images for date {date}")

        return images
    except requests.exceptions.RequestException as e:
        logger.error(f"Error fetching images for date {date}: {e}")
        return []
    

def get_date_input():
    date_input = input('Enter a date (YYYY-MM-DD): ')
    try:
        if date_input == "":
            date_input = datetime.strptime("2021-01-01", '%Y-%m-%d')
            logger.info("No date entered, defaulting to 2021-01-01")
        else:
            date_input = datetime.strptime(date_input, '%Y-%m-%d')
    except ValueError:
        logger.error('Invalid date format. Please enter a date in the format YYYY-MM-DD.')
        return get_date_input()
    return date_input

if __name__ == '__main__':
    date = get_date_input()
    images = get_images_on_date(date.strftime('%Y-%m-%d'))
    for image in images:
        logger.debug(image)