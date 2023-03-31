package site.j2k.winui

import kotlinx.cinterop.invoke
import kotlinx.cinterop.toCPointer
import platform.posix.rand
import platform.windows.*
import site.j2k.winui.utils.BasicComponents
import site.j2k.winui.utils.createWindow

const val CLASS_NAME = "MyUniqueWindowClass"
var counter = rand() % 123456

fun getClassName() = CLASS_NAME + counter++

open class Component(
    val className: String? = null,
    val windowName: String? = null,
    val x: Int = CW_USEDEFAULT,
    val y: Int = CW_USEDEFAULT,
    val width: Int = CW_USEDEFAULT,
    val height: Int = CW_USEDEFAULT,
    dwStyle: Int = 0,
    val parent: HWND? = null,
    hMenu: HMENU? = null,
    hInstance: HINSTANCE? = null,
    lpParam: LPVOID? = null
) {
    val hwnd: HWND = createWindow(
        className, windowName,
        x, y, width, height,
        dwStyle,
        parent,
        hMenu,
        hInstance,
        lpParam
    ) ?: throw Exception("Cannot create component $className")

    private val _children = mutableListOf<Component>()
    val children: List<Component> by this::_children

    fun addChild(child: Component): Component {
        _children.add(child)
        return child
    }
}

class Button(
    text: String? = null,
    x: Int = CW_USEDEFAULT,
    y: Int = CW_USEDEFAULT,
    width: Int = CW_USEDEFAULT,
    height: Int = CW_USEDEFAULT,
    dwStyle: Int = (WS_TABSTOP or WS_VISIBLE or WS_CHILD or BS_DEFPUSHBUTTON),
    parent: HWND? = null,
    hMenu: HMENU? = null,
    lpParam: LPVOID? = null
) : Component(
    BasicComponents.BUTTON.toString(), text,
    x, y, width, height,
    dwStyle, parent,
    hMenu,
    GetWindowLongPtr!!(
        parent, GWLP_HINSTANCE
    ).toCPointer(),
    lpParam
)

inline fun Component.button(
    text: String? = null,
    x: Int = this.x,
    y: Int = this.y,
    width: Int = CW_USEDEFAULT,
    height: Int = CW_USEDEFAULT,
    dwStyle: Int = 0,
    init: Button.() -> Unit = {}
) = this.addChild(
    Button(text, x, y, width, height, dwStyle, this.hwnd).apply { init() }
)
