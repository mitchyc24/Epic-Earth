from typing import Union
from datetime import datetime
from PIL import Image
import os


def create_gif(date: Union[datetime, str] = None, fps: int = 10):
    '''Creates a GIF from the EPIC images for a given date.'''
    if isinstance(date, str):
        try:
            date = datetime.strptime(date, '%Y-%m-%d')
        except ValueError:
            print("Invalid date format. Please enter a date in the format YYYY-MM-DD.")
            return

    # Get the images for the given date
    images = get_images_for_date(date)

    # Create the GIF
    gif_path = f'data/gifs/{date.strftime("%Y-%m-%d")}.gif'
    images[0].save(gif_path, save_all=True, append_images=images[1:], duration=1000 // fps, loop=0)
    print(f"GIF created: {gif_path}")
    return


def get_images_for_date(date: datetime):
    '''Fetches the EPIC images for a given date.'''
    '''images are stored in the local cache, under the data/images directory.'''

    images = []
    # Loop through the images in the cache
    for image in os.listdir(f'data/images/{date.strftime("%Y-%m-%d")}'):
        # Load the image
        image_path = f'data/images/{date.strftime("%Y-%m-%d")}/{image}'
        img = Image.open(image_path)
        images.append(img)
    return images


if __name__ == '__main__':
    create_gif("2016-02-24")
