package com.avinash.requestresource;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class register_activity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 0;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private static final String TAG = "EmailPassword";

    private View mContentView;
    private View mControlsView;
    private boolean mVisible;
    private EditText username;
    private EditText password;
    private EditText password1;
    private Button login_button;
    private FirebaseAuth mAuth;
    
    private FirebaseUser user;

    private ProgressDialog nDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_activity);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);
        username = (EditText) findViewById(R.id.register_activity_username);
        password = (EditText) findViewById(R.id.register_activity_password);
        login_button = (Button) findViewById(R.id.register_activity_button);
        password1 = (EditText) findViewById(R.id.register_activity_password2);
        FrameLayout fl= (FrameLayout) findViewById(R.id.register_activity_framelayout);

        nDialog = new ProgressDialog(register_activity.this);
        nDialog.setMessage("Creating user........");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);

        fl.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
//                Toast.makeText(register_activity.this,"this is swipe left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Intent intent= new Intent(getApplicationContext(), login_activity.class);
                startActivity(intent);
                finish();
            }
        });

        username.setHint("Enter username/e-mail");
        password.setHint("Enter password");
        password1.setHint("Confirm Password");
        mAuth = FirebaseAuth.getInstance();

        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                nDialog.show();
                if(validateUserEnteredData(username.getText().toString(), password.getText().toString(), password1.getText().toString()) == true){
                    createUserWithEmail(username.getText().toString(), password.getText().toString());
                }else{
                    nDialog.dismiss();
                    ;
                }
            }
        });
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private void createUserWithEmail(String username, String password){
        OkHttpClient client = new OkHttpClient();
        String url= "https://8resqservices.azurewebsites.net/auth/register";
        String postBody="{\n" +
                "    \"email\": \""+ username +"\",\n" +
                "    \"password\": \""+ password+"\"\n" +
                "}";
        RequestBody body = RequestBody.create(JSON, postBody);
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                updateUI(null, "login");
//                Toast.makeText(getApplicationContext(),"on failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    String userid  = (String) json.getString("userid");
                    String role = (String) json.getString("role");
                    String name = (String) json.getString("username");
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("8ResQ",0);
                    SharedPreferences.Editor editor = pref.edit();

                    editor.putString("role",role);
                    editor.putString("userid",userid);
                    editor.putString("name",name);
                    editor.apply();

                    updateUI(userid, "login");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void updateUI(String user, String contextValue) {
        if (contextValue.equals("login")){
            if(user == null){
                loginFailed();
            }else{
                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        }else if( contextValue.equals("usercreation")){
            if(user == null){
                userCreationFailed();
            }else{
                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        }
    }

    private void userCreationFailed() {
        Toast.makeText(register_activity.this, "User creation failed. Try again.",
                Toast.LENGTH_LONG).show();
    }


    private void loginFailed() {
    }


    public boolean validateUserEnteredData(String username, String password, String confirmPassword){
        if(username.replace(" ","").equals("") || password.replace(" ", "").equals("")){
            Toast.makeText(getApplicationContext(),"Please enter valid details!!", Toast.LENGTH_SHORT).show();
            return false;
        }else if(password.length() < 5){
            Toast.makeText(getApplicationContext(),"Password should be longer than 5 characters.!!", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!password.equals(confirmPassword)){
            Toast.makeText(getApplicationContext(),"Passwords doesn't match!!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EditText username = (EditText) findViewById(R.id.register_activity_username);
        EditText password = (EditText) findViewById(R.id.register_activity_password);
        username.setText("");
        password.setText("");
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}