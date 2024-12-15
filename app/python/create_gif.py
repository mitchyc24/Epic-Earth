import sys
import os
import logging
import time
from PIL import Image

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

def create_gif(image_paths, output_path):
    start_time = time.time()
    images = [Image.open(image_path) for image_path in image_paths]
    images[0].save(output_path, save_all=True, append_images=images[1:], duration=500, loop=0)
    end_time = time.time()
    logging.info(f"GIF created at {output_path} in {end_time - start_time:.2f} seconds")

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python create_gif.py <output_path> <image_path1> <image_path2> ...")
        sys.exit(1)

    output_path = sys.argv[1]
    image_paths = sys.argv[2:]

    # Ensure the output directory exists
    output_dir = os.path.dirname(output_path)
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    logging.info("Starting GIF creation")
    create_gif(image_paths, output_path)
    logging.info("GIF creation completed")