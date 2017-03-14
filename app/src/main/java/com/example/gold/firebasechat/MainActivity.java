package com.example.gold.firebasechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText etId, etPw;
    Button btnLogin;

    FirebaseDatabase database;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance(); //데이터베이스와 연결
        userRef = database.getReference("user");   //데이터베이스의 레퍼런스(시작점, 노드) 설정

        etId = (EditText) findViewById(R.id.etId);
        etPw = (EditText) findViewById(R.id.etPw);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String id = etId.getText().toString();
                final String pw = etPw.getText().toString();

                //DB.1 파이어베이스로 child(id)레퍼런스에대한 쿼리를 날린다.
                userRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {

                    //DB.2 파이어베이스는 쿼리를 끝내면 스냅샷에 담아서 onDataChange를 콜백(호출)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //차일드가 있으면
                        if(dataSnapshot.getChildrenCount() > 0){
                            String fbPw = dataSnapshot.child("password").getValue().toString(); //getValue() -> 키말고 값 가져온다.
                            String name = dataSnapshot.child("name").getValue().toString();
                            Log.w("MainActivity","pw = " + fbPw);
                            if(fbPw.equals(pw)){
                                Intent intent = new Intent(MainActivity.this, RoomListActivity.class);
                                intent.putExtra("userid", id);
                                intent.putExtra("username", name);
                                startActivity(intent);
                            }else{
                                Toast.makeText(MainActivity.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_LONG).show();
                            }
                        }else {
                            Toast.makeText(MainActivity.this, "User가 없습니다", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
