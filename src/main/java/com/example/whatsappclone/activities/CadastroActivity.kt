package com.example.whatsappclone.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappclone.databinding.ActivityCadastroBinding
import com.example.whatsappclone.model.User
import com.example.whatsappclone.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var senha: String
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        inicializarToolBar()
        inicializarEventosClique()


    }

    private fun inicializarEventosClique() {

        binding.btnCadastrar.setOnClickListener {
            if (validarCampos()) {
                cadastrarUsuario(name, email, senha)
            }
        }
    }

    private fun cadastrarUsuario(name: String, email: String, senha: String) {

        firebaseAuth.createUserWithEmailAndPassword(email,senha).addOnCompleteListener{resultado ->
            if(resultado.isSuccessful){

                //Salvar dados no Firestore
                val idUser = resultado.result.user?.uid
                if(idUser != null){
                    val user = User(idUser, name, email)
                    salvarUserFirestore(user)
                }


            }
        }.addOnFailureListener { erro ->
            try {
                throw erro
            }catch ( erroSenhaFraca: FirebaseAuthWeakPasswordException){
                erroSenhaFraca.printStackTrace()
                exibirMensagem("Senha fraca, digite outra como letras, números e caracteres especiais!")
            }catch ( erroUsuarioExistente: FirebaseAuthUserCollisionException){
                erroUsuarioExistente.printStackTrace()
                exibirMensagem("E-mail já pertence a outro usuário!")
            }catch ( erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException){
                erroCredenciaisInvalidas.printStackTrace()
                exibirMensagem("E-mail inválido, digite um outro e-mail!")
            }
        }


    }

    private fun salvarUserFirestore(user: User) {
        firestore.collection("users")
            .document(user.id).set(user)
            .addOnSuccessListener {
                exibirMensagem("Sucesso ao fazer seu cadastro!!")
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }.addOnFailureListener {
                exibirMensagem("Erro ao fazer seu cadastro!!")
            }
    }

    private fun validarCampos(): Boolean {
        name = binding.editName.text.toString()
        email = binding.editEmail.text.toString()
        senha = binding.editSenha.text.toString()
        if(name.isNotEmpty()){
            binding.textInputName.error = null
            if(email.isNotEmpty()){
                binding.textInputEmail.error = null
                if(senha.isNotEmpty()){
                    binding.textInputSenha.error = null
                    return true
                }else{
                    binding.textInputSenha.error = "Preencha a sua senha!"
                    return false
                }
            }else{
                binding.textInputEmail.error = "Preencha o seu e-mail!"
                return false
            }
        }else{
            binding.textInputName.error = "Preencha o seu nome!"
            return false
        }
    }

    private fun inicializarToolBar() {
        val toolbar = binding.includeToolbar.tbMain
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}


