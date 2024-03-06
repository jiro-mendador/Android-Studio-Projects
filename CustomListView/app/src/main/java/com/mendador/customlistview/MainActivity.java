package com.mendador.customlistview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int[] images = {R.drawable.french_fries, R.drawable.hamburger, R.drawable.fried_chicken,
            R.drawable.soft_drink,R.drawable.pasta, R.drawable.ice_cream};
    String[] imagesDescription = {"French Fries","Hamburger","Fried Chicken",
            "Soft Drink","Spaghetti","Ice Cream"};
    String[] imageSubDescription = {"P 50.00","P 100.00","P 150.00","P 75.00","P 99.99","P 80.00"};

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
            TextView imageName = (TextView) view.findViewById(R.id.tvFoodName);
            TextView imageSubDesc = (TextView) view.findViewById(R.id.tvFoodPrice);

            imageHolder.setImageResource(images[i]);
            imageName.setText(imagesDescription[i].toString());
            imageSubDesc.setText(imageSubDescription[i].toString());

            return view;
        }
    }
}