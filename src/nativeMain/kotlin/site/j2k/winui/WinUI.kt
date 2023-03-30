import kotlinx.cinterop.*
import platform.windows.*
import site.j2k.winui.Component
import site.j2k.winui.event.*
import site.j2k.winui.getClassName

private fun wndProc(
    hwnd: HWND?,
    msg: UInt,
    wParam: WPARAM,
    lParam: LPARAM
): LRESULT = memScoped {
    when (msg.toInt()) {
        WM_CREATE -> {
            val createStruct = lParam.toCPointer<CREATESTRUCT>()!!.pointed
            WinUI.create(CreationEvent(createStruct))
        }

        WM_DESTROY -> {
            WinUI.destroy(DestroyEvent())
        }

        WM_PAINT -> {
        }

        else -> return DefWindowProc!!(hwnd, msg, wParam, lParam)
    }

    0L
}

private object WinUI : Component() {
    val eventListener = EventListener()

    fun onCreate(handler: EventHandler<CreationEvent>) {
        eventListener.onEvent(CreationEvent, handler)
    }

    fun onDestroy(handler: EventHandler<DestroyEvent>) {
        eventListener.onEvent(DestroyEvent, handler)
    }

    fun create(event: CreationEvent) {
        eventListener.dispatch(CreationEvent, event)
    }

    fun destroy(event: DestroyEvent) {
        eventListener.dispatch(DestroyEvent, event)
    }
}

inline fun winUI(
    title: String? = null,
    x: Int = CW_USEDEFAULT,
    y: Int = CW_USEDEFAULT,
    width: Int = CW_USEDEFAULT,
    height: Int = CW_USEDEFAULT,
    dwStyle: Int = 0,
    init: Component.() -> Unit
) {
    val className = registerClass(getClassName())
    Component(
        className,
        title,
        x, y, width, height,
        dwStyle,
        null,
        null,
        GetModuleHandleA(null),
        null
    ).apply { init() }
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
