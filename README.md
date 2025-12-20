# BetterJump
An IntelliJ IDEA plugin for better in-file navigation.

## Overview
BetterJump introduces precise, customizable navigation shortcuts that make moving inside code files much faster.  
You can jump between blocks, words, or commas, and even select sections intelligently — without ever touching the mouse.

## Default Keyboard Shortcuts

### Block Navigation
| Action | Description | Default Shortcut |
|--------|--------------|------------------|
| Go to Next Block | Moves caret to the first non-whitespace character of the next `{}` block. | Shift + Ctrl + Alt + ] |
| Go to Previous Block | Moves caret to the first non-whitespace character of the previous `{}` block. | Shift + Ctrl + Alt + [ |
| Go to Inner Block | Moves caret to the start of the inner block’s content. | Shift + Alt + ] |
| Go to Outer Block | Moves caret to the start of the outer block’s content. | Shift + Alt + [ |
| Select Code Block | Selects the code inside the current `{}` block. | Shift + Ctrl + Alt + W |

### Comma Navigation
| Action | Description | Default Shortcut |
|--------|--------------|------------------|
| Go To Next Comma | Moves caret to the next comma. | Ctrl + , |
| Go To Previous Comma | Moves caret to the previous comma. | Ctrl + Shift + , |
| Select Content To Next Comma | Selects the text between commas to the right. | Ctrl + Alt + , |
| Select Content To Previous Comma | Selects the text between commas to the left. | Ctrl + Alt + Shift + , |

### Horizontal / Word Navigation
| Action | Description | Default Shortcut |
|--------|--------------|------------------|
| Jump Horizontally | Jumps caret horizontally by a preset number of characters. | Shift + Ctrl + Alt + X |
| Set Horizontal Jump Amount | Opens dialog to set the horizontal jump size. | Shift + Ctrl + G or Shift + Ctrl + X |
| Invert Jump Horizontal Direction | Inverts the direction of horizontal jumps (left ↔ right). | Shift + Alt + X |
| Jump To Right (Word) | Jumps caret to the next word start (configurable X distance). | Ctrl + Shift + Alt + → |
| Jump To Left (Word) | Jumps caret to the previous word start (configurable X distance). | Ctrl + Shift + Alt + ← |


## Tree Navigation

Quick vertical navigation for tree-based UI components such as the **Project Window** and **Structure View**.

| Action | Description | Default Shortcut |
|--------|-------------|------------------|
| Next Node Jump Down | Jumps the selection down by a fixed amount in a tree structure. | Ctrl + ↓ |
| Next Node Jump Up | Jumps the selection up by a fixed amount in a tree structure. | Ctrl + ↑ |


## Author
**Stav Gordeev**  
Email: stav.gr8@gmail.com

## Installation
1. Download the plugin JAR (or install from JetBrains Marketplace, if available).  
2. In IntelliJ IDEA, go to **Settings → Plugins → Install plugin from disk…** and select the JAR file.  
3. Restart the IDE to activate the plugin.  
4. (Optional) Open **Settings → Keymap → BetterJump** to view or remap shortcuts.
