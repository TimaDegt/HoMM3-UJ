import os
from PIL import Image

def slice_sprite_sheet(image_path, sprite_w=58, sprite_h=64, gap=5, output_dir="output_icons"):
    # Create output directory if it doesn't exist
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
        
    # Open the sprite sheet
    try:
        sheet = Image.open(image_path)
    except FileNotFoundError:
        print(f"Error: Could not find the file '{image_path}'. Make sure it's in the same folder.")
        return

    sheet_w, sheet_h = sheet.size
    
    # Calculate total columns and rows based on your spacing formula:
    # Total width = columns * sprite_w + (columns - 1) * gap
    # Rearranged: columns = (sheet_w + gap) // (sprite_w + gap)
    cols = (sheet_w + gap) // (sprite_w + gap)
    rows = (sheet_h + gap) // (sprite_h + gap)
    
    print(f"Detected sheet size: {sheet_w}x{sheet_h}")
    print(f"Slicing into {cols} columns and {rows} rows...")

    count = 0
    for r in range(rows):
        for c in range(cols):
            # Your math: (i, j) starts at (63i, 69j)
            # Left x = c * (58 + 5) = c * 63
            # Top y = r * (64 + 5) = r * 69
            left = c * (sprite_w + gap)
            top = r * (sprite_h + gap)
            right = left + sprite_w
            bottom = top + sprite_h
            
            # Double check we don't crop outside the actual image boundaries
            if right <= sheet_w and bottom <= sheet_h:
                # Crop the bounding box: (left, upper, right, lower)
                icon = sheet.crop((left, top, right, bottom))
                
                # Save the individual icon
                icon_name = f"icon_{r}_{c}.png"
                icon.save(os.path.join(output_dir, icon_name))
                count += 1

    print(f"Success! Saved {count} icons to the '{output_dir}' directory.")

# Run the function
if __name__ == "__main__":
    slice_sprite_sheet("sheet.png")