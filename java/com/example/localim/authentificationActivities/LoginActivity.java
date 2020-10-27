package com.example.localim.authentificationActivities;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.localim.DisplayListActivity;
import com.example.localim.R;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail,loginPassword;
    private Button loginButton,registerButton,newPassButton;
    private SignInButton signInButton;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = (EditText) findViewById(R.id.champsEmail);
        loginPassword = (EditText) findViewById(R.id.champsPassword);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        newPassButton = (Button) findViewById(R.id.newPasswordButton);
        mAuth = FirebaseAuth.getInstance();

        //Gestion de la connexion avec google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                //Contient l'ID client
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        //On accede à l'ecran de recuperation de mot de passe
        newPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
            }
        });

        //Gestion de la conexion par email et mot de passe
        loginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //On recupere l'email et le mdp
                String email = loginEmail.getText().toString() ;
                String password = loginPassword.getText().toString() ;
                if(email.isEmpty() || password.isEmpty())
                {
                    loginEmail.setError("Ces champs ne doivent pas être vide");
                    loginPassword.setError("Ces champs ne doivent pas être vide");
                }
                else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Authentication succeed.",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), DisplayListActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid password or e-mail !",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        //On accede à l'ecran de creation de compte
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish() ;
            }
        });
    }
    @Override
    public void onBackPressed() {
        //On revient au menu pour s'enregistrer
        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
    }

    //Methode permettant de s'enregistrer avec google
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Resultat retourné par l'intent de Google SignIn client
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // On a reussi à recuperer le compte google, on s'identifie donc
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
           //Erreur
            Log.w("Tag", "Erreur code=" + e.getStatusCode());
        }
    }

    //Methode pour s'authentifier avec google
    private void firebaseAuthWithGoogle(String idToken)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Si on a reussi à s'authentifier avec google, alors on passe a l'ctivite permettant d'afficher la liste des offres
                if(task.isSuccessful())
                {
                    Log.w("Connexion : "," Google-Ok");
                    Toast.makeText(getApplicationContext(),"You signed in through google", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),DisplayListActivity.class));
                    finish();
                }
                else
                {
                    Log.w("Connexion : "," Google-ERREUR");
                    Toast.makeText(getApplicationContext(),"Something went wrong !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}