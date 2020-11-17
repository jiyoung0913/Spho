package com.example.spho;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WriteActivity extends AppCompatActivity {

    TextView upload_text;
    EditText get_tilte;
    ImageView get_photo;
    TextView want_title;
    EditText want_text;
    boolean permissionCheck = false;
    private static final int REQUEST_CODE = 0;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private StorageReference mStorageRef;

    private Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        upload_text = findViewById(R.id.upload_text);
        get_tilte = findViewById(R.id.get_title);
        get_photo = findViewById(R.id.get_photo);
        want_title = findViewById(R.id.want_title);
        want_text = findViewById(R.id.want_text);

        permissionCheck = checkSelfPermission();
        if (!permissionCheck) {
            Intent intent = new Intent(); //기기 기본 갤러리 접근
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent,101);
        }

        upload_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vailDateCheck();
                save();

            }
        });

        get_photo = findViewById(R.id.get_photo);
        get_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });


    }

    private void save(){
        String title = get_tilte.getText().toString();
        String want = want_text.getText().toString();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        FirebaseStorage firebasestorage = FirebaseStorage.getInstance();

        final String cu = firebaseAuth.getUid();
        String filename = cu + "_" + System.currentTimeMillis();
        mStorageRef = firebasestorage.getReferenceFromUrl("본인의 firebase 저장소 ").child("WhriteClassImage/* "+ filename);
        final UploadTask uploadTask = mStorageRef.putFile(photoURI);

        long now = System.currentTimeMillis();
        Date date =new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/mm/dd");
        String time = mFormat.format(date);
        Map<String,String> map = new HashMap<>();
        map.put("title",title);
        map.put("want",want);
        map.put("time",time);
        map.put("user",firebaseUser.getEmail());

        firebaseFirestore.collection("Post").add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(),"완료",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        Log.d("ADDDiaryActivity","ERROR:"+ error);
                        Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();

                    }
                });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode ==1 ){
            int length = permissions.length;
            for(int i = 0; i < length; i++){
                if(grantResults[i]== PackageManager.PERMISSION_GRANTED){
                    Log.d("MainActivity","권한허용 : "+permissions[i]);
                }
            }
        }
    }


    public boolean checkSelfPermission(){
        boolean check = false;

        String temp = "";
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            temp += Manifest.permission.READ_EXTERNAL_STORAGE +" ";
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE +" ";
        }

        if(TextUtils.isEmpty(temp)== false){
            ActivityCompat.requestPermissions(this,temp.trim().split(" "),1);

        }else {
            Toast.makeText(this,"권한을 모두 허용",Toast.LENGTH_SHORT).show();
            check = true;
        }

        return check;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                try {
                    photoURI =data.getData();
                    InputStream in = getContentResolver().openInputStream(photoURI);

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    get_photo.setImageBitmap(img);
                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }


    }

    private void vailDateCheck(){
        String title = get_tilte.getText().toString();
        String want =want_text.getText().toString();
        if(TextUtils.isEmpty(title)){
            Toast.makeText(this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(want)){
            Toast.makeText(this, "원하는것을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
    }


}