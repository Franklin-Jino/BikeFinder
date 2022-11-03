package com.example.bikefinderapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikefinderapp.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;
     BikeDatabaseController db;
     RecyclerView recyclerView;
     AppCompatEditText chaiseNoET;

    protected ChaisNoAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        db=new BikeDatabaseController(this);
        binding.getRoot().findViewById(R.id.tagBike).setOnClickListener(this);
        chaiseNoET=binding.getRoot().findViewById(R.id.chaiseNoEditText);
        chaiseNoET.setOnClickListener(this);
        filterChaiseNo();
        binding.getRoot().findViewById(R.id.untagBike).setOnClickListener(this);
        binding.getRoot().findViewById(R.id.tagBikeCloudAnchor).setOnClickListener(this);
        binding.getRoot().findViewById(R.id.untagBikeCloudAnchor).setOnClickListener(this);
        binding.getRoot().findViewById(R.id.searchBike).setOnClickListener(this);
        binding.getRoot().findViewById(R.id.viaMap).setOnClickListener(this);
        binding.getRoot().findViewById(R.id.viaGoogleEarth).setOnClickListener(this);
        binding.getRoot().findViewById(R.id.viaCoreAR).setOnClickListener(this);

        setUpRecyclerView();


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setUpRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  filterChaiseNo();
    }

    private void filterChaiseNo() {

        chaiseNoET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                chaiseNoET.setSelection(chaiseNoET.getText(). length());
//                mAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                chaiseNoET.setSelection(chaiseNoET.getText(). length());
                mAdapter.getFilter().filter(editable.toString());
            }
        });
    }

    private void setUpRecyclerView() {
        recyclerView = binding.getRoot().findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        List<Bike> mDataSet=db.getAllBikes();
        mAdapter=new ChaisNoAdapter(mDataSet,this);
        recyclerView.setLayoutManager(mLayoutManager);
        chaiseNoET.setSelection(chaiseNoET.getText(). length());
        mAdapter.getFilter().filter(chaiseNoET.getText().toString().trim());
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tagBike:
               Intent tagIntent= new Intent(MainActivity.this, REQRActivity.class);
                Bundle tagbundle=new Bundle();
                tagbundle.putString("operation",Constants.TAG);
                tagIntent.putExtras(tagbundle);
                startActivity(tagIntent);

                break;
            case R.id.untagBike:
                Intent unTagIntent= new Intent(MainActivity.this, REQRActivity.class);
                Bundle untagbundle=new Bundle();
                untagbundle.putString("operation",Constants.UNTAG);
                unTagIntent.putExtras(untagbundle);
                startActivity(unTagIntent);
                break;
            case R.id.searchBike:
                Toast.makeText(getApplicationContext(), new Gson().toJson(db.getAllBikes()), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "test : "+  db.getAllBikes(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.tagBikeCloudAnchor:

                break;
            case R.id.untagBikeCloudAnchor:

                break;

            case R.id.viaMap:
                Intent mapsIntent= new Intent(MainActivity.this, MapsActivity.class);
              EditText editText= binding.getRoot().findViewById(R.id.chaiseNoEditText);
              String chas= editText.getText().toString().trim();
              if (chas.equalsIgnoreCase("")){
                  Toast.makeText(getApplicationContext(), new Gson().toJson(db.getAllBikes()), Toast.LENGTH_SHORT).show();

              }else{
                  Bike bike=db.getBike(chas);
                  Bundle bundle = new Bundle();
                  bundle.putSerializable("UniqueKey", bike);
                  mapsIntent.putExtras(bundle);
                  startActivity(mapsIntent);
              }

                break;
        }
    }
}