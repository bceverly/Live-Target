# Live Target Design System

## Color Palette

### Primary Colors
- **Red (Default):** `#FF0000` - Used for impact circles and numbers
- **Green:** `#00FF00` - Connection status, start buttons
- **Blue:** `#0000FF` - Save buttons, links
- **Orange:** `#FFA500` - Warning states (partial connection)

### Neutral Colors
- **Black:** `#000000` - Text, borders
- **White:** `#FFFFFF` - Backgrounds, contrast
- **Gray:** `#808080` - Secondary text, disabled states

### Semantic Colors
- **Success:** Green - Connected states, successful operations
- **Warning:** Orange - Partial connectivity, cautions
- **Error:** Red - Disconnected states, errors
- **Info:** Blue - General information, actions

## Typography

### iOS (SwiftUI)
- **Headlines:** `.headline` with `.bold` weight
- **Body Text:** `.body` system font
- **Captions:** `.caption` for secondary information
- **Numbers:** `.headline` with `.bold` for impact numbers

### Android (Future)
- **Headlines:** Material Design `headlineSmall`
- **Body Text:** `bodyMedium`
- **Captions:** `labelSmall`
- **Numbers:** `headlineSmall` with bold weight

## Spacing System

### Standard Spacing Scale
- **XS:** 4px
- **S:** 8px  
- **M:** 16px
- **L:** 24px
- **XL:** 32px
- **XXL:** 48px

### Component Spacing
- **Button padding:** 16px (M)
- **Card margins:** 16px (M)
- **Section spacing:** 24px (L)
- **Screen margins:** 16px (M)

## Component Specifications

### Impact Circles
- **Diameter:** 60px
- **Stroke width:** 3px
- **Color:** Configurable (default red)
- **Animation:** Fade in on detection

### Impact Numbers
- **Font size:** 18px (headline)
- **Font weight:** Bold
- **Color:** Configurable (default red)
- **Position:** Centered in circle

### Buttons
- **Height:** 44px minimum (iOS) / 48px (Android)
- **Corner radius:** 8px
- **Font weight:** Semibold
- **State colors:** Green (start), Red (stop), Blue (save)

### Settings Controls
- **Sliders:** System default styling
- **Color pickers:** Native platform pickers
- **Steppers:** Platform-appropriate controls

## Iconography

### System Icons
- **Watch connectivity:** `applewatch` (SF Symbols)
- **Settings:** `gear` or platform equivalent
- **Camera:** `camera` symbol
- **Save:** `square.and.arrow.down`

### Status Indicators
- **Connected:** Green circle or checkmark
- **Disconnected:** Red circle or X
- **Warning:** Orange triangle or exclamation

## Layout Principles

### Screen Structure
1. **Navigation bar** - Title and controls
2. **Main content** - Camera view with overlays
3. **Control bar** - Start/stop, save, clear buttons
4. **Settings sheet** - Modal configuration

### Responsive Design
- **Portrait orientation** - Primary layout
- **Landscape support** - Adjust control positions
- **Different screen sizes** - Scale appropriately

## Accessibility

### Color Accessibility
- Ensure sufficient contrast ratios (4.5:1 minimum)
- Don't rely solely on color for information
- Support system color preferences

### Text Accessibility
- Support Dynamic Type (iOS) / Font scaling (Android)
- Clear, readable fonts
- Appropriate text sizes

### Interaction Accessibility
- Minimum touch targets: 44x44px (iOS) / 48x48px (Android)
- VoiceOver/TalkBack support
- Clear focus indicators

## Platform Adaptations

### iOS Specific
- Follow iOS Human Interface Guidelines
- Use SF Symbols for icons
- Native navigation patterns
- SwiftUI design patterns

### Android Specific (Future)
- Follow Material Design guidelines
- Use Material icons
- Native Android navigation
- Jetpack Compose patterns

## Brand Identity

### App Identity
- **Name:** Live Target
- **Purpose:** Bullet impact detection
- **Audience:** Shooting enthusiasts, target practice
- **Tone:** Professional, precise, reliable

### Visual Style
- **Clean and minimal** - Focus on functionality
- **High contrast** - Clear visibility in various lighting
- **Responsive** - Immediate visual feedback
- **Consistent** - Predictable interactions