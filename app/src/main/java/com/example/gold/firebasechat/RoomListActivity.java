package com.example.gold.firebasechat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends AppCompatActivity {

    ListView listView;
    List<Room> datas = new ArrayList<>();
    ListAdapter adapter;

    FirebaseDatabase database;
    DatabaseReference roomRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        database = FirebaseDatabase.getInstance();
        roomRef = database.getReference("bbs");

        listView = (ListView) findViewById(R.id.listView);
        adapter = new ListAdapter(this, datas);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RoomListActivity.this, RoomActivity.class);
                Room room = datas.get(position);
                intent.putExtra("key", room.getKey());
                intent.putExtra("title", room.getTitle());
                startActivity(intent);
            }
        });

        roomRef.addValueEventListener(roomListener);

    }

    ValueEventListener roomListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            //Post post = dataSnapshot.getValue(Post.class);
            // ...
            Log.w("MainActivity","data count : " + dataSnapshot.getChildrenCount());

            datas.clear();

            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                Room room = new Room();
                String key = snapshot.getKey();
                room.setTitle(snapshot.getValue().toString());

                datas.add(room);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w("MainActivity", "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };

}



class ListAdapter extends BaseAdapter{

    Context context;
    List<Room> datas;
    LayoutInflater inflater;

    public ListAdapter(Context context, List<Room> datas){
        this.context = context;
        this.datas = datas;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_room_list, null);
        }

        Room room = datas.get(position);
        TextView roomTitle = (TextView) convertView.findViewById(R.id.roomTitle);
        roomTitle.setText(room.getTitle());

        return convertView;
    }
}
