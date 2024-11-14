from dataclasses import dataclass
from datetime import datetime
from typing import Dict, Any


@dataclass
class EPIC_Image:
    identifier: str
    caption: str
    image: str
    version: str
    date: datetime

    @staticmethod
    def from_dict(data: Dict[str, Any]) -> 'EPIC_Image':
        """
        Initializes an EPIC_Image instance from a dictionary.

        Parameters:
            data (Dict[str, Any]): A dictionary containing EPIC_Image data.

        Returns:
            EPIC_Image: An instance of EPIC_Image populated with the provided data.
        """
        # Parse the date string into a datetime object
        date_str = data.get('date')
        try:
            parsed_date = datetime.strptime(date_str, '%Y-%m-%d %H:%M:%S') if date_str else None
        except ValueError as e:
            raise ValueError(f"Incorrect date format for 'date': {date_str}") from e


        return EPIC_Image(
            identifier=data.get('identifier', ''),
            caption=data.get('caption', ''),
            image=data.get('image', ''),
            version=data.get('version', ''),
            date=parsed_date,
        )