package com.example.localim.authentificationActivities;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.localim.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button resetButton ;
    private EditText emailToReset ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Lie des variables avec des element du xml
        resetButton = findViewById(R.id.resetPassword);
        emailToReset = findViewById(R.id.textEmailAddress);

        //Si l'utilisateur clic sur le bouton
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //On recupere l e mail saisi par l'utilisateur
                String email = emailToReset.getText().toString();
                //Le champs ne doit pas etre vide
                if(email == "")
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter an email first !", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //Si l'email de recuperation a ete envoyé
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPasswordActivity.this, "Email sent, check your e-mail box",
                                                Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(ForgotPasswordActivity.this, "Something went wrong ! ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}

