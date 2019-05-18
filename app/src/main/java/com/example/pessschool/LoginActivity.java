package com.example.pessschool;

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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {


    private EditText txtUsername, txtPass;
    private Button btnFogotPass, btnSignIn;
//    private TextView txtCreateNewAcc;
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
//        txtCreateNewAcc=findViewById(R.id.register_account_link);
        // Firebase
        mAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);

//        // Configure Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("826478734615-6esbtehvf25gh3pllq4fga9qness6fce.apps.googleusercontent.com")
//                .requestEmail()
//                .build();
//
//        mGoogleSignInClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
//                    @Override
//                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
//                    {
//                        Toast.makeText(LoginActivity.this, "Connection to Google Sign in failed...", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();


    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }







    // SendUserToRegisterActivity
    public void createNewAcc(View view) {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    // Login Success Send User to MainActivity
    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
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
