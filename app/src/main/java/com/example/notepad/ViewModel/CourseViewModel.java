package com.example.notepad.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.notepad.CallBackInteface.CourseCallbacks;
import com.example.notepad.Course;
import com.example.notepad.Repository.CourseRepository;

import java.security.InvalidParameterException;
import java.util.List;

import static com.example.notepad.ViewModel.NoteViewModel.INVALID_ID;

public class CourseViewModel extends AndroidViewModel {
    private CourseRepository mRepository;
    private LiveData<List<Course>> mCourses;
    private static final String TAG = "CourseViewModel";
    public CourseViewModel(@NonNull Application application) {
        super(application);
        mRepository = new CourseRepository(application);
        mCourses = mRepository.getCourses();
    }

    public LiveData<List<Course>> getCourses() {
        return mCourses;
    }

    public void insert(Course course) {
         mRepository.insert(course);
    }

    public void update(Course course) {
        mRepository.update(course);
    }

    public void delete(Course course) {
        mRepository.deleteCourse(course);
    }

    public Course getCourse(int courseId) {
        return mRepository.getCourse(courseId);
    }

    @NonNull
    public int getCourseIdByTitle(@NonNull String courseTitle) {
        if (courseTitle == null) return INVALID_ID;
        return mRepository.getCourseIdByTitle(courseTitle);
    }

    public Course getCourseWithGivenTitleTask(String courseTitle) {
        if (courseTitle == null && courseTitle.isEmpty() ) {
            return new Course();
        }
        return mRepository.getCourseWithGivenTitle(courseTitle);
    }

    public void addCourseToCloud(Course course, CourseCallbacks callbacks , String userName) {
        if (course == null || callbacks == null || userName == null) {
            Log.e(TAG, "addCourseToCloud: Invalid Input to add Course to Cloud", new InvalidParameterException());
            return;
        }
        mRepository.addCourseToCloud(course,callbacks,userName);
    }

    public void getCoursesFromCloud(CourseCallbacks callbacks, String userName) {
        if (callbacks == null || userName == null) {
            Log.e(TAG, "getCoursesFromCloud: Invalid Input to get Courses from Cloud", new InvalidParameterException());
        }
        mRepository.getCoursesFromCloud(callbacks,userName);
    }

}
