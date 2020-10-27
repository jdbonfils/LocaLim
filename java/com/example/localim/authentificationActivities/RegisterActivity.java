package com.example.localim.authentificationActivities;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.localim.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText email,password;
    private Button registerButton,loginButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = (EditText) findViewById(R.id.mail);
        password = (EditText) findViewById(R.id.password);
        registerButton = (Button) findViewById(R.id.register);
        loginButton = (Button) findViewById(R.id.login);
        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString();
                String pswrd = password.getText().toString();
                //Les deux champs doivent être renseigné
                //Le mail indiqué ne doit pas etre nul
                if (TextUtils.isEmpty(mail)) {
                    Toast.makeText(getApplicationContext(), "Please fill in the required fields", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(pswrd)) {
                    Toast.makeText(getApplicationContext(), "Please fill in the required fields", Toast.LENGTH_SHORT).show();
                }
                //Le mot de passe doit être d'au moins 8 charactere
                else if (pswrd.length() < 8) {
                    Toast.makeText(getApplicationContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                } else {
                    //Gestion de la creation d'un compte avec email et mot de passe
                    firebaseAuth.createUserWithEmailAndPassword(mail, pswrd)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //Si la creation de compte a reussi
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Account created ! Please login.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Un compte est déjà lié à cet email", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        //Permet d'acceder à l'ecran de login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        //On quitte l'application qu'on on clique sur le bouton back
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }
}