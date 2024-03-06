package com.depantecustomlistview;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    int[] images = {R.drawable.pslogo, R.drawable.idlogo, R.drawable.xdlogo,
            R.drawable.lrlogo,R.drawable.aelogo, R.drawable.anlogo};
    String[] imagesDescription = {"Photoshop","InDesign","XD",
            "Lightroom","After Effects","Animate"};
    String[] imageSubDescription = {"a versatile image editing software for enhancing, retouching, and manipulating digital images",
            "for designing and publishing print and digital materials",
            "a user experience and user interface design tool",
            "a comprehensive photo editing and management software for photographers",
            "used in video post-production for adding special effects and animations to videos",
            "for creating interactive animations, multimedia content, and vector-based graphics"};

    ListView lvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        CustomAdapter adapter = new CustomAdapter();
        lvList.setAdapter(adapter);
    }

    void init() {
        lvList = (ListView) findViewById(R.id.lvLists);
    }

    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = getLayoutInflater().inflate(R.layout.custom_listview_layout,null);
            ImageView imageHolder = (ImageView) view.findViewById(R.id.ivImgHolder);
            TextView imageName = (TextView) view.findViewById(R.id.tvName);
            TextView imageSubDesc = (TextView) view.findViewById(R.id.tvDesc);

            imageHolder.setImageResource(images[i]);
            imageName.setText(imagesDescription[i].toString());
            imageSubDesc.setText(imageSubDescription[i].toString());

            return view;
        }
    }
}