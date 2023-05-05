package com.example.myfinal.Doctor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinal.Adapter.MsgAdapter;
import com.example.myfinal.Model.Msg;
import com.example.myfinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoctorSendMsg extends Fragment {

    private ImageView back, iv_send;
    private TextView tv_title;
    private EditText et_msg;
    private RecyclerView rcv;
    private CollectionReference msg_sender, msg_receiver;
    String patientID, msg, doctorID, senderRoom, receiverRoom;
    ArrayList<Msg> msgArrayList;
    MsgAdapter msgAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_send_msg, container, false);

        Bundle bundle = getArguments();
        String title = bundle.getString("name");
        patientID = bundle.getString("id_patient");

        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText(title);

        et_msg = (EditText) view.findViewById(R.id.et_msg);

        doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();

        receiverRoom = patientID + "_" + doctorID;
        senderRoom = doctorID + "_" + patientID;

        msg_receiver = FirebaseFirestore.getInstance().collection("Chat").document(receiverRoom).collection("Message");
        msg_sender = FirebaseFirestore.getInstance().collection("Chat").document(senderRoom).collection("Message");

        msg_sender.orderBy("date").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                ArrayList<Msg> msgArrayList1 = new ArrayList<>();
                for (DocumentSnapshot document : documentSnapshots) {
                    Msg msg1 = new Msg();

                    msg1.setMsg(document.getString("msg"));
                    msg1.setId_sender(document.getString("id_sender"));
                    msg1.setId_receiver(document.getString("id_receiver"));

                    msgArrayList1.add(msg1);
                }
                MsgAdapter msgAdapter1 = new MsgAdapter(getContext(), msgArrayList1);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);
                rcv.setLayoutManager(layoutManager);
                rcv.setLayoutManager(new LinearLayoutManager(getContext()));
                rcv.setAdapter(msgAdapter1);
            }
        });

        iv_send = (ImageView) view.findViewById(R.id.iv_send);
        iv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg = et_msg.getText().toString();

                Timestamp date = Timestamp.now();
                Map<String, Object> objectMap = new HashMap<>();
                objectMap.put("id_receiver", patientID);
                objectMap.put("id_sender", doctorID);
                objectMap.put("date", date);
                objectMap.put("msg", msg);
                et_msg.setText("");

                msg_sender.add(objectMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                msg_receiver.add(objectMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

                //                setUpRecyclerview();
            }
        });

        rcv = (RecyclerView) view.findViewById(R.id.rcv);

        back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoctorChatFragment doctorChatFragment = new DoctorChatFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_send_msg, doctorChatFragment).commit();
            }
        });

        return view;
    }

    public void setUpRecyclerview() {
        msg_sender.whereEqualTo("id_sender", doctorID).whereEqualTo("id_receiver", patientID).orderBy("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Msg> msgArrayList1 = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Msg msg1 = new Msg();

                        msg1.setMsg(documentSnapshot.getString("msg"));
                        msg1.setId_sender(documentSnapshot.getString("id_sender"));
                        msg1.setId_receiver(documentSnapshot.getString("id_receiver"));

                        msgArrayList1.add(msg1);
                    }
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true);
                    rcv.setLayoutManager(layoutManager);
                    msgAdapter = new MsgAdapter(getContext(), msgArrayList1);
                    rcv.setAdapter(msgAdapter);
                }
            }
        });

        msg_sender.whereEqualTo("id_sender", doctorID).whereEqualTo("id_receiver", patientID).orderBy("date").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                ArrayList<Msg> msgArrayList1 = new ArrayList<>();
                for (DocumentSnapshot document : documentSnapshots) {
                    Msg msg1 = new Msg();

                    msg1.setMsg(document.getString("msg"));
                    msg1.setId_sender(document.getString("id_sender"));
                    msg1.setId_receiver(document.getString("id_receiver"));

                    msgArrayList1.add(msg1);
                }
                MsgAdapter msgAdapter1 = new MsgAdapter(getContext(), msgArrayList1);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);
                rcv.setLayoutManager(layoutManager);
                msgAdapter1 = new MsgAdapter(getContext(), msgArrayList1);
                rcv.setAdapter(msgAdapter1);
            }
        });
    }
}