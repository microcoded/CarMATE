package fail.enormous.carmate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerAdapter(private val context: Context, private val listRecyclerItem: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Setting values for all the TextViews
        val brand: TextView = itemView.findViewById<View>(R.id.brand) as TextView
        // val model: TextView = itemView.findViewById<View>(R.id.model) as TextView
        val year: TextView = itemView.findViewById<View>(R.id.year) as TextView
        val color: TextView = itemView.findViewById<View>(R.id.color) as TextView
        val type: TextView = itemView.findViewById<View>(R.id.type) as TextView
        val price: TextView = itemView.findViewById<View>(R.id.price) as TextView

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return when (i) {
            TYPE -> {
                val layoutView = LayoutInflater.from(viewGroup.context).inflate(
                        R.layout.list_item, viewGroup, false)
                ItemViewHolder(layoutView)
            }
            else -> {
                val layoutView = LayoutInflater.from(viewGroup.context).inflate(
                        R.layout.list_item, viewGroup, false)
                ItemViewHolder(layoutView)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val viewType = getItemViewType(i)
        when (viewType) {
            TYPE -> {
                val itemViewHolder = viewHolder as ItemViewHolder
                // Get CarList as array
                val carlist: Car = listRecyclerItem[i] as Car
                // Set text to display accordingly, formatting with capitalisation and dollar signs where relevant.
                itemViewHolder.brand.setText(carlist.brand + " " + carlist.model)
                itemViewHolder.year.setText(context.getString(R.string.year_title, carlist.year))
                itemViewHolder.color.setText(context.getString(R.string.color_title, carlist.color.capitalize()))
                itemViewHolder.type.setText(context.getString(R.string.type_title, carlist.type.capitalize()))
                itemViewHolder.price.setText(context.getString(R.string.price_title, "$" + carlist.price))
                /* itemViewHolder.brand.setText(carlist.brand)
                itemViewHolder.model.setText(carlist.model)
                itemViewHolder.year.setText(carlist.year)
                itemViewHolder.color.setText(carlist.color)
                itemViewHolder.type.setText(carlist.type)
                itemViewHolder.price.setText(carlist.price) */
            }
            else -> {
                val itemViewHolder = viewHolder as ItemViewHolder
                val carlist: Car = listRecyclerItem[i] as Car
                itemViewHolder.brand.setText(carlist.brand + " " + carlist.model)
                itemViewHolder.year.setText(context.getString(R.string.year_title, carlist.year))
                itemViewHolder.color.setText(context.getString(R.string.color_title, carlist.color.capitalize()))
                itemViewHolder.type.setText(context.getString(R.string.type_title, carlist.type.capitalize()))
                itemViewHolder.price.setText(context.getString(R.string.price_title, "$" + carlist.price))
            }
        }
    }

    override fun getItemCount(): Int {
        return listRecyclerItem.size
    }

    /* private fun capitaliseString(str: String): String {
        var stringy = str
        try {
            stringy = str.substring(0, 1).toUpperCase() + str.substring(1) // Make first letter a capital
        }
        catch (e: Exception) {
            // If string is null, index out of bounds exception is caught
        }
        return stringy
    } */

    companion object {
        private const val TYPE = 1
    }
}