import cv2
import numpy as np
import os

# Configuration
input_dir = "textures\pikeman_frames"
output_file = "textures\pikeman_spritesheet.png"
sheet_size = 1000
slot_size = 125

# 1. Define your layout: Each list represents a row, containing indices of frames
# Example: Row 0 has 4 sprites, Row 1 has 3, etc.
layout = [
    	[7, 4, 3, 2, 1],          # Row 0
    	[11, 10, 9, 8, 5, 6],             # Row 1
	[17,16,15,14,13,12],      # Row 2
    	[23,22,21,20,19,18],
	[75,73,72,74,76,90],
	[121,120,119,118,117],
	[128,129,132,131,130,127],
	[142,141,138,137,133,140,134,135]
]

# Create a transparent 1000x1000 canvas
spritesheet = np.zeros((sheet_size, sheet_size, 4), dtype=np.uint8)

for row_idx, frame_indices in enumerate(layout):
    for col_idx, frame_idx in enumerate(frame_indices):
        
        # Load the specific frame
        frame_path = os.path.join(input_dir, f"frame_{frame_idx}.png")
        if not os.path.exists(frame_path):
            print(f"Warning: {frame_path} not found, skipping.")
            continue
            
        sprite = cv2.imread(frame_path, cv2.IMREAD_UNCHANGED)
        
        # Calculate destination position (top-left corner of the slot)
        start_y = row_idx * slot_size
        start_x = col_idx * slot_size
        
        # Center the sprite within the 125x125 block
        # (Assuming your individual frames were 75x75, this centers them)
        padding_y = (slot_size - sprite.shape[0]) // 2
        padding_x = (slot_size - sprite.shape[1]) // 2
        
        y_pos = start_y + padding_y
        x_pos = start_x + padding_x
        
        # Place the sprite onto the sheet
        spritesheet[y_pos:y_pos+sprite.shape[0], x_pos:x_pos+sprite.shape[1]] = sprite
        print(f"Placed frame_{frame_idx} at row {row_idx}, col {col_idx}")

# Save the final sheet
cv2.imwrite(output_file, spritesheet)
print(f"Spritesheet generated at {output_file}")