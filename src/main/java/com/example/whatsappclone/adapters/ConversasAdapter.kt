package com.example.whatsappclone.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.whatsappclone.databinding.ItemConversasBinding
import com.example.whatsappclone.model.Conversa
import com.squareup.picasso.Picasso

class ConversasAdapter(
    private val onCLick: (Conversa) -> Unit
) : Adapter<ConversasAdapter.ConversasViewHolder>() {

    private var listConversas = emptyList<Conversa>()

    @SuppressLint("NotifyDataSetChanged")
    fun addList(list:List<Conversa>){
        listConversas = list
        notifyDataSetChanged()
    }

    inner class ConversasViewHolder(
        private val binding: ItemConversasBinding
    ) : ViewHolder(binding.root){
        fun bind(conversa: Conversa){
            binding.textConversaName.text = conversa.name
            Picasso.get()
                .load(conversa.photo)
                .into(binding.imageConversaPhoto)
            binding.textConversaMessage.text = conversa.latestMessage

            binding.clItemCOnversa.setOnClickListener {
                onCLick(conversa)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversasViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemConversasBinding.inflate(
            inflater, parent, false
        )
        return ConversasViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ConversasViewHolder, position: Int) {
        val conversa = listConversas[position]
        holder.bind(conversa)
    }

    override fun getItemCount(): Int {
        return listConversas.size
    }


}