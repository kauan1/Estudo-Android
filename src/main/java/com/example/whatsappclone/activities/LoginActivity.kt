package com.example.whatsappclone.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappclone.databinding.ActivityLoginBinding
import com.example.whatsappclone.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private lateinit var email: String
    private lateinit var senha: String
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        inicialziarEventoClick()
        //firebaseAuth.signOut()

    }

    override fun onStart() {
        super.onStart()
        verificarUserLogado()
    }

    private fun verificarUserLogado() {
        val currentUser = firebaseAuth.currentUser
        if(currentUser != null){
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }

    private fun inicialziarEventoClick() {
        binding.textCadastro.setOnClickListener {
            startActivity(
                Intent(this, CadastroActivity::class.java)
            )
        }
        binding.btnLogar.setOnClickListener { 
            if(validarCampos()){
                logarUser()
            }
        }
    }

    private fun logarUser() {
        firebaseAuth.signInWithEmailAndPassword(email,senha)
            .addOnSuccessListener {
                exibirMensagem("Logado com sucesso!")
                startActivity(
                    Intent(this, MainActivity::class.java)
                )
            }.addOnFailureListener { erro ->
                try {
                    throw erro
                }catch ( erroUsuarioInvalido: FirebaseAuthInvalidUserException){
                    erroUsuarioInvalido.printStackTrace()
                    exibirMensagem("E-mail não cadastrado")
                }catch ( erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException){
                    erroCredenciaisInvalidas.printStackTrace()
                    exibirMensagem("E-mail inválido ou senha estão incorretos!")
                }
            }
    }

    private fun validarCampos(): Boolean {
        email = binding.editLoginEmail.text.toString()
        senha = binding.editLoginSenha.text.toString()
        if (email.isNotEmpty()){
            binding.textInputLoginEmail.error = ""
            if (senha.isNotEmpty()){
                binding.textInputLoginSenha.error = ""
                return true
            }else{
                binding.textInputLoginSenha.error = "Preencha a senha"
                return false
            }
        }else{
           binding.textInputLoginEmail.error = "Preencha o e-mail"
            return false
        }
    }
}


