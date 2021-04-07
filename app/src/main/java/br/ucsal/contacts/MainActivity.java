package br.ucsal.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import br.ucsal.contacts.adapter.RecycleViewAdapter;
import br.ucsal.contacts.adapter.ViewPageAdapter;
import br.ucsal.contacts.fragments.FragmentListSortedById;
import br.ucsal.contacts.models.Contact;
import br.ucsal.contacts.controller.ContactController;

public class MainActivity extends AppCompatActivity implements RecycleViewAdapter.OnContactClickListener {

    private static final int NEW_CONTACT_ACTIVITY_REQUEST_CODE = 1;
    private static final String TAG = "Clicked";
    public static final String CONTACT_ID = "contact_id";
    private ContactController contactViewModel;
    private RecyclerView recyclerView;
    private RecycleViewAdapter recyclerViewAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FragmentListSortedById fragmentListSortedById;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sec);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("University of Agriculture Faisalabad");
        // todo colocar a lista nos TABs
        // recyclerView = findViewById(R.id.recycler_view);

        fragmentListSortedById = new FragmentListSortedById();
        tabLayout.setupWithViewPager(this.viewPager);
        ViewPageAdapter viewPagerAdapter = new ViewPageAdapter(getSupportFragmentManager(), 0);
        viewPager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.addFragment(fragmentListSortedById);

        contactViewModel = new ViewModelProvider.AndroidViewModelFactory(MainActivity.this
                .getApplication())
                .create(ContactController.class);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactViewModel.index().observe(this, contacts -> {
            recyclerViewAdapter = new RecycleViewAdapter(contacts, MainActivity.this, this);
            recyclerView.setAdapter(recyclerViewAdapter);

        });



        FloatingActionButton fab = findViewById(R.id.add_contact_fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewContact.class);
            startActivityForResult(intent, NEW_CONTACT_ACTIVITY_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_CONTACT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            String name = data.getStringExtra(NewContact.NAME_REPLY);
            String phone = data.getStringExtra(NewContact.PHONE);
            String age = data.getStringExtra(NewContact.AGE);
            assert name != null;
            Contact contact = new Contact(name, phone, Integer.parseInt(age));
            ContactController.store(contact);
        }
    }

    @Override
    public void onContactClick(int position) {
        Contact contact = Objects.requireNonNull(contactViewModel.contacts.getValue()).get(position);
        Log.d(TAG, "onContactClick: " + contact.getId());
        Intent intent = new Intent(MainActivity.this, NewContact.class);
        intent.putExtra(CONTACT_ID, contact.getId());
        startActivity(intent);
    }


}