import os
import requests
import logging
from EPIC_Image import EPIC_Image
from dotenv import load_dotenv # type: ignore
from datetime import datetime

dotenv_path = os.path.join(os.path.dirname(__file__), '..', '.env')
load_dotenv(dotenv_path=dotenv_path)

API_KEY = os.environ.get('NASA_API_KEY')

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
