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

import com.example.notepad.NoteWithCourse;
import com.example.notepad.Activities.NoteActivity;
import com.example.notepad.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>{
    private static final String TAG = "NoteAdapter";
    private Context context;
    private List<NoteWithCourse> noteWithCourses;

    public NoteAdapter(Context context, List<NoteWithCourse> noteWithCourses) {
        this.context = context;
        this.noteWithCourses = noteWithCourses;
    }
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_note_with_course_list, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        final NoteWithCourse noteWithCourse = noteWithCourses.get(position);
        if (noteWithCourse == null) {
            Log.e(TAG, "onBindViewHolder: Null Note And Course Object");
            return;
        }
        holder.courseTitle.setText(noteWithCourse.course.getTitle());
        holder.noteTitle.setText(noteWithCourse.note.getNoteTitle());
        holder.noteId = noteWithCourse.note.getId();
    }

    @Override
    public int getItemCount() {
        if (noteWithCourses != null)
            return noteWithCourses.size();
        return 0;
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView courseTitle;
        private TextView noteTitle;
        private int noteId;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.item_course_title);
            noteTitle = itemView.findViewById(R.id.item_note_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NoteActivity.class);
                    intent.putExtra("NOTE_ID", noteId);
                    context.startActivity(intent);
                }
            });
        }
    }
}
