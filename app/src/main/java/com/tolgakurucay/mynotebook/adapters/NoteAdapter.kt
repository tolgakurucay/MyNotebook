package com.tolgakurucay.mynotebook.adapters


import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.tolgakurucay.mynotebook.NoteClickListener
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.NoteLayoutBinding
import com.tolgakurucay.mynotebook.models.NoteModel
import com.tolgakurucay.mynotebook.utils.GetCurrentDate
import com.tolgakurucay.mynotebook.utils.Util
import com.tolgakurucay.mynotebook.views.main.FeedFragmentDirections

class NoteAdapter(var noteList:ArrayList<NoteModel>,var completion:(noteList:ArrayList<NoteModel>)->Unit) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private lateinit var binding:NoteLayoutBinding
    private val dateClass= GetCurrentDate()
    val TAG="bilgi"
    val modelList=HashMap<NoteModel,Boolean>()
    val viewIdList=HashMap<Int,Boolean>()
    var modelArrayListEx=ArrayList<NoteModel>()
    var okay=false

    object a:NoteClickListener{
        override fun onItemClick(text: String) {
            Log.d("bilgi", "onItemClick: $text")
        }

    }





    class NoteViewHolder(view:NoteLayoutBinding): RecyclerView.ViewHolder(view.root){


    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        binding=NoteLayoutBinding.inflate(inflater)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder,position: Int) {
        if(!okay){
            okay=true

            for(id in 0 until noteList.size){
                viewIdList.put(holder.adapterPosition,false)
            }
            for(model in noteList){
                modelList.put(model,false)
            }

            Log.d(TAG, "onBindViewHolder: girildiiiiiiiiii")

        }




        //ilk başta layoutId'leri al ve false'e eşitle HashMap<Int,Boolean>
        //long click içerisinde hashmapin ilgili id'si ile karşılaştırma yaptır, eğer false ise true yap0



        binding.textViewTitle.setText(noteList[position].title)
        binding.textViewDate.setText(dateClass.getDateFromLong(noteList[position].date))
       if(noteList[position].imageBase64!=null){
           noteList[position].imageBase64?.let {
               binding.imageViewImage.setImageBitmap(Util.base64ToBitmap(it))
               binding.imageViewImage.background=null
           }
        }

        binding.itemLayout.setOnClickListener {
            val noteModel=noteList[position]
            val action=FeedFragmentDirections.actionFeedFragmentToNoteFragment(noteModel)
            holder.itemView.findNavController().navigate(action)
        }


       holder.itemView.setOnLongClickListener { p0 ->
           p0?.let { view ->



               if(viewIdList.get(holder.adapterPosition)==true){//seçilmişse
                   viewIdList.put(holder.adapterPosition,false)
                 //  modelList.put(noteList[position],true)
                   modelArrayListEx.remove(noteList[position])
                   holder.itemView.setBackgroundColor(android.R.color.transparent)

               }
               else//seçilmemişse
               {
                   holder.itemView.setBackgroundColor(R.color.purple_500)
                   //modelList.put(noteList[position],false)
                   modelArrayListEx.add(noteList[position])
                   viewIdList.put(holder.adapterPosition,true)
               }



                completion(modelArrayListEx)

           }
           true
       }


    }

    override fun getItemCount(): Int {
      
        return noteList.size
    }

    fun updateNoteList(newNoteList:List<NoteModel>){
        noteList.clear()
        noteList= ArrayList(newNoteList)
        notifyDataSetChanged()
    }



}

