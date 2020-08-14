package com.example.notepad.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.Activities.CourseActivity;
import com.example.notepad.Course;
import com.example.notepad.R;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<Course> mCourses;
    private Context context;

    public void setCourses(List<Course> mCourses) {
        this.mCourses = mCourses;
        notifyDataSetChanged();
    }

    public CourseAdapter(Context context , List<Course> courses) {
        this.context = context;
        mCourses = courses;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_list, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        if (mCourses != null && position < mCourses.size()) {
            Course course = mCourses.get(position);
            holder.courseTitleTextView.setText(course.getTitle());
            holder.courseId = course.getId();
        }else {
            Log.e("CourseAdapter", "Course List is empty");
        }
    }

    @Override
    public int getItemCount() {
        if (mCourses != null) {
            return mCourses.size();
        }
        return 0;
    }

    public class CourseViewHolder  extends RecyclerView.ViewHolder {
        private TextView courseTitleTextView;
        private int courseId;
        public CourseViewHolder(@NonNull final View itemView) {
            super(itemView);
            courseTitleTextView = itemView.findViewById(R.id.course_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CourseActivity.class);
                    intent.putExtra("COURSE_ID", courseId);
                    context.startActivity(intent);
                }
            });
        }
    }
}
