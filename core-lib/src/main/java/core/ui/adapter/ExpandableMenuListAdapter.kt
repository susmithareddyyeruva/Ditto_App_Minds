package core.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import core.lib.R
import core.ui.common.MenuModel
import core.ui.common.Utility
import java.util.*

class ExpandableMenuListAdapter(
    private val context: Context,
    private val listDataHeader: MutableList<MenuModel>,
    private val listChildData: HashMap<MenuModel, List<MenuModel>?>
) : BaseExpandableListAdapter(){

    override fun getChild(groupPosition: Int, childPosititon: Int): MenuModel? {
        return this.listChildData[listDataHeader[groupPosition]]?.get(childPosititon)
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup
    ): View? {
        var convertView = convertView
        val childText: String? = getChild(groupPosition, childPosition)?.menuName
        if (convertView == null) {
            val infalInflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(R.layout.nav_menu_list_child, null)
        }
        val drawable = Utility.getDrawableFromString(
            convertView?.context,
            getChild(groupPosition, childPosition)?.menuImage
        )
        convertView?.findViewById<ImageView>(R.id.ic_menu_image_child)?.setImageDrawable(drawable)
        val childMenuTitle = convertView
            ?.findViewById<TextView>(R.id.ic_menu_text_child)
        childMenuTitle?.text = childText
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return if (listChildData[listDataHeader[groupPosition]] == null) 0 else listChildData[listDataHeader[groupPosition]]!!
            .size
    }

    override fun getGroup(groupPosition: Int): MenuModel? {
        return listDataHeader[groupPosition]
    }

    override fun getGroupCount(): Int {
        return listDataHeader.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup
    ): View? {
        var convertView = convertView
        val headerTitle: String? = getGroup(groupPosition)?.menuName
        if (convertView == null) {
            val infalInflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(R.layout.nav_menu_list_header, null)
        }
        val headerMenuTitle = convertView?.findViewById<TextView>(R.id.ic_menu_text)
        headerMenuTitle?.text = headerTitle
        if(headerTitle == "Log Out"){
            headerMenuTitle?.setTextColor(context.getColor(R.color.logout_red))
        }else if(headerTitle == "Sign in"){
            headerMenuTitle?.setTextColor(context.getColor(R.color.sign_in_blue))
        }else{
            headerMenuTitle?.setTextColor(Color.BLACK)
        }

        val drawable = Utility.getDrawableFromString(
            convertView?.context,
            getGroup(groupPosition)?.menuImage
        )
        convertView?.findViewById<ImageView>(R.id.ic_menu_image)?.setImageDrawable(drawable)

        val isExpand = convertView?.findViewById<ImageView>(R.id.ic_menu_drop_image)
        isExpand?.visibility = if(getGroup(groupPosition)?.subMenu == null) View.GONE else View.VISIBLE

        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }


    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

}