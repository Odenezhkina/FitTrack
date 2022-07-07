package ru.kpfu.itis.fittrack.listForTheDay

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.kpfu.itis.fittrack.data.BaseEntity
import ru.kpfu.itis.fittrack.data.Product
import ru.kpfu.itis.fittrack.data.Recipe
import ru.kpfu.itis.fittrack.databinding.TrainingFoodForTheDayItemBinding
import ru.kpfu.itis.fittrack.fragments.ProductDescriptionFragment

class ListForTheDayAdapter(
    private val list: MutableList<BaseEntity>,
    private val onItemClick: (BaseEntity) -> Unit
) : RecyclerView.Adapter<ListForTheDayViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListForTheDayViewHolder =
        ListForTheDayViewHolder(
            TrainingFoodForTheDayItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClick = onItemClick
        )


    override fun onBindViewHolder(holder: ListForTheDayViewHolder, position: Int) {
        holder.onBind(list[position])

    }


    // it could not be worse, but it works.................
    // don't judge
    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(id: Int, context: Context, activity: Activity) {
        val item = list.get(id)
        changeSharedPref(activity, item)
        val sharedPreferencesStorage = SharedPreferencesStorage(context)
        val idSArr = sharedPreferencesStorage.loadIDS()?.split(" ")?.toMutableList()
        val categories = sharedPreferencesStorage.loadCategories()?.split(" ")?.toMutableList()
        val types = sharedPreferencesStorage.loadTypes()?.split(" ")?.toMutableList()
        var i = 0

        if (idSArr != null) {
            for (idd in idSArr) {
                val category = categories?.get(i)
                val type = types?.get(i)
                if (idd == (item.id - 1).toString() && category == item.category && type == item.type) {
                    break
                }
                i++
            }
        }
        idSArr?.removeAt(i)
        categories?.removeAt(i)
        types?.removeAt(i)
        sharedPreferencesStorage.clearAll()
        if (idSArr != null) {
            for ((s, k) in idSArr.withIndex()) {
                val category = categories?.get(s)
                val type = types?.get(s)
                if (!k.isNullOrBlank() && !category.isNullOrBlank() && !type.isNullOrBlank()) {
                    sharedPreferencesStorage.addCategory(category)
                    sharedPreferencesStorage.addItemID(k.toInt())
                    sharedPreferencesStorage.addType(type)
                }
            }
        }
        list.removeAt(id)
        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun addItem(i: Int, itemForTheDay: BaseEntity) {
        list.add(i, itemForTheDay)


        //todo proper comparator should be implemented
        val selector: (BaseEntity) -> Int = { item -> item.category.length }
        list.sortBy(selector)
        list.reverse()
        notifyDataSetChanged()
    }

    fun getItemList(): List<BaseEntity> {
        return list
    }

    override fun getItemCount(): Int = list.size

    private fun changeSharedPref(activity: Activity, baseEntity: BaseEntity) {
        val sharedPref = activity.getSharedPreferences(
            "UserData",
            Context.MODE_PRIVATE
        )
        var eatenCalories = sharedPref?.getInt(ProductDescriptionFragment.EATEN_CALORIES, 0)
        var eatenProteins = sharedPref?.getFloat(ProductDescriptionFragment.EATEN_PROTEINS, 0f)
        var eatenCarbs = sharedPref?.getFloat(ProductDescriptionFragment.EATEN_CARBS, 0f)
        var eatenFats = sharedPref?.getFloat(ProductDescriptionFragment.EATEN_FATS, 0f)
        val editor = sharedPref?.edit()

        eatenCalories = eatenCalories?.minus(baseEntity.calories)
        if (baseEntity is Product) {
            eatenProteins = eatenProteins?.minus(baseEntity.proteins)
            eatenCarbs = eatenCarbs?.minus(baseEntity.carbohydrates)
            eatenFats = eatenFats?.minus(baseEntity.fats)
        }
        if (baseEntity is Recipe) {
            eatenProteins = eatenProteins?.minus(baseEntity.proteins)
            eatenCarbs = eatenCarbs?.minus(baseEntity.carbohydrates)
            eatenFats = eatenFats?.minus(baseEntity.fats)
        }

        editor?.putInt(ProductDescriptionFragment.EATEN_CALORIES, eatenCalories!!)?.apply()
        editor?.putFloat(ProductDescriptionFragment.EATEN_PROTEINS, eatenProteins!!)?.apply()
        editor?.putFloat(ProductDescriptionFragment.EATEN_CARBS, eatenCarbs!!)?.apply()
        editor?.putFloat(ProductDescriptionFragment.EATEN_FATS, eatenFats!!)?.apply()

    }
}