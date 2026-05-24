import cv2
import numpy as np
import os

image_path="Archer.png"
out_dir="textures/archer_frames"
os.makedirs(out_dir,exist_ok=True)

img=cv2.imread(image_path,cv2.IMREAD_UNCHANGED)

if len(img.shape)==3 and img.shape[2]==4:
    thresh=img[:,:,3]
else:
    gray=cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    _,thresh=cv2.threshold(gray,240,255,cv2.THRESH_BINARY_INV)

contours,_=cv2.findContours(thresh,cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)

# 1. First pass: find the absolute maximum width or height
max_size=0
for cnt in contours:
    x,y,w,h=cv2.boundingRect(cnt)
    if w>max_size:
        max_size=w
    if h>max_size:
        max_size=h

canvas_size=max_size
print(f"Calculated optimal canvas size: {canvas_size}x{canvas_size}")

# 2. Second pass: extract and center the sprites
index=0
for cnt in contours:
    x,y,w,h=cv2.boundingRect(cnt)
    
    if w<5 or h<5:
        continue
        
    sprite=img[y:y+h,x:x+w]
    
    canvas=np.zeros((canvas_size,canvas_size,4),dtype=np.uint8)

    off_x=(canvas_size-w)//2
    off_y=(canvas_size-h)//2

    canvas[off_y:off_y+h,off_x:off_x+w]=sprite

    filename=f"{out_dir}/frame_{index}.png"
    cv2.imwrite(filename,canvas)
    print(f"Generated {filename} (Original size: {w}x{h})")
    index+=1

print(f"Done! Extracted {index} frames into {canvas_size}x{canvas_size} uniform blocks.")