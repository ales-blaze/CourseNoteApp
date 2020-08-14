package com.example.notepad.CallBackInteface;

import com.example.notepad.Course;

import java.util.List;

public interface CourseCallbacks {
    void CourseUploadFailed(Exception e);
    void errorFetchingCourse();
    void addCourseToLocalDatabase(List<Course> courses);
    void courseUploadSucceeded(String referenceId);
}
