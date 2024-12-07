import os
from dataclasses import dataclass
from datetime import datetime
from typing import Dict, Any
from PIL import Image
import logging
import requests
from io import BytesIO
from utils import parse_date, configure_logging, save_image

# Configure logging
logger = configure_logging(logging.DEBUG)

@dataclass
class EPIC_Image:
    identifier: str
    caption: str
    image_name: str
    version: str
    date: datetime
    image: Image = None



    @staticmethod
    def get_image(image_name: str)-> Image:
        '''Retrieves the image for the EPIC_Image instance either from the local cache or the NASA EPIC API.'''

        date = parse_date(image_name)
        local_image_path = os.path.join('data', 'images', date.strftime("%Y-%m-%d"), f'{image_name}.png')
        logger.info(f"Local image path: {local_image_path}")

        #Check local cache for image
        if os.path.exists(local_image_path):
            image = Image.open(local_image_path)
            logger.info(f"Loaded image {image_name} from cache.")
            return image
        
        logger.info(f"Image {image_name} not found in cache, fetching from NASA API.")

        url_endpoint = f'https://epic.gsfc.nasa.gov/archive/natural/{date.year}/{date.month:02}/{date.day:02}/png/{image_name}.png'
        try:
            response = requests.get(url_endpoint)
            response.raise_for_status()  # Raises an error for bad responses
            image = Image.open(BytesIO(response.content))
            logger.info(f"Downloaded image {image_name}")
            save_image(image, local_image_path)
            return image
        
        except requests.exceptions.RequestException as e:
            logger.error(f"Error fetching image {image_name}: {e}")
            return None
        

    @staticmethod
    def from_dict(data: Dict[str, Any]) -> 'EPIC_Image':
        """
        Initializes an EPIC_Image instance from a dictionary.

        Parameters:
            data (Dict[str, Any]): A dictionary containing EPIC_Image data.

        Returns:
            EPIC_Image: An instance of EPIC_Image populated with the provided data.
        """
        date = parse_date(data.get('image', ''))
        image = EPIC_Image.get_image(data.get('image', ''))
        

        return EPIC_Image(
            identifier=data.get('identifier', ''),
            caption=data.get('caption', ''),
            image_name=data.get('image', ''),
            version=data.get('version', ''),
            date=date,
            image=image
        )
    

