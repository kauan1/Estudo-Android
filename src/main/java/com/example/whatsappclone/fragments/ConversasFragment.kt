package com.example.whatsappclone.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.activities.MensagensActivity
import com.example.whatsappclone.adapters.ConversasAdapter
import com.example.whatsappclone.databinding.FragmentConversasBinding
import com.example.whatsappclone.model.Conversa
import com.example.whatsappclone.model.User
import com.example.whatsappclone.utils.Constantes
import com.example.whatsappclone.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ConversasFragment : Fragment() {

    private lateinit var binding: FragmentConversasBinding
    private lateinit var eventSnapshot: ListenerRegistration
    private lateinit var conversasAdapter: ConversasAdapter

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConversasBinding.inflate(
            inflater, container, false
        )

        conversasAdapter = ConversasAdapter{conversa ->
            val intent = Intent(context, MensagensActivity::class.java)
            val user = User(
                id = conversa.idUserDest,
                nome = conversa.name,
                photo = conversa.photo
            )
            intent.putExtra("dadosDest", user)
            startActivity(intent)
        }
        binding.rcConversas.adapter = conversasAdapter
        binding.rcConversas.layoutManager = LinearLayoutManager(context)
        binding.rcConversas.addItemDecoration(
            DividerItemDecoration(
                context, LinearLayoutManager.VERTICAL
            )
        )

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        addListenerConversas()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventSnapshot.remove()
    }

    private fun addListenerConversas() {
        val idUserRem = firebaseAuth.currentUser?.uid
        if(idUserRem != null){
            eventSnapshot = firestore
                .collection(Constantes.CONVERSAS)
                .document(idUserRem)
                .collection(Constantes.LATEST_CONVERSAS)
                .orderBy("data", Query.Direction.DESCENDING)
                .addSnapshotListener{ querySnapshot, error ->

                    if(error != null){
                        activity?.exibirMensagem("Erro ao recuperar conversas")
                    }

                    val listConversas = mutableListOf<Conversa>()
                    val documents = querySnapshot?.documents

                    documents?.forEach { documentSnapshot ->
                        val conversa = documentSnapshot.toObject(Conversa::class.java)
                        if(conversa!=null){
                            listConversas.add(conversa)
                        }
                    }

                    //atualizar adapter
                    if(listConversas.isNotEmpty()){
                        conversasAdapter.addList(listConversas)
                    }

                }
        }
    }

}