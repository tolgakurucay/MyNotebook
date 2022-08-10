package com.tolgakurucay.mynotebook.adapters


import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
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
    val viewIdList=HashMap<Int,Boolean>()
    var modelArrayListEx=ArrayList<NoteModel>()
    var okay=false





    class NoteViewHolder(val view:NoteLayoutBinding): RecyclerView.ViewHolder(view.root)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        binding=NoteLayoutBinding.inflate(inflater)
        return NoteViewHolder(binding)
    }


    fun viewIdListSetFalse(){
        viewIdList.clear()
        Log.d(TAG, "viewIdListSetFalse: ${viewIdList.size}")
        for(id in 0 until noteList.size){
            viewIdList.put(id,false)
        }
    }
    fun modelArrayListClear(){
        modelArrayListEx.clear()

    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        if(!okay){
            okay=true
            viewIdListSetFalse()
            Log.d(TAG, "onBindViewHolder: girildiiiiiiiiii")
        }


        binding.textViewTitle.text = noteList[position].title
        binding.textViewDate.text = dateClass.getDateFromLong(noteList[position].date)
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
                  /* viewIdList.put(holder.adapterPosition,false)
                   modelArrayListEx.remove(noteList[position])
                   holder.itemView.setBackgroundColor(android.R.color.transparent)*/
                   viewIdList.put(holder.adapterPosition,false)
                   modelArrayListEx.remove(noteList[position])
                   holder.view.noteBackground.setBackgroundResource(R.drawable.note_background)
               }
               else//seçilmemişse
               {
                  /*
                   holder.itemView.setBackgroundColor(R.color.purple_500)
                   modelArrayListEx.add(noteList[position])
                   viewIdList.put(holder.adapterPosition,true)*/

                   holder.view.noteBackground.setBackgroundResource(R.drawable.selected_note_background)
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
        noteList= ArrayList(newNoteList)
        notifyDataSetChanged()
    }



}

