package com.example.android.sairamedicalstore.ui.login;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.DefaultKeyValuePair;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.operations.MedicineOperations;
import com.example.android.sairamedicalstore.ui.MainActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mAuthProgressDialog;

    GoogleSignInAccount mGoogleAccount;
    protected GoogleApiClient mGoogleApiClient;

    public static final int RC_GOOGLE_LOGIN = 1;
    public Boolean alreadyAuthenticated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Firebase.setAndroidContext(this);

        /*if(!Utils.isInternetAvailable())
        {
            Toast.makeText(this,"Check internet connection",Toast.LENGTH_SHORT).show();
            SignInButton signInButton = (SignInButton)findViewById(R.id.login_with_google);
            signInButton.setVisibility(View.GONE);
            return;
        }*/

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        initializeScreen();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String encodedEmail = Utils.encodeEmail(user.getEmail().toUpperCase());
                    gotoMainActivity(encodedEmail);

                    alreadyAuthenticated = true;
                    SignInButton signInButton = (SignInButton)findViewById(R.id.login_with_google);
                    signInButton.setVisibility(View.GONE);

                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    public void initializeScreen() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_authenticating_with_firebase));
        mAuthProgressDialog.setCancelable(false);
        /* Setup Google Sign In */
        setupGoogleSignIn();

        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    private void setupGoogleSignIn() {
        SignInButton signInButton = (SignInButton)findViewById(R.id.login_with_google);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInGooglePressed(v);
            }
        });
    }

    public void onSignInGooglePressed(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
        mAuthProgressDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...); */
        if (requestCode == RC_GOOGLE_LOGIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess())
            {
                mGoogleAccount = result.getSignInAccount();
                getGoogleOAuthTokenAndLogin();
            }
            else
            {
                if (result.getStatus().getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    Toast.makeText(this,"The sign in was cancelled. Make sure you're connected to the internet and try again.",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,"Error handling the sign in: " + result.getStatus().getStatusMessage(),Toast.LENGTH_SHORT).show();
                }
                mAuthProgressDialog.dismiss();
            }

        }

    }

    private void getGoogleOAuthTokenAndLogin() {
        AuthCredential credential = GoogleAuthProvider.getCredential(mGoogleAccount.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String userName = task.getResult().getUser().getDisplayName();
                            String userEmail = task.getResult().getUser().getEmail();
                            String userPhoneNo = task.getResult().getUser().getPhoneNumber();
                            String userPhotoUrl = task.getResult().getUser().getPhotoUrl().toString();

                            addNewUser(userName, userEmail,userPhoneNo, userPhotoUrl);

                            String encodedEmail = Utils.encodeEmail(userEmail);


                        }
                    }
                });
    }

    public void addNewUser(String userName,String userEmail,String phoneNo, String userPhotoUrl) {
        final  String encodedEmail = Utils.encodeEmail(userEmail.toUpperCase());

        final HashMap<String, Object> timestampJoined = new HashMap<String, Object>();
        timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        final User newUser = new User(userName.toUpperCase(), userEmail.toUpperCase(),phoneNo, userPhotoUrl, timestampJoined);

        final Firebase currentUserReference = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_USERS).child(encodedEmail);

        currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                            /* If nothing is there ...*/
                if (dataSnapshot.getValue() == null) {
                    currentUserReference.setValue(newUser);
                }
                gotoMainActivity(encodedEmail);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }


    private void gotoMainActivity(final String encodedEmail)
    {
        Firebase mCurrentUserRef = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_USERS).child(encodedEmail);

        final Application sairaMedicalStoreApplication = this.getApplication();

        mCurrentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User loggedinUser = dataSnapshot.getValue(User.class);

                if (loggedinUser != null) {

                    ((SairaMedicalStoreApplication) sairaMedicalStoreApplication).setCurrentUser(loggedinUser);
                    updateUserType(encodedEmail);
                    getDefaultValues(loggedinUser);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void updateUserType(String encodedEmail)
    {
        final Application sairaMedicalStoreApplication = this.getApplication();
        ((SairaMedicalStoreApplication) sairaMedicalStoreApplication).setUserType(Constants.USER_TYPE_END_USER);

    }

    private void getDefaultValues(User loggedinUser)
    {
        MedicineOperations obj = new MedicineOperations(loggedinUser,this);
        obj.getAllDefaultValues();

        final Application sairaMedicalStoreApplication = this.getApplication();
        final ArrayList<DefaultKeyValuePair> mArrayListCommonDefaultValues = new ArrayList<>();
        Firebase allDefaultValuesRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DEFAULT_VALUES);

        Firebase commonDefaultValues = allDefaultValuesRef.child(Constants.FIREBASE_PROPERTY_COMMON_DEFAULT_VALUES);
        commonDefaultValues.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DefaultKeyValuePair defaultKeyValuePair = snapshot.getValue(DefaultKeyValuePair.class);
                        if(defaultKeyValuePair != null)
                        {
                            mArrayListCommonDefaultValues.add(defaultKeyValuePair);
                        }
                    }

                    ((SairaMedicalStoreApplication) sairaMedicalStoreApplication).
                            setArrayListCommonDefaultValues(mArrayListCommonDefaultValues);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}

