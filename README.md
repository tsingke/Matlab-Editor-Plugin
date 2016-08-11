Welcome to the Matlab-Editor-Plugin wiki!

## What is it?
It'll add following features to Matlab, and it's editor
* KeyPressListener: executes custom matlab functions passed in by the user beforehand
* ClipboardStack: stores the previous 10 strings copied to clipboard from matlab.
* FileStructure: searching through functions and sections

## Setup
1. you need Matlab, at least 2014a (i only tried from 2014a to 2016b_pre)
2. you need to download the matconsolectl package here [https://github.com/diffplug/matconsolectl](https://github.com/diffplug/matconsolectl) (is included in download page: All code up to tags/original:
Copyright (c) 2013, Joshua Kaplan)
3. create a Matlab function <MyKeyReleaseFunction>
    ```Matlab
    function out = MyKeyReleaseFunction(keyEvent)
    if nargin == 0; return; end
    % this is necessary, the first thing happen is trying find out whether 
    % the passed function is a valid matlab function or not
    ctrlFlag = evnt.isControlDown;
    shiftFlag = evnt.isShiftDown;
    altFlag = evnt.isAltDown;
    ctrlShiftFlag = ctrlFlag && shiftFlag && ~altFlag;
    ctrlShiftAltFlag = ctrlFlag && shiftFlag && altFlag;
    ctrlOnlyFlag = ctrlFlag && ~shiftFlag && ~altFlag;
    altOnlyFlag = ~ctrlFlag && ~shiftFlag && altFlag;`
    
    if altOnlyFlag && evnt.getKeyCode == evnt.VK_INSERT
        fprintf('ALT + INSERT\n')
    end
```
4. create `DefaultProps.properties` and `CustomProps.properties` in desired folder.
5. start Matlab and run following
```Matlab
    javaaddpath('<path>\matconsolectl-4.4.1.jar')
    javaaddpath('<path>\MEP_1632a.jar')
    at.justin.matlab.util.Settings.loadSettings(<path>/CustomProps.properties',...
                                          <path>/DefaultProps.properties')
    ea = at.justin.matlab.EditorApp.getInstance();
    ea.setCallbacks();
    ea.addMatlabCallback('MyKeyReleaseFunction');
```

## Predefined Features
### Clipboard Stack
Accessible via `CTRL + SHIFT + V`. Can be closed by pressing `ESCAPE`, moved around by dragging the frame. A double click will insert selected text from ClipboardStack to matlab 
### File Structure
Accessible via `CTRL + F12`. Can be closed by pressing `ESCAPE`, moved around by dragging the frame. Will show class methods and functions, as well as sections in a tree. Selecting a node will jump to selected function/method/section. Searching Sections, and Functions using simple contains or regular expressions (toggleable in focused window via `CTRL + F12`).
### Details View
If active editor is changed, details view will now be automatically synched. This behaviour can be changed if `autoDetailViewer` is set to `false` in `CustomProps.properties`.
