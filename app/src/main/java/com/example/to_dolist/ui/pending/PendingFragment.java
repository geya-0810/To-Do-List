package com.example.to_dolist.ui.pending;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.to_dolist.Add_ModifyTask;
import com.example.to_dolist.BottomFunction;
import com.example.to_dolist.BottomNavListener;
import com.example.to_dolist.R;
import com.example.to_dolist.TasksAdapter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class PendingFragment extends Fragment implements BottomFunction {
    private RecyclerView recyclerView;
    private TasksAdapter adapter;
    private PendingViewModel pendingViewModel;
    private BottomNavListener bottomNavListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BottomNavListener) {
            bottomNavListener = (BottomNavListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement BottomFunction");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending, container, false);

        recyclerView = view.findViewById(R.id.lvTask);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TasksAdapter(new ArrayList<>(), task -> {
            Intent modifyIntent = new Intent(getContext(), Add_ModifyTask.class);
            modifyIntent.putExtra("action", "modify");
            modifyIntent.putExtra("id", task.getId());
            modifyIntent.putExtra("title", task.getTitle());
            modifyIntent.putExtra("description", task.getDescription());
            modifyIntent.putExtra("date", task.getDate());
            startActivity(modifyIntent);
        }, isEnabled -> {
            // Adapter notice us when multi-select mode is enabled or disabled
            if (isEnabled) {
                bottomNavListener.updateBottomNavToMultiSelectMode();// update bottom navigation to multi-select mode
            } else {
                bottomNavListener.resetBottomNav();
            }
        });
        recyclerView.setAdapter(adapter);

        pendingViewModel = new ViewModelProvider(this).get(PendingViewModel.class);
        pendingViewModel.init(getContext());
        //observe tasksLiveData
        pendingViewModel.getTasks().observe(getViewLifecycleOwner(), taskList -> {
            adapter.updateData(taskList);
            if (getActivity() instanceof AppCompatActivity) {
                Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("List quantity: " + taskList.size());
            }
        });
        return view;
    }

    public void deleteSelectedTasks() {
        Set<Integer> selectedTasks = adapter.getSelectedTasks();
        pendingViewModel.deleteTasks(selectedTasks);
        adapter.exitMultiSelectMode(); //exit multi-select mode after delete
    }

    @Override
    public void moveSelectedTasks(String newStatus) {
        Set<Integer> selectedTasks = adapter.getSelectedTasks();
        pendingViewModel.moveTasks(selectedTasks, newStatus);
        adapter.exitMultiSelectMode();
    }

    @Override
    public void changeCategory(String newCategory) {
        Set<Integer> selectedTasks = adapter.getSelectedTasks();
        pendingViewModel.changeCategory(selectedTasks, newCategory);
        adapter.exitMultiSelectMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        pendingViewModel.refreshTasks(); // refresh data when fragment is resumed
    }
}