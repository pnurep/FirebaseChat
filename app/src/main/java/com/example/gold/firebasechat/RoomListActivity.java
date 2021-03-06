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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomListActivity extends AppCompatActivity {

    Button btnMakeRoom;
    EditText etRoomName;
    ListView listView;
    List<Room> datas = new ArrayList<>();
    ListAdapter adapter;

    // 데이터베이스 연결
    FirebaseDatabase database;
    DatabaseReference roomRef;

    String userid;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        Intent intent = getIntent();
        userid = intent.getExtras().getString("userid");
        username = intent.getExtras().getString("username");

        database = FirebaseDatabase.getInstance();
        roomRef = database.getReference("room");

        etRoomName = (EditText) findViewById(R.id.etRoomName);

        btnMakeRoom = (Button) findViewById(R.id.btnMakeRoom);
        btnMakeRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomName = etRoomName.getText().toString();
                DatabaseReference addRef = roomRef.child(roomName);
                String key = roomRef.getKey();
                Map<String, String> roomMap = new HashMap<>();
                roomMap.put(key, roomName);
                roomRef.setValue(roomMap);
            }
        });


        listView = (ListView) findViewById(R.id.listView);
        adapter = new ListAdapter(this, datas);
        listView.setAdapter(adapter);

        // 아이템이 클릭되면 그걸 체크해서 RoomActivity로 넘긴다.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Room room = datas.get(position);
                Intent intent = new Intent(RoomListActivity.this, RoomActivity.class);
                intent.putExtra("key",room.getKey());
                intent.putExtra("title",room.getTitle());
                intent.putExtra("userid",userid);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });

        roomRef.addValueEventListener(roomListener);
    }

    ValueEventListener roomListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //clear()를 하지않으면 기존에 있던 데이터에 snapshot형태의 뭉텅이 데이터가 붙어버리므로
            // 기존의 datas를 clear 해준다
            datas.clear();
            for( DataSnapshot snapshot : dataSnapshot.getChildren() ){
                Room room = new Room();
                room.setKey(snapshot.getKey());
                room.setTitle(snapshot.getValue().toString());

                datas.add(room);
            }
            adapter.notifyDataSetChanged(); // 어댑터에 변경되었다 알리면 어댑터는 화면을 갱신해준다.
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w("RoomListActivity", "roomListener:onCancelled", databaseError.toException());
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
        if(convertView == null)
            convertView = inflater.inflate(R.layout.item_room_list, null);

        Room room = datas.get(position);
        TextView roomTitle = (TextView) convertView.findViewById(R.id.roomTitle);
        roomTitle.setText(room.getTitle());
        return convertView;
    }
}
