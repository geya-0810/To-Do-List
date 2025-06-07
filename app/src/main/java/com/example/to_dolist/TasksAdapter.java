package com.example.to_dolist;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
    private ArrayList<TaskData> listData;
    private OnItemClickListener clickListener;
    private OnMultiSelectListener multiSelectListener;
    private Set<Integer> selectedTasks = new HashSet<>(); // store selected tasks id
    private boolean isMultiSelectMode = false; // is in multi-select mode or not

    public interface OnItemClickListener {
        void onItemClick(TaskData task);
    }

    public interface OnMultiSelectListener {
        void onMultiSelectModeChanged(boolean isEnabled);
    }

    public TasksAdapter(ArrayList<TaskData> listData, OnItemClickListener listener, OnMultiSelectListener multiSelectListener) {
        this.listData = (listData != null) ? listData : new ArrayList<>();
        this.clickListener = listener;
        this.multiSelectListener = multiSelectListener;
    }


    public TasksAdapter(ArrayList<TaskData> listData, OnItemClickListener listener) {
        this.listData = listData;
        this.clickListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TaskData task = listData.get(position);
        holder.txtId.setText(String.valueOf(position + 1));
        holder.txtTitle.setText(task.getTitle());
        holder.txtDescription.setText(task.getDescription());
        switch (task.getCategory()) {
            case "urgent":
                holder.ivCategory.setImageResource(R.drawable.urgent);
                break;
            case "important":
                holder.ivCategory.setImageResource(R.drawable.important);
                break;
            case "general":
                holder.ivCategory.setImageResource(R.drawable.general);
                break;
        }

        // change background color based on selection
        if (selectedTasks.contains(task.getId())) {
            holder.itemView.setBackgroundColor(Color.LTGRAY); // if selected, background is gray
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT); // if not selected, background is transparent
        }

        // check if in multi-select mode or not
        holder.itemView.setOnClickListener(v -> {
            if (isMultiSelectMode) {
                toggleSelection(task.getId());
            } else if (clickListener != null) {
                clickListener.onItemClick(task);
            }
        });

        // long click to change in multi-select mode
        holder.itemView.setOnLongClickListener(v -> {
            if (!isMultiSelectMode) {
                enterMultiSelectMode();
            }
            toggleSelection(task.getId());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void updateData(ArrayList<TaskData> newList) {
        this.listData = (newList != null) ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtId;
        public TextView txtTitle;
        public TextView txtDescription;
        public ImageView ivCategory;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.txtId = itemView.findViewById(R.id.id);
            this.txtTitle = itemView.findViewById(R.id.txtTitle);
            this.txtDescription = itemView.findViewById(R.id.txtDescription);
            this.ivCategory = itemView.findViewById(R.id.ivCategory);
            this.relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }

    //Enter multi-select mode
    public void enterMultiSelectMode() {
        isMultiSelectMode = true;
        if (multiSelectListener != null) {
            multiSelectListener.onMultiSelectModeChanged(true);
        }
    }

    // exit multi-select mode
    public void exitMultiSelectMode() {
        isMultiSelectMode = false;
        selectedTasks.clear();
        notifyDataSetChanged();
        if (multiSelectListener != null) {
            multiSelectListener.onMultiSelectModeChanged(false);
        }
    }

    // select and store multiple tasks or deselect task
    public void toggleSelection(int taskId) {
        if (selectedTasks.contains(taskId)) {
            selectedTasks.remove(taskId);
        } else {
            selectedTasks.add(taskId);
        }

        // if there are no selected tasks, exit multi-select mode
        if (selectedTasks.isEmpty()) {
            exitMultiSelectMode();
        } else {
            // if there are selected tasks, update the adapter
            notifyDataSetChanged();
        }
    }

    // get all selected tasks ID
    public Set<Integer> getSelectedTasks() {
        return selectedTasks;
    }
}
