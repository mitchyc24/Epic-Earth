import os
from utils import get_date_input, configure_logging
from fetch_images import get_EPIC_images_on_date
import logging
from datetime import datetime, timedelta



logger = configure_logging(logging.INFO)


def get_images_for_date_range(start_date: datetime, end_date: datetime):
    date = start_date
    while date <= end_date:
        get_EPIC_images_on_date(date)
        date += timedelta(days=1)

if __name__ == '__main__':
    
    start_date = get_date_input()
    end_date = get_date_input()
    logger.info(f"Fetching images for date range {start_date.strftime('%Y-%m-%d')} to {end_date.strftime('%Y-%m-%d')}")
    get_images_for_date_range(start_date, end_date)