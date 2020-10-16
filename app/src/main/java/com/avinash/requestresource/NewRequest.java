package com.avinash.requestresource;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewRequest extends AppCompatActivity {

    private  String TAG ="in new request: ";

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    EditText requestTitle;
    EditText requestDescription;
    EditText requestComments;
    EditText quantity;
    Spinner priority;
    Button requestButton;
    RadioButton radio_food, radio_medication, radio_ppe;
    Button completeRequestButton;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private void insertOneRequest() {
        String type = "";
        if(radio_food.isChecked())
            type = "radio_food";
        else if(radio_medication.isChecked())
            type = "radio_medication";
        else
            type = "radio_ppe";

        String userId = getSharedPreferences("8ResQ",0).getString("userid", "");
        OkHttpClient client = new OkHttpClient();
        String url= "https://8resqservices.azurewebsites.net/userRequest/raise";
        String postBody="{\"addressedBy\":\"\" ,\"comments\": \""+requestComments.getText()+"\",\"completed\": \"false\",\"description\": \""+requestDescription.getText()+"\",\"priority\": \""+ priority.getSelectedItemId() +"\",\"quantity\": \"" + quantity.getText() + "\", \"requestedOn\": \"\",\"title\": \""+ requestTitle.getText()+ " \",\"type\": \""+type+"\",\"userId\": \""+userId+"\" }";
        RequestBody body = RequestBody.create(JSON, postBody);
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.w(TAG, "Error adding document", e);
                Toast.makeText(getApplicationContext(),"on failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_request);
        Spinner spinner = findViewById(R.id.priority_spinner);
        requestTitle = (EditText) findViewById(R.id.requestTitle);
        requestDescription = (EditText) findViewById(R.id.requestDescription);
        quantity = (EditText) findViewById(R.id.requestQuantity);
        priority = (Spinner) findViewById(R.id.priority_spinner);
        radio_food = (RadioButton) findViewById(R.id.radio_food);
        radio_medication = (RadioButton) findViewById(R.id.radio_medication);
        radio_ppe  = (RadioButton) findViewById(R.id.radio_ppe);
        requestComments = (EditText) findViewById(R.id.requestComments);
        completeRequestButton = (Button) findViewById(R.id.closeRequestButton);

        MainActivity mainActivity = new MainActivity();
        String role = mainActivity.getUserRole();
        if(role.equals("staff")){
            completeRequestButton.setVisibility(View.GONE);
        }else{
            Intent intent = getIntent();
            String action = intent.getStringExtra("action");
            if(action == null || action.equals("add"))
                completeRequestButton.setVisibility(View.GONE);
            else
                completeRequestButton.setVisibility(View.VISIBLE);
        }
        completeRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                boolean completed = getIntent().getBooleanExtra("completed", false);
                if(completed == true){
                    Snackbar.make(v, "Request is already Closed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    closeOneRequest();
                }
            }
        });
        requestButton = (Button)findViewById(R.id.requestButton);
        requestButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String tag = requestButton.getText().toString();
                if(tag.equals("Place your Request"))
                    insertOneRequest();
                else if(tag.equals("Edit Request")){
                    Intent intent = getIntent();
                    boolean completed = getIntent().getBooleanExtra("completed", false);
                    if(completed == false)
                        setViewToUpdate();
                    else{
                        Snackbar.make(v, "Request is already Closed", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
                else if(tag.equals("Update Request"))
                    updateOneRequest();
                    
            }
        });
        ArrayList<String> arrayList = new ArrayList<>();
        //replace it with the list of priorities from the website.
        arrayList.add("Normal");
        arrayList.add("Critical");
        arrayList.add("Food time(Lunch/Dinner)");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tutorialsName = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void closeOneRequest() {
        String url = "https://8resqservices.azurewebsites.net/userRequest/closeRequest";

        OkHttpClient client = new OkHttpClient();
        String postBody="{\n" + "\"id\": \""+getIntent().getExtras().get("requestID") +"\"\n}";
        RequestBody body = RequestBody.create(JSON, postBody);
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(getApplicationContext(),"on failure", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onResponse(Response response) throws IOException {
                    Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(mainActivity);
            }
        });

//        mAuth = FirebaseAuth.getInstance();
//        user = mAuth.getCurrentUser();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        Map< String, Object > request = new HashMap< >();
//        request.put("quantity", Integer.parseInt(quantity.getText().toString()));
//        request.put("requestedOn", "");
//        request.put("completed", "true");
//        request.put("userID",getIntent().getStringExtra("userID"));
//        request.put("title", requestTitle.getText().toString());
//        request.put("description", requestDescription.getText().toString());
//        request.put("priority", priority.getSelectedItemId());
//        request.put("addressedBy", FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
//        request.put("comments",requestComments.getText().toString());
//        if(radio_food.isChecked())
//            request.put("type", "radio_food");
//        else if(radio_medication.isChecked())
//            request.put("type","radio_medication");
//        else
//            request.put("type","radio_ppe");
//
//        db.collection("requests")
//                .document(getIntent().getStringExtra("requestID"))
//                .update(request).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(mainActivity);
//                finish();
//                Log.d(TAG, "DocumentSnapshot updated with ID: " + getIntent().getStringExtra("requestID"));
//            }
//        })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
    }


    private void updateOneRequest() {

        String type = "";
        if(radio_food.isChecked())
            type = "radio_food";
        else if(radio_medication.isChecked())
            type = "radio_medication";
        else
            type = "radio_ppe";

        String userId = getSharedPreferences("8ResQ",0).getString("userid", "");
        OkHttpClient client = new OkHttpClient();
        String url= "https://8resqservices.azurewebsites.net/userRequest/updateRequest";
        String postBody="{\"id\":\""+getIntent().getStringExtra("requestID")+"\" ,\"addressedBy\":\"\" ,\"comments\": \""+requestComments.getText()+"\",\"completed\": \"false\",\"description\": \""+requestDescription.getText()+"\",\"priority\": \""+ priority.getSelectedItemId() +"\",\"quantity\": \"" + quantity.getText() + "\", \"requestedOn\": \"\",\"title\": \""+ requestTitle.getText()+ " \",\"type\": \""+type+"\",\"userId\": \""+userId+"\" }";
        RequestBody body = RequestBody.create(JSON, postBody);
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.w(TAG, "Error adding document", e);
                Toast.makeText(getApplicationContext(),"on failure", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
            }
        });

