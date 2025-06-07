package com.example.to_dolist.ui.in_progress;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.example.to_dolist.DBManager;
import com.example.to_dolist.TaskData;
import java.util.ArrayList;
import java.util.Set;

public class InProgressViewModel extends ViewModel {
    private MutableLiveData<ArrayList<TaskData>> tasksLiveData;
    private DBManager dbManager;

    public InProgressViewModel() {
        tasksLiveData = new MutableLiveData<>();
    }

    public void init(Context context) {
        if (dbManager == null) {
            dbManager = new DBManager(context);
            dbManager.open();
//            Log.d("debug13", "onCreate: ");
        }
        loadTasks();
    }

    public LiveData<ArrayList<TaskData>> getTasks() {
        return tasksLiveData;
    }

    private void loadTasks() {
        ArrayList<TaskData> taskList = new ArrayList<>();
        Cursor cursor = dbManager.fetch();
        if (cursor != null && cursor.getCount() > 0) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));

//                Log.d("DB_CHECK", "Fetched Task ID: " + id + ", Status: '" + status + "'");
                if ("in progress".equalsIgnoreCase(status)) {
                    taskList.add(new TaskData(id, title, description, status, category, date));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
//        Log.d("DB_CHECK", "Fetched Task Count: " + taskList.size());
        tasksLiveData.postValue(taskList);
    }

    public void deleteTasks(Set<Integer> taskIds) {
        if (dbManager != null) {
            for (int taskId : taskIds) {
                dbManager.delete(taskId);
            }
            loadTasks();
        }
    }

    public void moveTasks(Set<Integer> taskIds, String newStatus) {
        if (dbManager != null) {
            for (int taskId : taskIds) {
                dbManager.updateStatus(taskId, newStatus);
            }
            loadTasks();
        }
    }

    public void changeCategory(Set<Integer> taskIds, String newCategory) {
        if (dbManager != null) {
            for (int taskId : taskIds) {
                dbManager.updateCategory(taskId, newCategory);
            }
            loadTasks();
        }
    }

    public void refreshTasks() {
        loadTasks();
    }
}