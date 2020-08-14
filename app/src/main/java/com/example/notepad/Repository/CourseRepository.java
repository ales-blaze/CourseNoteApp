package com.example.notepad.Repository;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.notepad.CallBackInteface.CourseCallbacks;
import com.example.notepad.CourseRoomDatabase;
import com.example.notepad.Dao.CourseDao;
import com.example.notepad.Course;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CourseRepository {
    private static final String TAG = "CourseRepository";
    private LiveData<List<Course>> mCourses;
    private final CourseDao mCourseDao;
    private final Context context;

    public CourseRepository(Application application) {
        mCourseDao = CourseRoomDatabase.getDatabase(application).getCourseDao();
        context = application.getApplicationContext();
        mCourses = mCourseDao.getAllCourses();
    }

    public LiveData<List<Course>> getCourses() {
        return mCourses;
    }

    public void insert(Course course ) {
        try {
            new insertTask(mCourseDao).execute(course).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class insertTask extends AsyncTask<Course,Void,Void>{
        CourseDao mCourseDao;
        insertTask(CourseDao courseDao) {
            mCourseDao = courseDao;
        }
        @Override
        protected Void doInBackground(Course... courses) {
            mCourseDao.insert(courses[0]);
            return null;
        }
    }

    public void update(Course course ) {
        try {
            new updateTask(mCourseDao).execute(course).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class updateTask extends AsyncTask<Course,Void,Boolean>{
        private CourseDao mCourseDao;
        updateTask(CourseDao courseDao) {
            mCourseDao = courseDao;
        }
        @Override
        protected Boolean doInBackground(Course... courses) {
            mCourseDao.update(courses[0]);
            return null;
        }
    }

    public void deleteCourse(Course course) {
        try {
            new deleteTask(mCourseDao).execute(course).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class deleteTask extends AsyncTask<Course,Void,Boolean>{
        private CourseDao mCourseDao;
        deleteTask(CourseDao courseDao) {
            mCourseDao = courseDao;
        }
        @Override
        protected Boolean doInBackground(Course... courses) {
            mCourseDao.deleteNoteWithCourseId(courses[0].getId());
            mCourseDao.delete(courses[0]);
            return null;
        }
    }

    public Course getCourse(int courseId) {
        Course course = new Course();
        try {
            course = new getCourseById(mCourseDao).execute(courseId).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return course;
    }

    private class getCourseById extends AsyncTask<Integer,Void,Course>{
        private CourseDao mCourseDao;
        getCourseById(CourseDao courseDao) {
            mCourseDao = courseDao;
        }

        @Override
        protected Course doInBackground(Integer... integers) {
            return mCourseDao.getCourse(integers[0]);
        }
    }

    public int getCourseIdByTitle(String courseTitle) {
        try {
            return new getCourseIdByTitleTask(mCourseDao).execute(courseTitle).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    static class getCourseIdByTitleTask extends AsyncTask<String,Void,Integer> {
        private final CourseDao courseDao;
        getCourseIdByTitleTask(CourseDao courseDao) {
            this.courseDao = courseDao;
        }
        @Override
        protected Integer doInBackground(String... courseTitles) {
            return courseDao.getCourseIdByTitle(courseTitles[0]);
        }
    }

    public Course getCourseWithGivenTitle(@NonNull String title) {
        Course course = new Course();
        try {
            course = new getCourseWithGivenTitleTask(mCourseDao).execute(title).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return course;
    }

    static class getCourseWithGivenTitleTask extends AsyncTask<String,Void,Course> {
        private final CourseDao courseDao;
        getCourseWithGivenTitleTask(CourseDao courseDao) {
            this.courseDao = courseDao;
        }
        @Override
        protected Course doInBackground(String... courseTitles) {
            return courseDao.getCourseWithGivenTitle(courseTitles[0]);
        }
    }

    public void addCourseToCloud(@NonNull Course course , final CourseCallbacks callbacks , String userName) {
        if (course == null) {
            Log.d(TAG, "addToCloud: Invalid Course Input");
            return;
        }
        FirebaseFirestore.getInstance().
                collection(userName + "-course")
                .add(course)
                .addOnSuccessListener(documentReference -> {
                    callbacks.courseUploadSucceeded(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    callbacks.CourseUploadFailed(e);
                });
    }

    public void getCoursesFromCloud(final CourseCallbacks courseCallbacks,String userName) {
        final List<Course> backupCourses = new ArrayList<>();
        FirebaseFirestore.getInstance().collection(userName+"-course")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Course course = document.toObject(Course.class);
                            backupCourses.add(course);
                        }
                        courseCallbacks.addCourseToLocalDatabase(backupCourses);
                    } else {
                        courseCallbacks.errorFetchingCourse();
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

}
