package com.example.to_dolist;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.to_dolist.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements BottomNavListener {

    private ActivityMainBinding binding;
    private DBManager dbManager;
    private ListView listView;
    private SimpleCursorAdapter adapter;
    private Cursor cursor;
    final String[] from = new String[] { DatabaseHelper._ID, DatabaseHelper.TITLE, DatabaseHelper.DESCRIPTION };

    final int[] to = new int[] { R.id.id, R.id.txtTitle, R.id.txtDescription};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_pending, R.id.navigation_inProgress, R.id.navigation_completed)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // add task depend the page use at
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (dbManager == null) {
                dbManager = new DBManager(this);
                dbManager.open();
//                Log.d("debug11", "onCreate: ");
            }
            Intent intent = new Intent(MainActivity.this, Add_ModifyTask.class);
            intent.putExtra("action", "add");
            binding.btnAdd.setOnClickListener(view -> {
                if(destination.getId() == R.id.navigation_pending) {    //PendingFragment
                    intent.putExtra("status", "pending");
                    startActivity(intent);

                }else if(destination.getId() == R.id.navigation_inProgress) {   //InProgressFragment
                    intent.putExtra("status", "in progress");
                    startActivity(intent);

                }else if(destination.getId() == R.id.navigation_completed) {    //CompletedFragment
                    intent.putExtra("status", "completed");
                    startActivity(intent);

                }
            });
        });
    }

    @Override
    public void updateBottomNavToMultiSelectMode() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.getMenu().clear();
        navView.inflateMenu(R.menu.long_click_nav_menu);

        // get current fragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        if (navHostFragment != null) {
            Fragment currentFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();

            if (currentFragment == null) {
                return;  // prevent NullPointerException
            }

            // remove previous listener to prevent duplicate calls
            navView.setOnItemSelectedListener(null);

            navView.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.navigation_delete) {
                    confirmDelete(currentFragment);
                } else if (item.getItemId() == R.id.navigation_moveTo) {
                    showMoveToMenu(findViewById(R.id.navigation_moveTo), currentFragment);
                } else if (item.getItemId() == R.id.navigation_changeTo) {
                    showChangeCategoryMenu(findViewById(R.id.navigation_changeTo), currentFragment);
                }
                return true;
            });
        }
    }

    private void confirmDelete(Fragment currentFragment) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete the selected tasks?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (currentFragment instanceof BottomFunction) {
                        ((BottomFunction) currentFragment).deleteSelectedTasks();
                    }
                    resetBottomNav();  // delete when user confirm
                })
                .setNegativeButton("No", null)  // if user click no, do nothing
                .show();
    }

    private void showMoveToMenu(View anchor, Fragment currentFragment) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.status_list, popup.getMenu());

        popup.setOnMenuItemClickListener(menuItem -> {
            String newStatus = null;

            int itemId = menuItem.getItemId();
            if (itemId == R.id.navigation_move_to_pending) {
                newStatus = "pending";
            } else if (itemId == R.id.navigation_move_to_in_progress) {
                newStatus = "in progress";
            } else if (itemId == R.id.navigation_move_to_completed) {
                newStatus = "completed";
            } else if (itemId == R.id.status_list_close) {
                popup.dismiss();
                return true;
            }

            if (newStatus != null && currentFragment instanceof BottomFunction) {
                ((BottomFunction) currentFragment).moveSelectedTasks(newStatus);
            }
            return true;
        });
        popup.show();
    }

    private void showChangeCategoryMenu(View anchor, Fragment currentFragment) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.category_list, popup.getMenu());

        popup.setOnMenuItemClickListener(menuItem -> {
            String newCategory = null;

            int itemId = menuItem.getItemId();
            if (itemId == R.id.change_to_urgent) {
                newCategory = "urgent";
            } else if (itemId == R.id.change_to_important) {
                newCategory = "important";
            } else if (itemId == R.id.change_to_general) {
                newCategory = "general";
            } else if (itemId == R.id.category_list_close) {
                popup.dismiss();
                return true;
            }

            if (newCategory != null && currentFragment instanceof BottomFunction) {
                ((BottomFunction) currentFragment).changeCategory(newCategory);
            }
            return true;
        });
        popup.show();
    }

    @Override
    public void resetBottomNav() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        if (navView != null) {
            navView.getMenu().clear();
            navView.inflateMenu(R.menu.bottom_nav_menu);
            // Rebind the navigation so that the selected item is in sync with the current Fragment
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            NavigationUI.setupWithNavController(navView, navController);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dbManager != null) {
            dbManager.close();
//            Log.d("debug3", "onPause: ");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dbManager != null) {
            dbManager.close();
//            Log.d("debug2", "onStop: ");
        }
    }
}