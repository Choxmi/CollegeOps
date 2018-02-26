package com.choxmi.collegeops;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Choxmi on 12/22/2017.
 */

public class AttendanceRecyclerAdapter extends RecyclerView.Adapter<AttendanceRecyclerAdapter.ViewHolder> {
    Context context;
    ArrayList<Attendance> students;
    TextView name,id;
    CheckBox mark;

    public AttendanceRecyclerAdapter(Context context){
        this.context = context;
        students = FacAttendanceActivity.students;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View model = LayoutInflater.from(context).inflate(R.layout.attendance_item,parent,false);
        return new ViewHolder(model);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        name = (TextView)holder.itemView.findViewById(R.id.nameTxt);
        name.setText(students.get(position).getName());
        id = (TextView)holder.itemView.findViewById(R.id.idTxt);
        id.setText(students.get(position).getId());
        mark = (CheckBox)holder.itemView.findViewById(R.id.attChk);
        mark.setChecked(students.get(position).isMark());

        mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void onCheckboxClicked(View view,int pos) {

        boolean checked = ((CheckBox) view).isChecked();

        if(checked){
            Toast.makeText(context,"Checked",Toast.LENGTH_SHORT).show();
            students.get(pos).setMark(true);
        }else{
            Toast.makeText(context,"Not",Toast.LENGTH_SHORT).show();
            students.get(pos).setMark(false);
        }
    }
}
