import sys
from PIL import Image

def create_gif(image_paths, output_path):
    images = [Image.open(image_path) for image_path in image_paths]
    images[0].save(output_path, save_all=True, append_images=images[1:], duration=500, loop=0)

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python create_gif.py <output_path> <image_path1> <image_path2> ...")
        sys.exit(1)

    output_path = sys.argv[1]
    image_paths = sys.argv[2:]
    create_gif(image_paths, output_path)