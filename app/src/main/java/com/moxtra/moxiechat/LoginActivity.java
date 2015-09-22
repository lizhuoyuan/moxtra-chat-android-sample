package com.moxtra.moxiechat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.moxtra.moxiechat.common.PreferenceUtil;
import com.moxtra.moxiechat.model.DummyData;
import com.moxtra.moxiechat.model.User;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.moxtra.sdk.MXAccountManager;
import com.moxtra.sdk.MXSDKConfig;
import com.moxtra.sdk.MXSDKException;

import java.io.IOException;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements MXAccountManager.MXAccountLinkListener {

    private static final String TAG = "LoginActivity";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private String SENDER_ID = "3367684136";
    private GoogleCloudMessaging gcm;
    private String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PreferenceUtil.getUser(this) != null) {
            setupMoxtraUser();
            return;
        }

        setContentView(R.layout.activity_login);
        mProgressView = findViewById(R.id.login_progress);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mEmailView.setText("amy@example.com");
        addEmailsToAutoComplete(DummyData.EMAILS);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setText("password");
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);

        showSelectionDialog();
    }

    private void showSelectionDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.selectUserTitle)
                .items(DummyData.EMAILS.toArray(new String[DummyData.EMAILS.size()]))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        User user = DummyData.USERS.get(i);
                        mEmailView.setText(user.email);
                        mPasswordView.setText(user.password);
                    }
                })
                .show();
    }

    private void startChatListActivity() {
        Log.i(TAG, "startChatListActivity");
        startActivity(new Intent(this, ChatListActivity.class));
        finish();
    }

    private void setupMoxtraUser() {
        if (MXAccountManager.getInstance().isLinked()) {
            Log.i(TAG, "Moxtra user is already linked.");
            startChatListActivity();
        } else {
            Log.i(TAG, "Moxtra user is not linked yet.");
            regid = getRegistrationId(this);
            if (TextUtils.isEmpty(regid)) {
                Log.i(TAG, "Register in background.");
                registerInBackground();
            } else {
                try {
                    User user = PreferenceUtil.getUser(this);
                    Bitmap avatar = BitmapFactory.decodeStream(this.getAssets().open(user.avatarPath));
                    final MXSDKConfig.MXUserInfo mxUserInfo = new MXSDKConfig.MXUserInfo(user.email, MXSDKConfig.MXUserIdentityType.IdentityUniqueId);
                    final MXSDKConfig.MXProfileInfo mxProfileInfo = new MXSDKConfig.MXProfileInfo(user.firstName, user.lastName, avatar);
                    MXAccountManager.getInstance().setupUser(mxUserInfo, mxProfileInfo, null, regid, this);
                } catch (IOException e) {
                    Log.e(TAG, "Can't decode avatar.", e);
                }
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //Demo only: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //Demo only: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onLinkAccountDone(boolean success) {
        if (success) {
            Log.i(TAG, "Linked to moxtra account successfully.");
            startChatListActivity();
        } else {
            Toast.makeText(this, "Failed to setup moxtra user.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed to setup moxtra user.");
            showProgress(false);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return false;
            }

            for (User user : DummyData.USERS) {
                if (user.email.equals(mEmail)) {
                    return user.password.equals(mPassword);
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                PreferenceUtil.saveUser(LoginActivity.this, mEmail);
                setupMoxtraUser();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
                showProgress(false);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private String getRegistrationId(Context context) {
        String registrationId = PreferenceUtil.getGcmRegId(context);
        if (TextUtils.isEmpty(registrationId)) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = PreferenceUtil.getAppVersion(this);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mProgressView != null) {
                    mProgressView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(LoginActivity.this);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // Save the regid in Moxtra so we can get the GCM notification.
                    try {
                        User user = PreferenceUtil.getUser(LoginActivity.this);
                        Bitmap avatar = BitmapFactory.decodeStream(LoginActivity.this.getAssets().open(user.avatarPath));
                        final MXSDKConfig.MXUserInfo mxUserInfo = new MXSDKConfig.MXUserInfo(user.email, MXSDKConfig.MXUserIdentityType.IdentityUniqueId);
                        final MXSDKConfig.MXProfileInfo mxProfileInfo = new MXSDKConfig.MXProfileInfo(user.firstName, user.lastName, avatar);
                        MXAccountManager.getInstance().setupUser(mxUserInfo, mxProfileInfo, null, regid, LoginActivity.this);
                    } catch (IOException e) {
                        Log.e(TAG, "Can't decode avatar.", e);
                    }

                    // Persist the regID - no need to register again.
                    PreferenceUtil.setGcmRegId(LoginActivity.this, regid);
                    PreferenceUtil.setAppVersion(LoginActivity.this, getAppVersion(LoginActivity.this));
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.e(TAG, "Error when register on GCM.", ex);
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (mProgressView != null) {
                    mProgressView.setVisibility(View.GONE);
                }
                Log.d(TAG, "Reg done: " + msg);
            }
        }.execute(null, null, null);
    }
}

