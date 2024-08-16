package com.example.whatsappclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.whatsappclone.databinding.ItemMensagensDestBinding
import com.example.whatsappclone.databinding.ItemMensagensRemBinding
import com.example.whatsappclone.model.Message
import com.example.whatsappclone.utils.Constantes
import com.google.firebase.auth.FirebaseAuth

class MensagensAdapter : Adapter<ViewHolder>() {

    private var listMensagens = emptyList<Message>()
    fun addList(list: List<Message>){
        listMensagens = list
        notifyDataSetChanged()
    }

    class MensagensRemViewHolder(
        private val biding: ItemMensagensRemBinding
    ) : ViewHolder(biding.root){

        fun bind( message: Message){
            biding.textMensagemRem.text = message.message
        }

        companion object {
            fun inflarLayout(parent: ViewGroup) : MensagensRemViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMensagensRemBinding.inflate(
                    inflater, parent, false
                )
                return MensagensRemViewHolder(itemView)
            }
        }
    }

    class MensagensDestViewHolder(
        private val biding: ItemMensagensDestBinding
    ) : ViewHolder(biding.root){

        fun bind( message: Message){
            biding.textMensagemDest.text = message.message
        }

        companion object {
            fun inflarLayout(parent: ViewGroup) : MensagensDestViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMensagensDestBinding.inflate(
                    inflater, parent, false
                )
                return MensagensDestViewHolder(itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = listMensagens[position]
        val idCurrentUser = FirebaseAuth.getInstance().currentUser?.uid.toString()

        return if(idCurrentUser == message.idUser){
            Constantes.TIPO_REM
        }else{
            Constantes.TIPO_DEST
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if(viewType == Constantes.TIPO_REM)
           return MensagensRemViewHolder.inflarLayout(parent)

        return MensagensDestViewHolder.inflarLayout(parent)

    }




    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = listMensagens[position]

        when(holder){
            is MensagensRemViewHolder -> holder.bind(message)
            is MensagensDestViewHolder -> holder.bind(message)
        }
        /*val messageRemViewHolder = holder as MensagensRemViewHolder
        messageRemViewHolder.bind(message)*/
    }

    override fun getItemCount(): Int {
        return listMensagens.size
    }

}