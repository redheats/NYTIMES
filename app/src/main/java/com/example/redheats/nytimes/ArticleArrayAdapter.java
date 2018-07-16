package com.example.redheats.nytimes;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    public ArticleArrayAdapter(Context context, List<Article> articles){
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get data item for position
        Article article = this.getItem(position);
        //check if the view is being reuse
        //if not using recycler view, inflate layout
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.article, parent, false);
        }
        //find views
        ImageView article_thumbnail = convertView.findViewById(R.id.thumbnail);
        TextView article_headline = convertView.findViewById(R.id.headline);
        TextView article_snippet = convertView.findViewById(R.id.snippet);
        //clear recycled image from convert view from last time
        article_thumbnail.setImageResource(0);

        article_headline.setText(article.getHeadline());
        article_snippet.setText(article.getSnippet());

        String thumbnail = article.getThumbnail();
        //if(!TextUtils.isEmpty(thumbnail)){
        //    Picasso.get()
        //            .load(Uri.parse(thumbnail))
        //            .into(article_thumbnail);
        //}
        if(!TextUtils.isEmpty(thumbnail)){
            article_thumbnail.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load(Uri.parse(thumbnail))
                    .error(Glide.with(getContext()).load(android.R.drawable.ic_menu_report_image))
                    .into(article_thumbnail);
        }
        else {
            article_thumbnail.setVisibility(View.GONE);
        }
        return convertView;
    }
}
