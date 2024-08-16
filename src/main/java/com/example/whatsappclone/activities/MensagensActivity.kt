package com.example.whatsappclone.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.adapters.MensagensAdapter
import com.example.whatsappclone.databinding.ActivityMensagensBinding
import com.example.whatsappclone.model.Conversa
import com.example.whatsappclone.model.Message
import com.example.whatsappclone.model.User
import com.example.whatsappclone.utils.Constantes
import com.example.whatsappclone.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class MensagensActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMensagensBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var conversasAdapter: MensagensAdapter

    private var dadosDest: User? = null
    private var dadosRem: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        recuperarDadosUsers()
        inicializarToolBar()
        inicializarEvetosClick()
        inicializarRecyclerView()
        inicializarListeners()
    }

    private fun inicializarRecyclerView() {

        with(binding){
            conversasAdapter = MensagensAdapter()
            rvMensagens.adapter = conversasAdapter
            rvMensagens.layoutManager = LinearLayoutManager(applicationContext)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    private fun inicializarListeners() {
        val idUserRem = firebaseAuth.currentUser?.uid
        val idUserDest = dadosDest?.id

        if(idUserRem != null && idUserDest != null){

            listenerRegistration = firestore
                .collection(Constantes.MENSAGENS)
                .document(idUserRem)
                .collection(idUserDest)
                .orderBy("data", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, erro ->

                    if(erro != null){
                        exibirMensagem("Erro ao recuperar mensagens")
                    }
                    val listMessage = mutableListOf<Message>()
                    val documents = querySnapshot?.documents
                    documents?.forEach { documentSnapshot ->
                        val message = documentSnapshot.toObject(Message::class.java)
                        if(message != null){
                            listMessage.add(message)
                        }
                    }

                    if (listMessage.isNotEmpty()){
                        conversasAdapter.addList(listMessage)
                    }

                }

        }
    }

    private fun inicializarEvetosClick() {
        binding.fabEnviar.setOnClickListener {
            val mensagem = binding.editMensagem.text.toString()
            salvarMensagem(mensagem)
        }
    }

    private fun salvarMensagem(textMessage: String) {

        if(textMessage.isNotEmpty()){
            val idUserRem = firebaseAuth.currentUser?.uid
            val idUserDest = dadosDest?.id

            if(idUserRem != null && idUserDest != null){
                val message = Message(
                    idUserRem, textMessage
                )

                //Salvar para o Remetente
                salvarMensagemFirestore(idUserRem, idUserDest, message)
                //save photo e name Dest
                var conversaRem = Conversa(
                    idUserRem, idUserDest,
                    dadosDest!!.photo, dadosDest!!.nome,
                    textMessage
                )
                salvarConversaFirestore(conversaRem)

                //Salvar para o Destinatario
                salvarMensagemFirestore(idUserDest, idUserRem, message)
                //save photo e name Rem
                var conversaDest = Conversa(
                    idUserDest, idUserRem,
                    dadosRem!!.photo, dadosRem!!.nome,
                    textMessage
                )
                salvarConversaFirestore(conversaDest)

                binding.editMensagem.setText("")

            }
        }

    }

    private fun salvarConversaFirestore(conversa: Conversa) {
        firestore
            .collection(Constantes.CONVERSAS)
            .document(conversa.idUserRem)
            .collection(Constantes.LATEST_CONVERSAS)
            .document(conversa.idUserDest)
            .set(conversa)
            .addOnFailureListener {
                exibirMensagem("Erro ao salvar conversa")
            }
    }

    private fun salvarMensagemFirestore(
      idUserRem: String, idUserDest: String, message: Message
    ) {
        firestore.collection(Constantes.MENSAGENS)
            .document(idUserRem)
            .collection(idUserDest)
            .add( message )
            .addOnFailureListener {
                exibirMensagem("Erro ao enviar mensagem")
            }
    }


    private fun recuperarDadosUsers() {
        val idUserRem = firebaseAuth.currentUser?.uid
        //Recuoerando dados current user
        if(idUserRem!=null){
            firestore
                .collection(Constantes.USERS)
                .document(idUserRem)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    if(user!=null){
                        dadosRem = user
                    }
                }
        }

        //Recuperando dados Dest
        val extras = intent.extras
        if(extras != null){
            dadosDest = extras.getParcelable("dadosDest", User::class.java)

        }
    }
    private fun inicializarToolBar() {
        val toolbar = binding.tbMensagens
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = ""
            if(dadosDest != null){
                binding.textNomeConversa.text = dadosDest!!.nome
                Picasso.get()
                    .load(dadosDest!!.photo)
                    .into(binding.imagePhotoConversa)
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }
}