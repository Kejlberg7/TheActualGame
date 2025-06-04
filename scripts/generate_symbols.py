from PIL import Image, ImageDraw

# Create a new image with a transparent background
width = 128
height = 32
image = Image.new('RGBA', (width, height), (0, 0, 0, 0))
draw = ImageDraw.Draw(image)

# Function to draw a symbol in a 32x32 cell
def draw_symbol(x, symbol_type):
    cell_size = 32
    padding = 4
    if symbol_type == 'speed_up':
        # Draw double arrow up
        points = [(x + 16, padding), (x + 8, padding + 12), (x + 24, padding + 12)]
        draw.polygon(points, fill=(255, 255, 255, 255))
        points = [(x + 16, padding + 16), (x + 8, padding + 28), (x + 24, padding + 28)]
        draw.polygon(points, fill=(255, 255, 255, 255))
    elif symbol_type == 'speed_down':
        # Draw double arrow down
        points = [(x + 16, padding + 28), (x + 8, padding + 16), (x + 24, padding + 16)]
        draw.polygon(points, fill=(255, 255, 255, 255))
        points = [(x + 16, padding + 12), (x + 8, padding), (x + 24, padding)]
        draw.polygon(points, fill=(255, 255, 255, 255))
    elif symbol_type == 'shots_up':
        # Draw plus symbol
        draw.rectangle([x + 14, padding + 4, x + 18, padding + 28], fill=(255, 255, 255, 255))
        draw.rectangle([x + 4, padding + 14, x + 28, padding + 18], fill=(255, 255, 255, 255))
    elif symbol_type == 'shots_down':
        # Draw minus symbol
        draw.rectangle([x + 4, padding + 14, x + 28, padding + 18], fill=(255, 255, 255, 255))

# Draw all symbols
draw_symbol(0, 'speed_up')
draw_symbol(32, 'speed_down')
draw_symbol(64, 'shots_up')
draw_symbol(96, 'shots_down')

# Save the image
image.save('assets/symbols.png') 