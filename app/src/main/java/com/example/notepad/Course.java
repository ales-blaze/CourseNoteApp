package com.example.notepad;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Course extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "course_id")
    private int id;
    @ColumnInfo(name = "title")
    private String mTitle;
    @ColumnInfo(name = "description")
    private String mDescription;
    @Ignore
    private boolean isNew = false;

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @Bindable
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
        notifyPropertyChanged(BR.noteTitle);
    }
    @Bindable
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
        notifyPropertyChanged(BR.noteTitle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        if (getId() != course.getId()) return false;
        if (isNew() != course.isNew()) return false;
        if (mTitle != null ? !mTitle.equals(course.mTitle) : course.mTitle != null) return false;
        return mDescription != null ? mDescription.equals(course.mDescription) : course.mDescription == null;
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (mTitle != null ? mTitle.hashCode() : 0);
        result = 31 * result + (mDescription != null ? mDescription.hashCode() : 0);
        result = 31 * result + (isNew() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", mTitle='" + mTitle + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", isNew=" + isNew +
                '}';
    }
}
