package site.j2k.winui.utils

import platform.windows.*

fun createWindow(
    className: String? = null,
    windowName: String? = null,
    x: Int = CW_USEDEFAULT,
    y: Int = CW_USEDEFAULT,
    width: Int = CW_USEDEFAULT,
    height: Int = CW_USEDEFAULT,
    dwStyle: Int = 0,
    parent: HWND? = null,
    hMenu: HMENU? = null,
    hInstance: HINSTANCE? = null,
    lpParam: LPVOID? = null
) = CreateWindowExW(
    0u,
    className, windowName,
    dwStyle.toUInt(),
    x, y, width, height,
    parent,
    hMenu,
    hInstance,
    lpParam
)

enum class BasicComponents {
    BUTTON,
    COMBOBOX,
    EDIT,
    LISTBOX,
    MDICLIENT,
    RICHEDIT_CLASS,
    SCROLLBAR,
    STATIC
}
