package tokyo.mp015v.mycamera

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class ListAdapter(context: Context, resource : Int, items : List<ListItem>) : ArrayAdapter<ListItem>(context,resource,items){
    val mResource = resource
    val mItems = items
    val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position : Int, convertView : View?, parent : ViewGroup?) : View{
        lateinit var view : View

        if( convertView != null){
            view = convertView
        }else{
            view = mInflater.inflate( mResource , null)
        }

        val item = mItems.get( position )

        val thumbnail = view.findViewById<ImageView>(R.id.thumbnail)
        thumbnail.setImageBitmap( item.thumbnail )

        val title = view.findViewById<TextView>(R.id.title)
        title.text = item.title
        return view
    }


}