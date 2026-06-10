import cv2
import numpy as np
import os

# Configuration
input_dir = "frames/"
output_file = "textures/Cavalier_spritesheet.png"
sheet_size = 1500
slot_size = 150

# 1. Define your layout: Each list represents a row, containing indices of frames
# Example: Row 0 has 4 sprites, Row 1 has 3, etc.
layout = [
    [0, 1, 2, 3, 4, 5, 6, 7, 8],
    [9, 10, 11, 12, 13, 14, 15, 16, 17],
    [42, 43, 44, 45, 46],
    [34, 35, 36, 37, 38, 39, 40, 41],
    [62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72]
]

# Create a transparent 1000x1000 canvas
w_s=max(len(row) for row in layout)*slot_size
h_s=len(layout)*slot_size
spritesheet=np.zeros((h_s,w_s,4),dtype=np.uint8)

for row_idx, frame_indices in enumerate(layout):
    for col_idx, frame_idx in enumerate(frame_indices):

        # Load the specific frame
        frame_path = os.path.join(input_dir, f"frame_{frame_idx:03d}.png")
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
