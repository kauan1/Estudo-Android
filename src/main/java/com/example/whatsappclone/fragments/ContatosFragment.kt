package com.example.whatsappclone.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.R
import com.example.whatsappclone.activities.MensagensActivity
import com.example.whatsappclone.adapters.ContatosAdapter
import com.example.whatsappclone.databinding.FragmentContatosBinding
import com.example.whatsappclone.model.User
import com.example.whatsappclone.utils.Constantes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage

class ContatosFragment : Fragment() {

    private lateinit var binding: FragmentContatosBinding
    private lateinit var eventSnapshot: ListenerRegistration
    private lateinit var contatosAdapter: ContatosAdapter

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentContatosBinding.inflate(
            inflater, container, false
        )

        contatosAdapter = ContatosAdapter{ user ->
            val intent = Intent(context, MensagensActivity::class.java)
            intent.putExtra("dadosDest", user)
            intent.putExtra("origem", Constantes.ORIGEM_CONTATO)
            startActivity(intent)
        }
        binding.rvContatos.adapter = contatosAdapter
        binding.rvContatos.layoutManager = LinearLayoutManager(context)
        binding.rvContatos.addItemDecoration(
            DividerItemDecoration(
                context, LinearLayoutManager.VERTICAL
            )
        )

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        addListenerContatos()
    }

    private fun addListenerContatos() {
        eventSnapshot = firestore.collection(Constantes.USERS)
            .addSnapshotListener { querySnapshot, erro ->

                val listContatos = mutableListOf<User>()
                val documents = querySnapshot?.documents
                documents?.forEach { documentSnapshot ->
                    val idCurrentUser = firebaseAuth.currentUser?.uid
                    val user = documentSnapshot.toObject(User::class.java)
                    if(user != null && idCurrentUser != null){
                        if(idCurrentUser != user.id){
                            listContatos.add(user)
                        }

                    }

                }

                if(listContatos.isNotEmpty()){
                    contatosAdapter.addList(listContatos)
                }

            }
    }

    override fun onDestroy() {
        super.onDestroy()
        eventSnapshot.remove()
    }

}