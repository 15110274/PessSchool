package com.example.preschool;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {


    private EditText txtUsername, txtPass;
    private Button btnFogotPass, btnSignIn;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addControlls();

        addEvents();

    }

//    //Check user hiện tại đã đăng nhập app hay chưa
//    @Override
//    protected void onStart()
//    {
//        super.onStart();
//
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//
//        if(currentUser != null) {
//            SendUserToMainActivity();
//        }
//    }


    private void addEvents() {

        // Click LogIn
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllowingUserToLogin();
            }
        });

        // Click FogotPass
        btnFogotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialogFogotPass =new Dialog(LoginActivity.this);
                dialogFogotPass.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogFogotPass.setCancelable(false);
                dialogFogotPass.setContentView(R.layout.forgot_password_dialog);
                dialogFogotPass.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                final EditText txtMailFogotPass =dialogFogotPass.findViewById(R.id.txtMailForgotPass);
                final TextView txtNotAMail=dialogFogotPass.findViewById(R.id.txtNotAMail);
                Button btnSendForgotPass=dialogFogotPass.findViewById(R.id.btnSendFogotPass);
                Button btnBackSendPass=dialogFogotPass.findViewById(R.id.btnBackSendPass);

                btnSendForgotPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txtNotAMail.setText("");
                        loadingBar.setTitle("Loading");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        sendResetPassword();
                    }

                    /**
                     * send mail reset pass to current mail
                     */
                    private void sendResetPassword() {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        String emailAddress =txtMailFogotPass.getText().toString().trim();

                        if(isMail(emailAddress)){
                            auth.sendPasswordResetEmail(emailAddress)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Email sent.");
                                                Toast.makeText(LoginActivity.this,
                                                        getString(R.string.reset_pass_successful),
                                                        Toast.LENGTH_LONG).show();
                                                dialogFogotPass.cancel();
                                                loadingBar.dismiss();
                                            }
                                            else {
                                                Toast.makeText(LoginActivity.this,
                                                        "Fail to reset your password account",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                        else {
                            txtNotAMail.setText(getString(R.string.not_a_mail));
                            loadingBar.dismiss();
                        }
                    }

                    /**
                     * Check is Mail
                     * @param email
                     * @return
                     */
                    private boolean isMail(String email) {
                        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
                        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
                        java.util.regex.Matcher m = p.matcher(email);
                        return m.matches();
                    }
                });

                btnBackSendPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogFogotPass.cancel();
                    }
                });

                dialogFogotPass.show();
            }
        });
    }

    private void AllowingUserToLogin()
    {
        String email = txtUsername.getText().toString();
        String password = txtPass.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,R.string.insert_email, Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, R.string.insert_pass, Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle(R.string.login);
            loadingBar.setMessage(getString(R.string.message_login_successfully));
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            // Sign With Email and Password by Firebase
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                SendUserToMainActivity();

                                Toast.makeText(LoginActivity.this, R.string.you_are_Logged_In_successfully, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, R.string.error_occured + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void addControlls() {
        txtUsername=findViewById(R.id.login_email);
        txtPass=findViewById(R.id.login_password);
        btnFogotPass=findViewById(R.id.fogot_password);
        btnSignIn=findViewById(R.id.login_button);

        mAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);
    }


    // SendUserToRegisterActivity
    public void createNewAcc(View view) {
        Intent intent = new Intent(LoginActivity.this,RegisterDemoActivity.class);
        startActivity(intent);

    }

    // Login Success Send User to MainActivity
    private void SendUserToMainActivity()
    {
        //////////////////////////////////////////////////////
        String currentUserID = mAuth.getCurrentUser().getUid();
        final DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        final DatabaseReference ClassRef=FirebaseDatabase.getInstance().getReference().child("Class");
        UsersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String idClass=dataSnapshot.child("idclass").getValue().toString();
                ClassRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String idTeacher=dataSnapshot.child(idClass).child("teacher").getValue().toString();
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.putExtra("idClass",idClass);
                        mainIntent.putExtra("idTeacher",idTeacher);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }
    // Login Fail Send User back  LoginActivity
    private void SendUserToLoginActivity()
    {
        Intent mainIntent = new Intent(LoginActivity.this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
