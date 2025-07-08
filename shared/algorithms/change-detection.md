# Change Detection Algorithm

## Overview
The core algorithm for detecting bullet impacts on targets through image comparison.

## Algorithm Steps

### 1. Image Preprocessing
- Convert images to consistent color space (RGB)
- Extract pixel data from camera frames
- Handle image orientation (rotation correction)

### 2. Difference Detection
```
For each pixel (x,y):
  diff = |pixel1.red - pixel2.red| + |pixel1.green - pixel2.green| + |pixel1.blue - pixel2.blue|
  if diff > threshold:
    mark as changed pixel
```

### 3. Region Grouping (Flood Fill)
- Group adjacent changed pixels into regions
- Calculate bounding box for each region
- Filter regions by minimum size (caliber * 2)

### 4. Impact Point Calculation
```
centerX = (region.minX + region.maxX) / 2
centerY = (region.minY + region.maxY) / 2
normalizedX = centerX / imageWidth
normalizedY = centerY / imageHeight
```

## Configuration Parameters

| Parameter | Description | Default | Range |
|-----------|-------------|---------|-------|
| `threshold` | Color difference threshold | 50 | 0-255 |
| `checkInterval` | Time between checks (seconds) | 2.0 | 0.1-10.0 |
| `minChangeSize` | Minimum region size (pixels) | caliber * 2 | 1-100 |

## Platform Considerations

### iOS Implementation
- Uses `UIImage` and `CGImage` for image handling
- Pixel data extracted via `CGContext`
- Coordinates normalized to 0-1 range

### Android Implementation (Future)
- Will use `Bitmap` and `Canvas` for image handling
- Pixel data via `Bitmap.getPixels()`
- Same normalization approach

## Performance Notes
- Algorithm runs on background thread
- Throttled by `checkInterval` to prevent excessive CPU usage
- Large images are downscaled for processing efficiency

## Testing Considerations
- Test with various lighting conditions
- Validate with different bullet calibers
- Verify accuracy with known impact positions