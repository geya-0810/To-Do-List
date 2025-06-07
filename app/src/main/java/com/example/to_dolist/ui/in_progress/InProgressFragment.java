package com.example.to_dolist.ui.in_progress;

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

public class InProgressFragment extends Fragment implements BottomFunction {
    private RecyclerView recyclerView;
    private TasksAdapter adapter;
    private InProgressViewModel inProgressViewModel;
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
        View view = inflater.inflate(R.layout.fragment_in_progress, container, false);

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
            if (isEnabled) {
                bottomNavListener.updateBottomNavToMultiSelectMode();
            } else {
                bottomNavListener.resetBottomNav();
            }
        });
        recyclerView.setAdapter(adapter);

        inProgressViewModel = new ViewModelProvider(this).get(InProgressViewModel.class);
        inProgressViewModel.init(getContext());
        inProgressViewModel.getTasks().observe(getViewLifecycleOwner(), taskList -> {
            adapter.updateData(taskList);
            if (getActivity() instanceof AppCompatActivity) {
                Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("List quantity: " + taskList.size());
            }
        });
        return view;
    }

    public void deleteSelectedTasks() {
        Set<Integer> selectedTasks = adapter.getSelectedTasks();
        inProgressViewModel.deleteTasks(selectedTasks);
        adapter.exitMultiSelectMode();
    }

    @Override
    public void moveSelectedTasks(String newStatus) {
        Set<Integer> selectedTasks = adapter.getSelectedTasks();
        inProgressViewModel.moveTasks(selectedTasks, newStatus);
        adapter.exitMultiSelectMode();
    }

    @Override
    public void changeCategory(String newCategory) {
        Set<Integer> selectedTasks = adapter.getSelectedTasks();
        inProgressViewModel.changeCategory(selectedTasks, newCategory);
        adapter.exitMultiSelectMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        inProgressViewModel.refreshTasks();
    }
}