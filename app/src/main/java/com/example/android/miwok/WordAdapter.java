package com.example.android.miwok;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;

public class WordAdapter extends ArrayAdapter<Word> {
    private int color;

    public WordAdapter(@NonNull Context context, @NonNull List<Word> objects, int color) {
        super(context, 0, objects);
        this.color = color;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView =
                LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Word currentWord = getItem(position);
        RelativeLayout textContainer = (RelativeLayout) listItemView.findViewById(R.id.texts);
        TextView englishWord = (TextView) listItemView.findViewById(R.id.englishWord);
        TextView miwokWord = (TextView) listItemView.findViewById(R.id.miwokWord);
        ImageView wordImage = (ImageView) listItemView.findViewById(R.id.wordImage);

        textContainer.setBackgroundColor(ContextCompat.getColor(getContext(), color));
        englishWord.setText(currentWord.getEnglishTranslation());
        miwokWord.setText(currentWord.getMiwokTranslation());
        if (currentWord.getImage() != 0) {
            wordImage.setImageResource(currentWord.getImage());
        } else {
            wordImage.setVisibility(View.GONE);
        }
        return listItemView;
    }
}
