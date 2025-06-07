package com.example.to_dolist;

public interface BottomFunction {   //interface for bottom navigation of function
    void deleteSelectedTasks();
    void moveSelectedTasks(String newStatus);
    void changeCategory(String newCategory);
}
