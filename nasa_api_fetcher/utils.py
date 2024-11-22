import os
import logging
from datetime import datetime, timedelta
from PIL import Image

def configure_logging(level=logging.DEBUG) -> logging.Logger:
    '''Configures the logging module to display the time, log level, and message.'''
    logging.basicConfig(level=level, format='%(asctime)s - %(levelname)s - %(message)s')
    return logging.getLogger(__name__)

logger = configure_logging(logging.INFO)

def get_date_input() -> datetime:
    '''Prompts the user for a date input and returns a datetime object, defaulting to yesterday if no date is entered.'''
    date_input = input('Enter a date (YYYY-MM-DD): ')
    try:
        if date_input == "":
            date_input = datetime.now() - timedelta(days=1)
            logger.info(f"No date entered, defaulting to yesterday: {date_input.strftime('%Y-%m-%d')}")
        else:
            date_input = datetime.strptime(date_input, '%Y-%m-%d')
    except ValueError:
        logger.error('Invalid date format. Please enter a date in the format YYYY-MM-DD.')
        return get_date_input()
    logger.info(f"Date entered: {date_input.strftime('%Y-%m-%d')}")
    return date_input


def parse_date(image_name: str)-> datetime:
    '''Parses the date from the NASA EPIC image name which is in the format "epic_1b_20241119150054"'''
    date_str = image_name.split('_')[-1]
    try:
        return datetime.strptime(date_str, '%Y%m%d%H%M%S')
    except ValueError as e:
        logger.error(f"Error parsing date from image name {image_name}: {e}")
        return None
    

def save_image(image: Image, path: str):
    '''Saves the image to the local cache.'''
    os.makedirs(os.path.dirname(path), exist_ok=True)
    image.save(path)
    logger.info(f"Saved image {path}.")