//        mAuth = FirebaseAuth.getInstance();
//        user = mAuth.getCurrentUser();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        Map< String, Object > request = new HashMap< >();
//        request.put("quantity", Integer.parseInt(quantity.getText().toString()));
//        request.put("requestedOn", "");
//        request.put("completed", "false");
//        request.put("userID",getIntent().getStringExtra("userID"));
//        request.put("title", requestTitle.getText().toString());
//        request.put("description", requestDescription.getText().toString());
//        request.put("priority", priority.getSelectedItemId());
//        request.put("addressedBy", "");
//        request.put("comments",requestComments.getText().toString());
//        if(radio_food.isChecked())
//            request.put("type", "radio_food");
//        else if(radio_medication.isChecked())
//            request.put("type","radio_medication");
//        else
//            request.put("type","radio_ppe");
//
//        db.collection("requests")
//                .document(getIntent().getStringExtra("requestID"))
//                .update(request).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(mainActivity);
//                Log.d(TAG, "DocumentSnapshot updated with ID: " + getIntent().getStringExtra("requestID"));
//            }
//        })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String action = intent.getStringExtra("action");
        if(action == null || action.equals("add")){
            setViewToAdd();
        }else if(action.equals("view")) {
            setViewToEdit();
        }else if(action.equals("update")){
            setViewToUpdate();
        }
    }

    private void setViewToAdd() {
        requestButton.setText("Place your Request");
        completeRequestButton.setVisibility(View.GONE);
        resetView();
    }

    private void setDisabledView(){
        requestTitle.setEnabled(false);
        requestDescription.setEnabled(false);
        requestComments.setEnabled(false);
        quantity.setEnabled(false);
        priority.setEnabled(false);
        radio_food.setEnabled(false);
        radio_medication.setEnabled(false);
        radio_ppe.setEnabled(false);
    }
    private void resetView(){
        requestTitle.setEnabled(true);
        requestDescription.setEnabled(true);
        requestComments.setEnabled(true);
        quantity.setEnabled(true);
        priority.setEnabled(true);
        radio_food.setEnabled(true);
        radio_medication.setEnabled(true);
        radio_ppe.setEnabled(true);
    }

    private void setViewToUpdate() {
        Intent intent = getIntent();
        resetView();

        requestTitle.setText(intent.getStringExtra("title"));
        requestDescription.setText(intent.getStringExtra("description"));
        requestComments.setText(intent.getStringExtra("comments"));
        quantity.setText(intent.getStringExtra("quantity"));
        priority.setSelection(intent.getIntExtra("priority", 0));
        if(intent.getStringExtra("type").equals("radio_food")){
            radio_food.setChecked(true);
        }else if(intent.getStringExtra("type").equals("radio_medication")){
            radio_medication.setChecked(true);
        }else{
            radio_ppe.setChecked(true);
        }
        requestButton.setText("Update Request");
    }

    private void setViewToEdit() {
        Intent intent = getIntent();
        setDisabledView();
        requestTitle.setText(intent.getStringExtra("title"));
        requestDescription.setText(intent.getStringExtra("description"));
        requestComments.setText(intent.getStringExtra("comments"));
        quantity.setText(intent.getStringExtra("quantity"));
        priority.setSelection(intent.getIntExtra("priority", 0));
        requestButton.setText("Edit Request");
        if(intent.getStringExtra("type").equals("radio_food")){
            radio_food.setChecked(true);
            radio_medication.setChecked(false);
            radio_ppe.setChecked(false);
        }else if(intent.getStringExtra("type").equals("radio_medication")){
            radio_medication.setChecked(true);
            radio_ppe.setChecked(false);
            radio_food.setChecked(false);
        }else{
            radio_medication.setChecked(false);
            radio_food.setChecked(false);
            radio_ppe.setChecked(true);
        }
    }
}