package com.example.spho;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button login;
    Button signup;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.Password);
        login = findViewById(R.id.login_btn);
        signup = findViewById(R.id.signup_btn);

        firebaseAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });

    }

    private void login (){
        String userEmail = email.getText().toString();
        String userpassword = password.getText().toString();

        if(TextUtils.isEmpty(userEmail)){
            Toast.makeText(this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userpassword)){
            Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(userEmail,userpassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else{
                            Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });

    }


}