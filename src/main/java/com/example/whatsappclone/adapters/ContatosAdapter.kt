package com.example.whatsappclone.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.whatsappclone.databinding.ItemContatosBinding
import com.example.whatsappclone.model.User
import com.squareup.picasso.Picasso

class ContatosAdapter(
    private val onClick: (User) -> Unit
) : Adapter<ContatosAdapter.ContatosViewHolder>() {

    private  var listContatos = emptyList<User>()

    @SuppressLint("NotifyDataSetChanged")
    fun addList(list:List<User>){
        listContatos = list
        notifyDataSetChanged()
    }

    inner class ContatosViewHolder(
        private val binding: ItemContatosBinding
    ) : ViewHolder(binding.root){
        fun bind(user: User){

            binding.textContatoName.text = user.nome
            Picasso.get()
                .load(user.photo)
                .into(binding.imageContatoPhoto)

            binding.clItemContato.setOnClickListener {
                onClick(user)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatosViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemContatosBinding.inflate(
            inflater, parent, false
        )
        return ContatosViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ContatosViewHolder, position: Int) {
        val user = listContatos[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return listContatos.size
    }

}