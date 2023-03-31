package site.j2k.winui

import kotlinx.cinterop.*
import platform.windows.*
import site.j2k.winui.event.*


fun messageLoop() {
    memScoped {
        val msg = alloc<MSG>()
        while (GetMessageA(msg.ptr, null, 0u, 0u) > 0) {
            TranslateMessage(msg.ptr)
            DispatchMessageA(msg.ptr)
        }
    }
}

private fun wndProc(
    hwnd: HWND?,
    msg: UInt,
    wParam: WPARAM,
    lParam: LPARAM
): LRESULT = memScoped {
    when (msg.toInt()) {
        WM_CREATE -> {
            val createStruct = lParam.toCPointer<CREATESTRUCT>()!!.pointed
            mainWindow?.create(CreationEvent(createStruct))
        }

        WM_DESTROY -> {
            mainWindow?.destroy(DestroyEvent())
            PostQuitMessage(0)
        }

        WM_PAINT -> {
        }

        else -> return DefWindowProc!!(hwnd, msg, wParam, lParam)
    }

    0L
}

private var mainWindow: WinUI? = null

/**
 * Main window class
 *
 * NOTE: you should not use this class yourself. Use the [winUI] method
 *
 * @see [winUI]
 */
class WinUI(
    title: String? = null,
    x: Int = CW_USEDEFAULT,
    y: Int = CW_USEDEFAULT,
    width: Int = CW_USEDEFAULT,
    height: Int = CW_USEDEFAULT,
    dwStyle: Int = 0,
) : Component(
    registerClass(getClassName()),
    title,
    x, y, width, height,
    dwStyle,
    null,
    null,
    GetModuleHandleA(null),
    null
) {
    private val eventListener = EventListener()

    fun onCreate(handler: EventHandler<CreationEvent>) {
        eventListener.onEvent(CreationEvent, handler)
    }

    fun onDestroy(handler: EventHandler<DestroyEvent>) {
        eventListener.onEvent(DestroyEvent, handler)
    }

    internal fun create(event: CreationEvent) {
        eventListener.dispatch(CreationEvent, event)
    }

    internal fun destroy(event: DestroyEvent) {
        eventListener.dispatch(DestroyEvent, event)
    }
}

inline fun winUI(
    title: String? = null,
    x: Int = CW_USEDEFAULT,
    y: Int = CW_USEDEFAULT,
    width: Int = CW_USEDEFAULT,
    height: Int = CW_USEDEFAULT,
    dwStyle: Int = (WS_OVERLAPPED or WS_CAPTION or WS_SYSMENU or WS_MINIMIZEBOX),
    useMessageLoop: Boolean = true,
    init: Component.() -> Unit = {}
) {
    WinUI(
        title,
        x, y,
        width, height,
        dwStyle
    ).apply {
        init()
        ShowWindow(hwnd, SW_SHOWDEFAULT)
        UpdateWindow(hwnd)
    }

    if (useMessageLoop) messageLoop()
}

fun registerClass(className: String = getClassName()) = memScoped {
    val wndClass = alloc<WNDCLASS>().apply {
        lpfnWndProc = staticCFunction(::wndProc)
        hInstance = GetModuleHandleA(null)
        lpszClassName = className.wcstr.ptr
    }

    if (RegisterClass!!(wndClass.ptr) == 0.toUShort())
        throw Exception("Cannot register class $className")

    return@memScoped className
}
