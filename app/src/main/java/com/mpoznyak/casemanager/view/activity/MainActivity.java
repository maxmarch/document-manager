package com.mpoznyak.casemanager.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mpoznyak.casemanager.R;
import com.mpoznyak.casemanager.adapter.CaseAdapter;
import com.mpoznyak.casemanager.adapter.TypeAdapter;
import com.mpoznyak.casemanager.callback.RecyclerItemTouchHelper;
import com.mpoznyak.casemanager.presenter.MainPresenter;
import com.mpoznyak.domain.model.Case;
import com.mpoznyak.domain.model.Type;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {


    private static final int REQUEST_CODE = 1234;
    private ConstraintLayout mNoTypePosterLayout;
    private RecyclerView mCasesRecyclerView, mTypesRecyclerView;
    private CaseAdapter mCaseAdapter;
    private TypeAdapter mTypeAdapter;
    private TextView mNameToolbarTv;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBar actionBar;
    private Button mAddTypeButton;
    private FloatingActionButton mNewCaseBtn;
    private MainPresenter mMainPresenter;
    private String currentTypeName;
    private List<Case> mCases;
    private List<Type> mTypes;
    private Type mCurrentType;
    private int mCurrentTypePosition;
    private ItemTouchHelper.SimpleCallback mItemTouchHelperCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainPresenter = new MainPresenter(this);
        if (mMainPresenter.typeDataisEmpty()) {
            Intent welcomeIntent = new Intent(this, WelcomeActivity.class);
            startActivity(welcomeIntent);
        } else {
            setContentView(R.layout.activity_main);


            verifyPermissions();

            mNavigationView = findViewById(R.id.navigation);
            View header = mNavigationView.getHeaderView(0);
            mAddTypeButton = header.findViewById(R.id.main_add_type);
            mAddTypeButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, NewTypeActivity.class);
                startActivity(intent);
            });

            mNoTypePosterLayout = findViewById(R.id.noTypesPosterLayout);
            mCasesRecyclerView = findViewById(R.id.main_cases_recyclerview);
            mTypesRecyclerView = findViewById(R.id.main_types_recyclerview);
            mTypes = mMainPresenter.loadTypes();
            mTypeAdapter = new TypeAdapter(mTypes, new TypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view) {
                    int position = mTypesRecyclerView.getChildLayoutPosition(view);
                    mCurrentTypePosition = position;
                    mCurrentType = mTypes.get(position);
                    mCurrentType.setLastOpened(true);
                    mMainPresenter.updateType(mCurrentType);
                    List<Case> list = mMainPresenter.loadCasesBySelectedType(mCurrentType.getName());
                    mCases.clear();
                    mCases.addAll(list);
                    mCaseAdapter.notifyDataSetChanged();
                    currentTypeName = mCurrentType.getName();
                    if (currentTypeName.length() > 16) {
                        currentTypeName = currentTypeName.substring(0, 16) + "...";
                    }
                    mNameToolbarTv.setText(currentTypeName);
                    mDrawerLayout.closeDrawers();
                }
            });
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mTypesRecyclerView.setLayoutManager(linearLayoutManager);
            DividerItemDecoration divider = new DividerItemDecoration(mTypesRecyclerView.getContext(),
                    linearLayoutManager.getOrientation());
            mTypesRecyclerView.addItemDecoration(divider);
            mCases = mMainPresenter.loadCasesByLastOpenedType();
            mCaseAdapter = new CaseAdapter(mCases,
                    new CaseAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view) {
                            int position = mTypesRecyclerView.getChildLayoutPosition(view);
                            Case aCase = mCases.get(position);
                            int caseId = aCase.getId();
                            Intent intent = new Intent(getApplicationContext(), CaseActivity.class);
                            intent.putExtra("caseId", caseId);
                            startActivity(intent);
                        }
                    });
            mCasesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mCasesRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mCasesRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            mCasesRecyclerView.setAdapter(mCaseAdapter);
            mTypesRecyclerView.setAdapter(mTypeAdapter);

            mNameToolbarTv = findViewById(R.id.mainNameToolbarTv);
            mToolbar = findViewById(R.id.main_toolbar);
            setSupportActionBar(mToolbar);
            actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24px);

            mDrawerLayout = findViewById(R.id.main_drawer_layout);

            mNewCaseBtn = findViewById(R.id.fabNewCase);
            mNewCaseBtn.setOnClickListener(v -> {
                Intent newCaseIntent = new Intent(this, NewCaseActivity.class);
                newCaseIntent.putExtra("type", currentTypeName);
                startActivity(newCaseIntent);
            });

            ItemTouchHelper.SimpleCallback itemTouchHelperCallback
                    = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {


                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return true;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                }

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                }
            };

            mItemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
            new ItemTouchHelper(mItemTouchHelperCallback).attachToRecyclerView(mCasesRecyclerView);

        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if (!mMainPresenter.typeDataisEmpty()) {
            mTypeAdapter.notifyDataSetChanged();
            mCaseAdapter.notifyDataSetChanged();
            mCurrentType = mMainPresenter.loadLastOpenedType();
            for (int i = 0; i < mTypes.size(); i++) {
                if (mTypes.get(i).equals(mCurrentType))
                    mCurrentTypePosition = i;
            }
            currentTypeName = mCurrentType.getName();
            if (currentTypeName.length() > 16) {
                currentTypeName = currentTypeName.substring(0, 16) + "...";
            }
            mNameToolbarTv.setText(currentTypeName);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                mTypes.clear();
                mTypes.addAll(mMainPresenter.loadTypes());
                mTypeAdapter.notifyDataSetChanged();
                return true;
            case R.id.delete_type:
                if (mMainPresenter.typeDataisEmpty()) {
                    Toast.makeText(this, "You have not any type to delete"
                            , Toast.LENGTH_SHORT).show();
                    break;
                }
                List<Type> tempTypes = new ArrayList<>();
                mMainPresenter.deleteType(mCurrentType);
                if (mTypes.size() != 0) {
                    mTypes.remove(mCurrentTypePosition);
                } else {
                    Toast.makeText(this, "No types defined", Toast.LENGTH_SHORT).show();
                }
                tempTypes.addAll(mTypes);
                for (Type type : tempTypes) {
                    if (type != null) {
                        mCurrentType = type;
                        mCurrentType.setLastOpened(true);
                        mMainPresenter.updateType(mCurrentType);
                        mTypes.clear();
                        mTypes.addAll(tempTypes);
                        break;
                    }
                }
                if (mMainPresenter.typeDataisEmpty()) {
                    mNameToolbarTv.setText(null);
                    mNewCaseBtn.setVisibility(View.INVISIBLE);
                    mNoTypePosterLayout.setVisibility(View.VISIBLE);
                }
                onResume();
                break;
            case R.id.edit_type:
                if (mMainPresenter.typeDataisEmpty()) {
                    Toast.makeText(this, "You have not any type to edit"
                            , Toast.LENGTH_SHORT).show();
                    break;
                }
                Type type = mTypes.get(mCurrentTypePosition);
                Intent editIntent = new Intent(this, EditTypeActivity.class);
                editIntent.putExtra("typeId", type.getId());
                startActivity(editIntent);
                break;



        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CaseAdapter.CaseViewHolder) {
            final Case swippedCase = mCases.get(viewHolder.getAdapterPosition());
            String name = swippedCase.getName();

            final int deletedIndex = viewHolder.getAdapterPosition();

            mCaseAdapter.removeItem(viewHolder.getAdapterPosition());

            Snackbar snackbar = Snackbar
                    .make(mDrawerLayout, name + " removed from type!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mCaseAdapter.restoreItem(swippedCase, deletedIndex);
                }
            });
            snackbar.addCallback(new Snackbar.Callback() {

                @Override
                public void onDismissed(Snackbar callbackSnackbar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        mMainPresenter.deleteCase(swippedCase);
                    }
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }

    }

    private void verifyPermissions() {
        String[] permissions = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    REQUEST_CODE
            );
        }
    }
}
