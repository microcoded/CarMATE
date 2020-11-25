import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fail.enormous.carmate.CarList

class RecyclerAdapter(private val context: Context, private val listRecyclerItem: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brand: TextView = itemView.findViewById<View>(R.id.brand) as TextView
        val model: TextView = itemView.findViewById<View>(R.id.model) as TextView
        val year: TextView = itemView.findViewById<View>(R.id.year) as TextView
        val color: TextView = itemView.findViewById<View>(R.id.color) as TextView
        val type: TextView = itemView.findViewById<View>(R.id.type) as TextView
        val price: TextView = itemView.findViewById<View>(R.id.price) as TextView
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return when (i) {
            TYPE -> {
                val layoutView: View = LayoutInflater.from(viewGroup.context).inflate(
                    R.layout.list_item, viewGroup, false
                )
                ItemViewHolder(layoutView)
            }
            else -> {
                val layoutView: View = LayoutInflater.from(viewGroup.context).inflate(
                    R.layout.list_item, viewGroup, false
                )
                ItemViewHolder(layoutView)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val viewType = getItemViewType(i)
        when (viewType) {
            TYPE -> {
                val itemViewHolder = viewHolder as ItemViewHolder
                val carlist: Carlist = listRecyclerItem[i] as CarList.CarList
                itemViewHolder.brand.setText(carlist.getBrand())
                itemViewHolder.model.setText(carlist.getModel())
                itemViewHolder.year.setText(carlist.getYear())
                itemViewHolder.color.setText(carlist.getColor())
                itemViewHolder.type.setText(carlist.getType())
                itemViewHolder.price.setText(carlist.getPrice())
            }
            else -> {
                val itemViewHolder = viewHolder as ItemViewHolder
                val carlist: Carlist = listRecyclerItem[i] as CarList
                itemViewHolder.brand.setText(carlist.getBrand())
                itemViewHolder.model.setText(carlist.getModel())
                itemViewHolder.year.setText(carlist.getYear())
                itemViewHolder.color.setText(carlist.getColor())
                itemViewHolder.type.setText(carlist.getType())
                itemViewHolder.price.setText(carlist.getPrice())
            }
        }
    }

    override fun getItemCount(): Int {
        return listRecyclerItem.size
    }

    companion object {
        private const val TYPE = 1
    }
}