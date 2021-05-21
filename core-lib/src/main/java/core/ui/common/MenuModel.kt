package core.ui.common


data class MenuModel (
    val menuName: String?,
    val menuImage: String?,
    val subMenu: List<MenuModel>?
)