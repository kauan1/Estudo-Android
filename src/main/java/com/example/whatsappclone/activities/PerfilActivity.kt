package com.example.whatsappclone.activities

import android.content.pm.PackageManager
import android.Manifest
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.whatsappclone.databinding.ActivityPerfilBinding
import com.example.whatsappclone.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class PerfilActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
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

    private var temPermissaoCamera = false
    private var temPermissaoGaleria = false
    private val managerGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){uri ->
        if(uri != null){
            binding.imagePerfil.setImageURI(uri)
            uploadImageStorage(uri)
        }else{
            exibirMensagem("Nenhuma imagem selecionada")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        inicializarToolBar()
        solicitarPermissoes()
        inicializarEventosClique()
    }

    override fun onStart() {
        super.onStart()
        recuperarDadosIniciaisUser()
    }

    private fun recuperarDadosIniciaisUser() {
        val idUser = firebaseAuth.currentUser?.uid
        if(idUser != null){
            firestore
                .collection("users")
                .document(idUser)
                .get()
                .addOnSuccessListener { docummentSnapshot ->
                    val dadaUsers = docummentSnapshot.data
                    if(dadaUsers != null){
                        val name = dadaUsers["nome"] as String
                        val photo = dadaUsers["photo"] as String
                        binding.editNomePerfil.setText(name)
                        if(photo.isNotEmpty()){
                            Picasso.get()
                                .load(photo)
                                .into(binding.imagePerfil)
                        }
                    }
                }
        }

    }

    private fun uploadImageStorage(uri: Uri) {
        val idUser = firebaseAuth.currentUser?.uid
        if(idUser!=null){
            storage
                .getReference("photos")
                .child("users")
                .child(idUser)
                .child("perfil.jpg")
                .putFile(uri)
                .addOnSuccessListener { task ->
                    exibirMensagem("Sucesso ao fazer upload da imagem")
                    task.metadata
                        ?.reference
                        ?.downloadUrl
                        ?.addOnSuccessListener { uri ->
                            val dados = mapOf(
                                "photo" to uri.toString()
                            )
                            updateDataPerfil(idUser, dados)
                        }
                }
                .addOnFailureListener {
                    exibirMensagem("Erro ao fazer upload da image")
                }
        }

    }

    private fun updateDataPerfil(idUser: String, dados: Map<String, String>){
        firestore
            .collection("users")
            .document(idUser)
            .update(dados)
            .addOnSuccessListener {
                exibirMensagem("Sucesso ao atualizar perfil")
            }
            .addOnFailureListener {
                exibirMensagem("Erro ao atualizar perfil")
            }
    }

    private fun inicializarEventosClique() {
        binding.fabSelecionar.setOnClickListener{
            if(temPermissaoGaleria){
                managerGaleria.launch("image/*")
            }else{
                exibirMensagem("Não tem permissão para acessar galeria!!")
                solicitarPermissoes()
            }
        }
        binding.btnUpdatePerfil.setOnClickListener {

            val nameUser = binding.editNomePerfil.text.toString()
            if(nameUser.isNotEmpty()){

                val idUser = firebaseAuth.currentUser?.uid

                if(idUser != null){
                    val dados = mapOf(
                        "nome" to nameUser.toString()
                    )
                    updateDataPerfil(idUser, dados)
                }
            }else{
                exibirMensagem("Preencha o nome para atualizar")
            }

        }
    }

    private fun solicitarPermissoes() {

        //verificar permissão
        temPermissaoCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        temPermissaoGaleria = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        val listaPermissoesNegadas = mutableListOf<String>()
        if(!temPermissaoCamera)
            listaPermissoesNegadas.add(Manifest.permission.CAMERA)
        if(!temPermissaoGaleria)
            listaPermissoesNegadas.add(Manifest.permission.READ_MEDIA_IMAGES)

        if(listaPermissoesNegadas.isNotEmpty()){
            //solicitar permissões
            val gerenciarPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ){ permissoes ->

                temPermissaoCamera = permissoes[Manifest.permission.CAMERA] ?: temPermissaoCamera
                temPermissaoGaleria = permissoes[Manifest.permission.READ_MEDIA_IMAGES] ?: temPermissaoGaleria

            }
            gerenciarPermissoes.launch(listaPermissoesNegadas.toTypedArray())
        }
    }

    private fun inicializarToolBar() {
        val toolbar = binding.includeToolbarPerfil.tbMain
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "Editar Perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}