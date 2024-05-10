package com.example.billsplitter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MemberAvatarSpinnerAdapter extends ArrayAdapter<Integer> {

    MemberAvatarSpinnerAdapter(@NonNull Context context, @NonNull List<Integer> objects) {
        super(context, 0, objects);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }

    @SuppressWarnings({"ConstantConditions"})
    private View getCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.add_member_avatar_spinner, parent, false);
        }

        int avatarResource = getItem(position);

        ImageView imageView = convertView.findViewById(R.id.addMemberAvatarSpinnerImage);
        imageView.setImageResource(avatarResource); // set resource id to imageView reference

        return convertView;
    }
}
