package com.example.spho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    EditText newemail;
    EditText newpassword;
    EditText passwordcheck;
    Button signup_btn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        newemail = findViewById(R.id.newemail);
        newpassword = findViewById(R.id.newpassword);
        passwordcheck = findViewById(R.id.passwordcheck);
        signup_btn = findViewById(R.id.signup_btn);

        firebaseAuth = FirebaseAuth.getInstance();

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });

    }

    private void signup(){
        String userEmail = newemail.getText().toString();
        String userpassword = newpassword.getText().toString();
        String check = passwordcheck.getText().toString();

        if(TextUtils.isEmpty(userEmail)){
            Toast.makeText(this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userpassword)){
            Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(check)){
            Toast.makeText(this, "비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!userpassword.equals(check) ){
            Toast.makeText(this, "비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(userEmail,userpassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        }else{
                            Toast.makeText(getApplicationContext(),"회원가입 실패",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });

    }
}