package br.edu.ifspsaocarlos.agendafirebase.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.Query;

import br.edu.ifspsaocarlos.agendafirebase.R;
import br.edu.ifspsaocarlos.agendafirebase.adapter.ContatoFirebaseAdapter;
import br.edu.ifspsaocarlos.agendafirebase.utils.Constants;

public class BaseActivity extends AppCompatActivity {


    public ListView list;
    protected SearchView searchView;
    Firebase myFirebaseRef;
    ContatoFirebaseAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list = (ListView) findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2,
                                    long arg3) {

                Intent inte = new Intent(getApplicationContext(), DetalheActivity.class);

                inte.putExtra("FirebaseID", mAdapter.getRef(arg2).getKey());
                startActivityForResult(inte, 0);
            }
        });


        registerForContextMenu(list);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.pesqContato).getActionView();

        Log.d("Agenda", "Menu " + searchManager.getSearchableInfo(getComponentName()));

//        ComponentName cn = new ComponentName(getApplicationContext(), SearchActivity.class);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setIconifiedByDefault(true);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater m = getMenuInflater();
        m.inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ContatoFirebaseAdapter adapter = (ContatoFirebaseAdapter) list.getAdapter();

        String FirebaseID = adapter.getRef(info.position).getKey().toString();


        switch (item.getItemId()) {
            case R.id.delete_item:
                myFirebaseRef.child(FirebaseID).removeValue();
                Toast.makeText(getApplicationContext(), "Removido com sucesso", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onContextItemSelected(item);
    }


    protected void buildListView() {
        mAdapter = new ContatoFirebaseAdapter(this,  myFirebaseRef);
        list.setAdapter(mAdapter);
    }

    protected void buildSearchListView(String query) {
        Query queryRef = myFirebaseRef.orderByChild("nome").startAt(query).endAt(query +"\uf8ff");
        mAdapter = new ContatoFirebaseAdapter(this,  queryRef);
        list.setAdapter(mAdapter);

        Log.d("Agenda", "Query " + query);
    }


}
