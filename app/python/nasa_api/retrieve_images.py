import os
import sys
import requests
import logging
import sqlite3
import json
from EPIC_Image import EPIC_Image
from datetime import datetime
from PIL import Image
from io import BytesIO


# API key for NASA API
API_KEY = os.getenv('NASA_API_KEY')

# Configure logging
logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

def get_EPIC_images_on_date(date: datetime) -> list[EPIC_Image]:
    available_dates_response = requests.get(f"https://api.nasa.gov/EPIC/api/natural/all?api_key={API_KEY}")
    available_dates = available_dates_response.json()
    
    # Extract dates from the JSON response
    available_dates_list = [datetime.strptime(item['date'], '%Y-%m-%d').date() for item in available_dates]
    
    if date.date() not in available_dates_list:
        logger.error(f"No images available for date {date.strftime('%Y-%m-%d')}")
        return []
    
    url_endpoint = f'https://api.nasa.gov/EPIC/api/natural/date/{date.strftime("%Y-%m-%d")}?api_key={API_KEY}'
    try:
        logger.info(f"Fetching images for date {date.strftime('%Y-%m-%d')} from {url_endpoint}")
        response = requests.get(url_endpoint)
        response.raise_for_status()  # Raises an error for bad responses
        data = response.json()
        logger.info(f"First data item: {data[0]}")
        list_of_EPIC_images = [EPIC_Image.from_dict(item) for item in data]
        logger.info(f"Found {len(list_of_EPIC_images)} images for date {date.strftime('%Y-%m-%d')}")
        return list_of_EPIC_images
    
    except requests.exceptions.RequestException as e:
        logger.error(f"Error fetching images for date {date.strftime('%Y-%m-%d')}: {e}")
        return []

def save_image(image_url, image_name, date):
    response = requests.get(image_url)
    response.raise_for_status()
    image = Image.open(BytesIO(response.content))
    
    # Create the directory if it doesn't exist
    date_str = date.strftime('%Y-%m-%d')
    directory = os.path.join('data', 'images', date_str)
    os.makedirs(directory, exist_ok=True)
    
    # Save the image
    image_path = os.path.join(directory, f'{image_name}.png')
    image.save(image_path)
    
    return image_path

def main():
    # Get arguments from the command line
    import argparse
    parser = argparse.ArgumentParser(description='Retrieve EPIC images from NASA API')
    parser.add_argument('date', type=str, help='Date in format YYYY-MM-DD')
    args = parser.parse_args()

    # Parse the date
    date = datetime.strptime(args.date, '%Y-%m-%d')
    logger.info(f"Retrieving images for date {date.strftime('%Y-%m-%d')}")

    # Get the images for the date
    images = get_EPIC_images_on_date(date)
    if not images:
        logger.error(f"No images found for date {date.strftime('%Y-%m-%d')}")
        return

    # Save images and print metadata
    image_metadata = []
    for image in images:
        image_path = save_image(image.url, image.name, date)
        metadata = {
            'name': image.name,
            'path': image_path,
            'date': date.strftime('%Y-%m-%d')
        }
        image_metadata.append(metadata)

    
    print(json.dumps(image_metadata))

if __name__ == "__main__":
    try:
        main()
    except Exception as e:
        logger.error(f"An error occurred: {e}", exc_info=True)
        sys.stderr.write(f"An error occurred: {e}\n")
        sys.exit(1)